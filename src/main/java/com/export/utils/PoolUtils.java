package com.export.utils;

import org.apache.commons.io.FileUtils;
import org.hyperledger.indy.sdk.IndyException;
import org.hyperledger.indy.sdk.pool.Pool;
import org.hyperledger.indy.sdk.pool.PoolJSONParameters;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PoolUtils {
    
    // 풀 설정 저장 시 생성되는 파일 이름
    // 기존의 풀과 다른 풀에 연결할 경우 이름 변경 또는 기존 파일 삭제 필요
    // 파일 위치(윈도우 기준) : C:\Users\사용자컴퓨터이름\.indy_client
    private static final String DEFAULT_POOL_NAME = "issuer9";

    // 풀 버전 적용시 사용 (기본 2)
    public static final int PROTOCOL_VERSION = 2;

    // 제네시스 파일이 없을 경우 자체적으로 생성
    // EnvironmentUtils의 testPoolIp 값으로 수정가능
    private static File createGenesisTxnFile(String filename) throws IOException {
        System.out.println("=== CreateGenesisTxnFile ===");
        //String path = EnvironmentUtils.getTmpPath(filename);

        String path;

        // 운영체제 확인
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            path = FileUtils.getUserDirectoryPath() + "\\" + filename; // Window
        }
        else {
            path = new File("").getAbsolutePath() + "\\" + filename; // Centos
        }

        // 풀 IP 설정
        String testPoolIp = EnvironmentUtils.getTestPoolIP();

        // 제네시스 파일 입력 및 생성
        String[] defaultTxns = new String[]{
                String.format("{\"reqSignature\":{},\"txn\":{\"data\":{\"data\":{\"alias\":\"Node1\",\"blskey\":\"4N8aUNHSgjQVgkpm8nhNEfDf6txHznoYREg9kirmJrkivgL4oSEimFF6nsQ6M41QvhM2Z33nves5vfSn9n1UwNFJBYtWVnHYMATn76vLuL3zU88KyeAYcHfsih3He6UHcXDxcaecHVz6jhCYz1P2UZn2bDVruL5wXpehgBfBaLKm3Ba\",\"blskey_pop\":\"RahHYiCvoNCtPTrVtP7nMC5eTYrsUA8WjXbdhNc8debh1agE9bGiJxWBXYNFbnJXoXhWFMvyqhqhRoq737YQemH5ik9oL7R4NTTCz2LEZhkgLJzB3QRQqJyBNyv7acbdHrAT8nQ9UkLbaVL9NBpnWXBTw4LEMePaSHEw66RzPNdAX1\",\"client_ip\":\"%s\",\"client_port\":9702,\"node_ip\":\"%s\",\"node_port\":9701,\"services\":[\"VALIDATOR\"]},\"dest\":\"Gw6pDLhcBcoQesN72qfotTgFa7cbuqZpkX3Xo6pLhPhv\"},\"metadata\":{\"from\":\"Th7MpTaRZVRYnPiabds81Y\"},\"type\":\"0\"},\"txnMetadata\":{\"seqNo\":1,\"txnId\":\"fea82e10e894419fe2bea7d96296a6d46f50f93f9eeda954ec461b2ed2950b62\"},\"ver\":\"1\"}", testPoolIp, testPoolIp),
                String.format("{\"reqSignature\":{},\"txn\":{\"data\":{\"data\":{\"alias\":\"Node2\",\"blskey\":\"37rAPpXVoxzKhz7d9gkUe52XuXryuLXoM6P6LbWDB7LSbG62Lsb33sfG7zqS8TK1MXwuCHj1FKNzVpsnafmqLG1vXN88rt38mNFs9TENzm4QHdBzsvCuoBnPH7rpYYDo9DZNJePaDvRvqJKByCabubJz3XXKbEeshzpz4Ma5QYpJqjk\",\"blskey_pop\":\"Qr658mWZ2YC8JXGXwMDQTzuZCWF7NK9EwxphGmcBvCh6ybUuLxbG65nsX4JvD4SPNtkJ2w9ug1yLTj6fgmuDg41TgECXjLCij3RMsV8CwewBVgVN67wsA45DFWvqvLtu4rjNnE9JbdFTc1Z4WCPA3Xan44K1HoHAq9EVeaRYs8zoF5\",\"client_ip\":\"%s\",\"client_port\":9704,\"node_ip\":\"%s\",\"node_port\":9703,\"services\":[\"VALIDATOR\"]},\"dest\":\"8ECVSk179mjsjKRLWiQtssMLgp6EPhWXtaYyStWPSGAb\"},\"metadata\":{\"from\":\"EbP4aYNeTHL6q385GuVpRV\"},\"type\":\"0\"},\"txnMetadata\":{\"seqNo\":2,\"txnId\":\"1ac8aece2a18ced660fef8694b61aac3af08ba875ce3026a160acbc3a3af35fc\"},\"ver\":\"1\"}\n", testPoolIp, testPoolIp),
                String.format("{\"reqSignature\":{},\"txn\":{\"data\":{\"data\":{\"alias\":\"Node3\",\"blskey\":\"3WFpdbg7C5cnLYZwFZevJqhubkFALBfCBBok15GdrKMUhUjGsk3jV6QKj6MZgEubF7oqCafxNdkm7eswgA4sdKTRc82tLGzZBd6vNqU8dupzup6uYUf32KTHTPQbuUM8Yk4QFXjEf2Usu2TJcNkdgpyeUSX42u5LqdDDpNSWUK5deC5\",\"blskey_pop\":\"QwDeb2CkNSx6r8QC8vGQK3GRv7Yndn84TGNijX8YXHPiagXajyfTjoR87rXUu4G4QLk2cF8NNyqWiYMus1623dELWwx57rLCFqGh7N4ZRbGDRP4fnVcaKg1BcUxQ866Ven4gw8y4N56S5HzxXNBZtLYmhGHvDtk6PFkFwCvxYrNYjh\",\"client_ip\":\"%s\",\"client_port\":9706,\"node_ip\":\"%s\",\"node_port\":9705,\"services\":[\"VALIDATOR\"]},\"dest\":\"DKVxG2fXXTU8yT5N7hGEbXB3dfdAnYv1JczDUHpmDxya\"},\"metadata\":{\"from\":\"4cU41vWW82ArfxJxHkzXPG\"},\"type\":\"0\"},\"txnMetadata\":{\"seqNo\":3,\"txnId\":\"7e9f355dffa78ed24668f0e0e369fd8c224076571c51e2ea8be5f26479edebe4\"},\"ver\":\"1\"}\n", testPoolIp, testPoolIp),
                String.format("{\"reqSignature\":{},\"txn\":{\"data\":{\"data\":{\"alias\":\"Node4\",\"blskey\":\"2zN3bHM1m4rLz54MJHYSwvqzPchYp8jkHswveCLAEJVcX6Mm1wHQD1SkPYMzUDTZvWvhuE6VNAkK3KxVeEmsanSmvjVkReDeBEMxeDaayjcZjFGPydyey1qxBHmTvAnBKoPydvuTAqx5f7YNNRAdeLmUi99gERUU7TD8KfAa6MpQ9bw\",\"blskey_pop\":\"RPLagxaR5xdimFzwmzYnz4ZhWtYQEj8iR5ZU53T2gitPCyCHQneUn2Huc4oeLd2B2HzkGnjAff4hWTJT6C7qHYB1Mv2wU5iHHGFWkhnTX9WsEAbunJCV2qcaXScKj4tTfvdDKfLiVuU2av6hbsMztirRze7LvYBkRHV3tGwyCptsrP\",\"client_ip\":\"%s\",\"client_port\":9708,\"node_ip\":\"%s\",\"node_port\":9707,\"services\":[\"VALIDATOR\"]},\"dest\":\"4PS3EDQ3dW1tci1Bp6543CfuuebjFrg36kLAUcskGfaA\"},\"metadata\":{\"from\":\"TWwCRQRZ2ZHMJFn9TzLp7W\"},\"type\":\"0\"},\"txnMetadata\":{\"seqNo\":4,\"txnId\":\"aa5e817d7cc626170eca175822029339a444eb0ee8f0bd20d3b0b76e566fb008\"},\"ver\":\"1\"}", testPoolIp, testPoolIp)
        };

        File file = new File(path);

        FileUtils.forceMkdirParent(file);

        FileWriter fw = new FileWriter(file);
        for (String defaultTxn : defaultTxns) {
            fw.write(defaultTxn);
            fw.write("\n");
        }

        fw.close();

        return file;
    }

    // 제네시스 파일이 있을 경우 읽기
    // 파일 위치(윈도우 기준) : C:\Users\사용자컴퓨터이름\
    //         (Cento os 기준) : home/사용자이름/
    private static File readGenesisTxnFile(String filename) {
        System.out.println("=== ReadGenesisTxnFile ===");

        String path;
        File file;
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            // Window
            path = FileUtils.getUserDirectoryPath() + "\\" + filename;
            file = new File(path);
            if (file.exists()) return file;
        }
        else {
            // Centos
            path = new File("").getAbsolutePath() + "\\" + filename;
            file = new File(path);
            if (file.exists()) return file;

        }

        System.out.println("there no file");
        return new File("");
    }

    // 설정 파일 생성
    // 풀 정보를 만들어 저장하며 이후 해당 정보를 통해 연결
    public static String createPoolLedgerConfig() throws IOException, InterruptedException, java.util.concurrent.ExecutionException, IndyException {
        //File genesisTxnFile = createGenesisTxnFile("temp.txn");
        System.out.println("=== CreatePoolLedgerConfig ===");

        // 제네시스 파일 읽기
        File genesisTxnFile = readGenesisTxnFile("pool_transactions_genesis");

        // 제네시스 파일이 없을 경우 새로 생성
        if (!genesisTxnFile.exists()) {
            System.out.println("there no transactions file!");
            genesisTxnFile = createGenesisTxnFile("pool_transactions_genesis");
        }

        // 위 행동을 통해 생성되지 않을 경우 Null 반환
        if (!genesisTxnFile.exists()) {
            System.out.println("The pool_transactions_genesis file was not recognized. " +
                    "Please check the pool_transactions_genesis file.");
            return null;
        }

        // 위 정보 기반으로 설정 파일 생성
        PoolJSONParameters.CreatePoolLedgerConfigJSONParameter createPoolLedgerConfigJSONParameter
                = new PoolJSONParameters.CreatePoolLedgerConfigJSONParameter(genesisTxnFile.getAbsolutePath());
        System.out.println("PoolLedgerConfig : " + createPoolLedgerConfigJSONParameter);

        String poolFilePath = FileUtils.getUserDirectoryPath() + "/.indy_client/pool/" + DEFAULT_POOL_NAME;
        System.out.println("poolFilePath : " + poolFilePath);
        File poolFilePathCheck = new File(poolFilePath);

        // 이미 만들어진 설정 파일이 있는지 확인
        System.out.println("poolFilePathCheck : " + poolFilePathCheck.isDirectory());
        if (!poolFilePathCheck.isDirectory())
        {
            System.out.println("create Pool Ledger");
            Pool.createPoolLedgerConfig(DEFAULT_POOL_NAME, createPoolLedgerConfigJSONParameter.toJson()).get();
        }

        return DEFAULT_POOL_NAME;
    }

}

