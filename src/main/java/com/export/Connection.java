package com.export;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Connection {
    private String username;
    private String host;
    private int port;
    private String password;

    private Session session;
    private ChannelExec channelExec;

    Scanner sc = new Scanner(System.in);

    Connection(String username, String host, int port, String password) {
        this.username = username;
        this.host = host;
        this.port = port;
        this.password = password;
    }

    public void connectSSH() {
        try {
            session = new JSch().getSession(username, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            System.out.println("ssh session connect Success");

            while (true) {
                if (session == null) {
                    System.out.println("ssh session is null");
                    break;
                }

                String command = sc.nextLine();
                System.out.println("커맨드 입력");
                command = sc.nextLine();
                System.out.println("command : " + command);

                if(command == null || command.trim().equals("")) {
                    System.out.println("ssh command is null");
                    break;
                }

                command(command);
            }

        } catch (JSchException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.disConnectSSH();
        }
    }

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
                    Thread.sleep(100);
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

    public void disConnectSSH() {
        System.out.println("ssh session disConnect");
        if (session != null) session.disconnect();
        if (channelExec != null) channelExec.disconnect();
    }
}
