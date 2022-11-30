package com.export;

import org.hyperledger.indy.sdk.IndyException;

import java.util.Scanner;


public class Main {

    public static NodeMonitoring indyNodeManager = new NodeMonitoring();

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        int num = 0;

        String walletId;
        String walletPw;

        try {
            while (true)
            {
                System.out.println("0 : 서버 IP 확인");
                System.out.println("1 : 풀과 연결");
                System.out.println("2 : 지갑 생성 및 열기");
                System.out.println("3 : 지갑 열기");
                System.out.println("4 : Trustee DID 생성");
                System.out.println("5 : DID 생성");
                System.out.println("6 : 풀 노드 상태 확인");
                System.out.println("7 : 추가 노드 생성");
                System.out.println("8 : 노드 추가 요청");
                System.out.println("9 : 리소스 읽기");
                System.out.println("10 : 종료");

                System.out.println("번호 입력 : ");
                num = sc.nextInt();

                switch (num) {
                    case 0:
                        indyNodeManager.GetServerIP();
                        System.out.println("서버 IP : " + indyNodeManager.server_IP);
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
                        indyNodeManager.GetValidatorInfo();
                        break;
                    case 7:
                        indyNodeManager.RunIndyContainer();
                        break;
                    case 8:
                        indyNodeManager.AddNodeListCheck();
                        break;
                    case 9:
                        indyNodeManager.GetResourceFile();
                        break;
                    case 10:
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