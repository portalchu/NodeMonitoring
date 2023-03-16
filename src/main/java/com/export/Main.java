package com.export;

import com.jcraft.jsch.JSchException;
import jdk.javadoc.internal.doclets.toolkit.Resources;
import org.hyperledger.indy.sdk.IndyException;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.Scanner;

public class Main {

    public static NodeMonitoring indyNodeManager = new NodeMonitoring();
    public static Connection connection;

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        int num = 0;

        String walletId;
        String walletPw;
        int n;

        String command;

        try {
            while (true)
            {
                System.out.println("0 : 모니터링데이터설정");
                System.out.println("1 : 풀과 연결");
                System.out.println("2 : 지갑 생성 및 열기");
                System.out.println("3 : 지갑 열기");
                System.out.println("4 : Trustee DID 생성");
                System.out.println("5 : DID 생성");
                System.out.println("@6 : ssh 파일 다운 테스트");
                System.out.println("@7 : 외부 노드 테스트");
                System.out.println("8 : 노드 추가 요청");
                System.out.println("9 : 리소스 읽기");
                System.out.println("10 : 노드 모니터링 시스템 실행");

                System.out.println("번호 입력 : ");
                num = sc.nextInt();

                switch (num) {
                    case 0:
                        indyNodeManager.CreateMonitoringData();
                        break;
                    case 1:
                        indyNodeManager.ConnectIndyPool();
                        break;
                    case 2:
                        System.out.println("지갑 이름 : ");
                        walletId = sc.next();
                        System.out.println("지갑 비번 : ");
                        walletPw = sc.next();
                        indyNodeManager.createWallet(walletId, walletPw);
                        break;
                    case 3:
                        System.out.println("지갑 이름 : ");
                        walletId = sc.next();
                        System.out.println("지갑 비번 : ");
                        walletPw = sc.next();
                        indyNodeManager.openWallet(walletId, walletPw);
                        break;
                    case 4:
                        indyNodeManager.createTrusteeDid();
                        break;
                    case 5:
                        indyNodeManager.createDid();
                        break;
                    case 6:
                        try {
                            if (connection == null) {
                                connection = new Connection("root", "220.68.5.139",
                                        22, "umcl123456789");
                            }
                            connection.connectSSHServer();
                            connection.connectChannelSftp();

                            String path = "/root";
                            String fileName = "pool_transactions_genesis";
                            //String userPath = Main.class.getClassLoader().getResource("clientIP.json").getPath();
                            String userPath = new File("").getAbsolutePath();
                            System.out.println("userPath : " + userPath);
                            connection.download(path, fileName, userPath);

                        } finally {
                            connection.disConnectSSH();
                        }
                        break;
                    case 7:
                        System.out.println("case 7");
                        indyNodeManager.ConnectIndyPool();
                        walletId = "wallet" + (Math.random() * 1000);
                        walletPw = "1234";
                        indyNodeManager.createWallet(walletId, walletPw);
                        indyNodeManager.createTrusteeDid();
                        indyNodeManager.createDid();
                        indyNodeManager.StartNodeMonitoring();
                        break;
                    case 8:
                        indyNodeManager.AddNodeListCheck();
                        break;
                    case 9:
                        indyNodeManager.GetResourceFile();
                        break;
                    case 10:
                        System.out.println("case 7");
                        indyNodeManager.ConnectIndyPool();
                        walletId = "wallet" + (Math.random() * 1000);
                        walletPw = "1234";
                        indyNodeManager.createWallet(walletId, walletPw);
                        indyNodeManager.createTrusteeDid();
                        indyNodeManager.createDid();
                        indyNodeManager.StartNodeMonitoring();
                        return;
                    default:
                        System.out.println("잘못된 입력 값");
                        break;
                }

            }

        }catch (IndyException e) {
            System.out.println("IndyException : " + e);
        }
        catch (Exception e) {
            System.out.println("Exception : " + e);
        }

        //indyNodeManager.RunIndyContainer();
    }


}