package com.export;

import com.jcraft.jsch.*;

import java.io.*;
import java.util.Scanner;

// 외부 컴퓨터 조작을 위한 클래스
// 기본적으로 ssh 및 scp를 사용하므로 각각 설치 필요
public class Connection {
    private String userName;
    private String host;
    private int port;
    private String password;

    private Session session;
    private ChannelExec channelExec;
    private Channel channel;
    private ChannelSftp channelSftp;

    Scanner sc = new Scanner(System.in);

    // Connection 생성자
    // @입력 값
    // userName: 연결할 컴퓨터의 사용자 이름
    // host: 연결할 컴퓨터의 IP 주소
    // port: 연결할 컴퓨터의 Port 번호 (기본 22 필요)
    // passwork: ssh 연결을 위한 비밀번호
    Connection(String userName, String host, int port, String password) {
        this.userName = userName;
        this.host = host;
        this.port = port;
        this.password = password;
    }

    // SSH 연결
    // Connection 생성자에서 입력된 값을 통해 SSH Server와 연결
    public Session connectSSHServer() throws Exception {
        System.out.println("==== connect ssh ====");
        session = new JSch().getSession(userName, host, port);
        session.setPassword(password);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();
        System.out.println("ssh session connect Success");

        return session;
    }

    public void connectChannelSftp() throws Exception {
        System.out.println("==== connect Channel Sftp ====");
        channel = session.openChannel("sftp");
        channel.connect();

        channelSftp = (ChannelSftp) channel;
    }

    // command 실행문
    // @입력 값
    // command: 외부 컴퓨터에서 실행할 명령어 입력
    public void command(String command) throws Exception {
        ChannelExec channelExec = null;
        ByteArrayOutputStream responseStream = null;
        //BufferedReader commandReader = null;
        try {
            channelExec = (ChannelExec) session.openChannel("exec");

            //commandReader = new BufferedReader(new InputStreamReader(channelExec.getInputStream()));

            responseStream = new ByteArrayOutputStream();
            channelExec.setOutputStream(responseStream);

            channelExec.setCommand(command);
            channelExec.connect();

                while (channelExec.isConnected()) {
                    Thread.sleep(3000);
                }

            String responseString = new String(responseStream.toByteArray());

            /*
            String commandLine = commandReader.readLine();
            if(commandLine == null || commandLine.equals("")) {
                commandLine = "Fail";
            }
            System.out.println(" command Result String -> [ " + commandLine + " ] ");
             */
            System.out.println(responseString);

        } finally {
            responseStream.close();
            //commandReader.close();
            channelExec.disconnect();
        }
    }

    // scp를 사용한 파일 다운로드
    // scp를 사용하여 외부 컴퓨터에서 특정 파일을 요청해 다운로드
    // @입력 값
    // path: 외부 컴퓨터에서 실행할 명령어 입력
    // fileName: 외부 컴퓨터에서 실행할 명령어 입력
    // userPath: 
    public void download(String path, String fileName, String userPath) throws Exception {
        System.out.println("==== download ====");
        InputStream in = null;
        FileOutputStream out = null;
        try {
            channelSftp.cd(path);
            in = channelSftp.get(fileName);

            String fullpath = userPath + File.separator + fileName;
            System.out.println("fullpath : " + fullpath);

            out = new FileOutputStream(new File(fullpath));
            int i;

            while ((i = in.read()) != -1) {
                out.write(i);
            }

        } finally {
            out.close();
            in.close();
        }
    }

    public void disConnectSSH() {
        System.out.println("ssh session disConnect");
        if (session != null) session.disconnect();
        if (channelExec != null) channelExec.disconnect();
    }
}
