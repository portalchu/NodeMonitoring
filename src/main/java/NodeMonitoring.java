
import utils.PoolUtils;
import org.apache.commons.io.FileUtils;
import org.hyperledger.indy.sdk.ledger.Ledger;
import org.hyperledger.indy.sdk.pool.Pool;
import org.hyperledger.indy.sdk.did.Did;
import org.hyperledger.indy.sdk.did.DidJSONParameters;
import org.hyperledger.indy.sdk.did.DidResults.CreateAndStoreMyDidResult;
import org.hyperledger.indy.sdk.wallet.Wallet;
import org.json.JSONObject;
import org.json.JSONArray;

import static utils.PoolUtils.PROTOCOL_VERSION;
import static org.hyperledger.indy.sdk.ledger.Ledger.buildNymRequest;
import static org.hyperledger.indy.sdk.ledger.Ledger.buildAttribRequest;
import static org.hyperledger.indy.sdk.ledger.Ledger.buildGetAttribRequest;
import static org.hyperledger.indy.sdk.ledger.Ledger.buildGetNymRequest;
import static org.hyperledger.indy.sdk.ledger.Ledger.signAndSubmitRequest;
import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class NodeMonitoring {
    private Pool pool;
    // 지정한 블록체인 원장 이름
    String poolName;
    // 지갑 핸들
    private Wallet wallet;
    // 지갑 아이디 구성
    String myWalletConfig;
    // 지갑 비밀번호 구성
    String myWalletCredentials;
    // 블록체인 원장에 접근 가능한 DID 생성 Seed
    String trusteeSeed = "000000000000000000000000Trustee1";
    // 블록체인 원장에 접근 가능한 DID
    String trusteeDid;
    // Issuer DID
    String myDid;
    // Issuer 공개키
    String myVerkey;
    // 입력을 위해 사용
    Scanner sc = new Scanner(System.in);
    // 서버가 종료된 후, 새로 실행 시 이전에 사용했던 데이터들을 저장하고 있기 위한 메타데이터(Schema ID, Credential Definition ID ...)
    JSONObject myDidMetadata;

    //String server_IP = "192.168.45.155";
    String server_IP = "220.68.5.138";

    int unreachableNodeCount = 0;
    int totalNodeCount = 0;
    int reachableNodeCount = 0;

    public void ConnectIndyPool() throws Exception{
        System.out.println("Start Ledger");

        // Pool 연결 Protocol Version: 고정값
        // PROTOCOL_VERSION = 2;
        Pool.setProtocolVersion(PROTOCOL_VERSION).get();
        // /com.utils/PoolUtils.java: Pool 연결을 위한 Config File 생성 함수
        // DEFAULT_POOL_NAME = "issuer";
        // Pool Config 생성 API 사용: Pool.createPoolLedgerConfig(원장 이름, Pool Genesis Transaction File)
        this.poolName = PoolUtils.createPoolLedgerConfig();
        // Pool 연결 API 사용: Pool.openPoolLedger(원장 이름, 연결 런타임 구성)
        this.pool = Pool.openPoolLedger(poolName, "{}").get();
    }

    // wallet 사용
    // wallet 파일이 있다면 open
    // wallet 파일이 없다면 create
    public void walletInput() throws Exception {
        System.out.println("지갑 생성 및 열기");
        System.out.println("Wallet ID: ");
        // 지갑 아이디 입력 받음
        String myWallet = sc.next();
        System.out.println("Wallet KEY: ");
        // 지갑 비밀번호 입력 받음
        String myWalletKey = sc.next();

        // 생성된 지갑이 저장되는 경로
        String walletFilePath = FileUtils.getUserDirectoryPath() + "/.indy_client/wallet/" + myWallet;
        System.out.println("walletFilePath : " + walletFilePath);

        // 데이터 저장을 위해 사용
        myDidMetadata = new JSONObject();

        // 지갑이 저장되는 경로 탐색
        File walletFilePathCheck = new File(walletFilePath);
        System.out.println("walletFilePathCheck : " + walletFilePathCheck.isDirectory());

        // 지갑이 없으면
        if (!walletFilePathCheck.isDirectory()) {
            createWallet(myWallet, myWalletKey);
            createTrusteeDid();
            createDid();
        }
        else { // 지갑이 있으면
            openWallet(myWallet, myWalletKey);
            findDidByWallet();
        }
    }

    // wallet 생성
    // @입력 값
    // my_wallet: wallet의 이름
    // my_wallet_key: wallet의 비밀번호
    public void createWallet(String myWallet, String myWalletKey) throws Exception{
        System.out.println("Create Wallet");
        this.myWalletConfig = new JSONObject().put("id", myWallet).toString();
        this.myWalletCredentials = new JSONObject().put("key", myWalletKey).toString();

        // 지갑 생성
        // 지갑 생성 API 사용: Wallet.createWallet(지갑 아이디 구성, 지갑 비밀번호 구성)
        Wallet.createWallet(this.myWalletConfig, this.myWalletCredentials).get();
        // 지갑 핸들 추출
        // 지갑 오픈 API 사용: Wallet.openWallet(지갑 아이디 구성, 지갑 비밀번호 구성)
        this.wallet = Wallet.openWallet(this.myWalletConfig, this.myWalletCredentials).get();

        System.out.println("My Wallet ID: "+this.myWalletConfig);
    }

    // wallet 열기
    // @입력 값
    // my_wallet: wallet의 이름
    // my_wallet_key: wallet의 비밀번호
    public void openWallet(String myWallet, String myWalletKey) throws Exception {
        System.out.println("Open Wallet");
        this.myWalletConfig = new JSONObject().put("id", myWallet).toString();
        this.myWalletCredentials = new JSONObject().put("key", myWalletKey).toString();
        try {
            // 지갑 핸들 추출
            // 지갑 오픈 API 사용: Wallet.openWallet(지갑 아이디 구성, 지갑 비밀번호 구성)
            this.wallet = Wallet.openWallet(this.myWalletConfig, this.myWalletCredentials).get();

            this.myDidMetadata = new JSONObject();

            System.out.println("My Wallet ID: "+this.myWalletConfig);
        } catch (Exception e) {
            System.out.println("Wallet doesn't exist.");
            return;
        }
    }

    // trusteedid, endoserdid(schema 생성에 필요) 생성
    public void createTrusteeDid() throws Exception{
        System.out.println("Create Trustee DID");
        // endorser DID 생성
        // DID 생성 API 사용: Did.createAndStoreMyDid(지갑 핸들, DID 생성 구성(없으면 Default))

        // trusteeSeed를 사용하여 블록체인에 등록된 trustee DID 생성
        DidJSONParameters.CreateAndStoreMyDidJSONParameter theirDidJson =
                new DidJSONParameters.CreateAndStoreMyDidJSONParameter(null, this.trusteeSeed, null, null);

        // DID 생성 API 사용: Did.createAndStoreMyDid(지갑 핸들, DID 생성 구성(없으면 Default))
        CreateAndStoreMyDidResult createTheirDidResult = Did.createAndStoreMyDid(this.wallet, theirDidJson.toJson()).get();
        this.trusteeDid = createTheirDidResult.getDid();
    }

    // did 생성
    public void createDid() throws Exception{
        System.out.println("Create DID");
        // DID 생성
        // DID 생성 API 사용: Did.createAndStoreMyDid(지갑 핸들, DID 생성 구성(없으면 Default))
        CreateAndStoreMyDidResult createMyDidResult = Did.createAndStoreMyDid(this.wallet, "{}").get();
        this.myDid = createMyDidResult.getDid();
        this.myVerkey = createMyDidResult.getVerkey();

        // 트랜잭션 생성 API 사용: buildNymRequest(트랜잭션 작성자 DID, 저장할 DID, 저장할 공개키, 트랜잭션 별칭, 저장할 DID의 권한)
        String nymRequest = buildNymRequest(this.trusteeDid, this.myDid, this.myVerkey, null, null).get();

        // 블록체인 저장 API 사용: signAndSubmitRequest(원장 핸들, 지갑 핸들, 트랜잭션 작성자 DID, 트랜잭션)
        String nymResponseJson = signAndSubmitRequest(this.pool, this.wallet, this.trusteeDid, nymRequest).get();
        JSONObject nymResponse = new JSONObject(nymResponseJson);

        // 블록체인에 해당 DID가 제대로 등록됐는지 검증
        assertEquals(this.myDid, nymResponse.getJSONObject("result").getJSONObject("txn").getJSONObject("data").getString("dest"));
        assertEquals(this.myVerkey, nymResponse.getJSONObject("result").getJSONObject("txn").getJSONObject("data").getString("verkey"));

        
        // did 메타데이터 정보 저장을 위해 작성
        this.myDidMetadata.put("myDid", this.myDid);
        this.myDidMetadata.put("trusteeDid", this.trusteeDid);
        this.myDidMetadata.put("myVerkey", this.myVerkey);
        Did.setDidMetadata(this.wallet, this.myDid, this.myDidMetadata.toString());
    }

    // Test를 위한 함수
    // did 검색
    public void getDid() throws Exception{
        System.out.println("Get DID");
        String getDID = sc.next();

        // 정보를 등록할 때와 마찬가지로 검색을 위한 Get 트랜잭션을 작성해야함
        // 트랜잭션 검색 API 사용: buildGetNymRequest(트랜잭션 작성자 DID, 검색할 DID)
        String getNymRequest = buildGetNymRequest(this.myDid, getDID).get();

        // 응답을 통해 검색 결과를 얻을 수 있음
        // 블록체인 저장 API 사용: signAndSubmitRequest(원장 핸들, 지갑 핸들, 트랜잭션 작성자 DID, 트랜잭션)
        String getNymResponse = signAndSubmitRequest(this.pool, this.wallet, this.myDid, getNymRequest).get();
        System.out.println(getNymResponse);
    }

    // Test를 위한 함수
    // endpoint 검색
    public void getEndpoint() throws Exception{
        System.out.println("Get Endpoint: ");
        String targetDID = sc.next();

        // 트랜잭션 생성 API 사용: buildGetAttribRequest(트랜잭션 작성자 DID, 검색할 DID, 속성 이름, 데이터 해시값, 암호화된 속성 데이터)
        String getAttribRequest = buildGetAttribRequest(this.myDid, targetDID, "endpoint", null, null).get();

        // 블록체인 저장 API 사용: signAndSubmitRequest(원장 핸들, 지갑 핸들, 트랜잭션 작성자 DID, 트랜잭션)
        String getAttribResponse = signAndSubmitRequest(this.pool, this.wallet, this.myDid, getAttribRequest).get();
        System.out.println(getAttribResponse);
    }

    public void findDidByWallet() throws Exception {
        System.out.println("find DID By Wallet");

        // 지갑에서 did 및 didMetadata 모두 가져옴
        String didList = Did.getListMyDidsWithMeta(this.wallet).get();

        // 해당 정보의 경우 JSONArray 형식으로 오기 때문에 변환
        // 예시 : {["A"],["B"],["C"], ... ,["Z"]}
        JSONArray didListJsonArray = new JSONArray(didList);

        // 리스트 형식의 did 값들을 하나 씩 확인하여 did Metadata 확인
        Iterator<Object> didListIterator = didListJsonArray.iterator();
        while (didListIterator.hasNext()) {
            JSONObject didListData = new JSONObject(didListIterator.next().toString());
            Object didMetadata = didListData.get("metadata");

            // 만약 Metadata를 넣은적이 없다면 null이기 때문에 사용자 빼곤 다 null 있음
            if (!didMetadata.equals(null)) {
                // did 메타데이터 정보 저장을 위해 작성
                this.myDidMetadata = new JSONObject(didMetadata.toString());

                // 메타 데이터의 키 값 및 정보 확인
                Iterator<String> testKey = this.myDidMetadata.keys();

                // 각각의 정보들을 변수에 매핑
                this.myDid = myDidMetadata.getString("myDid");
                this.trusteeDid = myDidMetadata.getString("trusteeDid");
                this.myVerkey = myDidMetadata.getString("myVerkey");
            }
        }
    }

    public void GetValidatorInfo() throws Exception {
        System.out.println("==== getValidatorInfoObj ====");

        String getValidatorInfoRequest = Ledger.buildGetValidatorInfoRequest(trusteeDid).get();
        String getValidatorInfoResponse = Ledger.signAndSubmitRequest(pool, wallet, trusteeDid, getValidatorInfoRequest).get();

        JSONObject getValidatorInfoObj = new JSONObject(getValidatorInfoResponse);

        System.out.println("getValidatorInfoObj : " + getValidatorInfoObj);

        for (int i = 1; i <= 4; i++) {
            //Assert.assertFalse(new JSONObject(getValidatorInfoObj.getString(String.format("Node%s", i))).getJSONObject("result").isNull("data"));
            JSONObject validatorInfo = new JSONObject(getValidatorInfoObj.getString(String.format("Node%s", i)));
            System.out.println("validatorInfo : " + validatorInfo);

            JSONObject result = validatorInfo.getJSONObject("result");
            JSONObject data = result.getJSONObject("data");
            JSONObject poolInfo = data.getJSONObject("Pool_info");

            unreachableNodeCount = poolInfo.getInt("Unreachable_nodes_count");
            totalNodeCount = poolInfo.getInt("Total_nodes_count");
            reachableNodeCount = poolInfo.getInt("Reachable_nodes_count");
        }

        System.out.println("unreachableNodeCount : " + unreachableNodeCount);
        System.out.println("totalNodeCount : " + totalNodeCount);
        System.out.println("reachableNodeCount : " + reachableNodeCount);
    }

    public void RunIndyContainer() throws Exception {
        System.out.println("==== RunIndyContainer ====");

        String cmd;

        cmd = "ipconfig";
        System.out.println("cmd : " + cmd);
        RunWindowCmd(cmd);

        cmd = "docker run -itd --name indy-test1 -p " + server_IP + ":9711-9720:9711-9720 -e POOL='sandbox' indy-test";
        System.out.println("cmd : " + cmd);
        RunWindowCmd(cmd);

        cmd = "docker exec --user root indy-test1 sh -c \"cd etc/indy;sed -i \"s/None/$POOL/g\" indy_config.py\"";
        System.out.println("cmd : " + cmd);
        RunWindowCmd(cmd);

        cmd = "docker exec --user root indy-test1 sh -c \"init_indy_node NewNode " + server_IP + " 9711 " + server_IP + " 9712 0000000000000000000000000NewNode >> NewNode_info.txt\"";
        System.out.println("cmd : " + cmd);
        RunWindowCmd(cmd);

        cmd = "docker cp indy-test:/var/lib/indy " + FileUtils.getUserDirectoryPath();
        System.out.println("cmd : " + cmd);
        RunWindowCmd(cmd);

        cmd = "docker cp " + FileUtils.getUserDirectoryPath() + "/indy/sandbox/pool_transactions_genesis indy-test1:/var/lib/indy/sandbox";
        System.out.println("cmd : " + cmd);
        RunWindowCmd(cmd);

        cmd = "docker cp indy-test1:/NewNode_info.txt " + FileUtils.getUserDirectoryPath();
        System.out.println("cmd : " + cmd);
        RunWindowCmd(cmd);

        cmd = "docker exec --user root -d indy-test1 sh -c \"start_indy_node NewNode 0.0.0.0 9711 0.0.0.0 9712\"";
        System.out.println("cmd : " + cmd);
        RunWindowCmd(cmd);

    }

    public String RunWindowCmd(String cmd) throws Exception {
        Process p = Runtime.getRuntime().exec("cmd /c " + cmd);
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

        String line = null;

        while((line = br.readLine()) != null) {

            System.out.println(line);
        }

        return line;
    }

    public void AddNode() throws Exception {
        List<String> nodeFileList = Files.readAllLines(Paths.get(
                FileUtils.getUserDirectoryPath() + "/NewNode_info.txt"));

        System.out.println("nodeFileList : " + nodeFileList);

        String nodeFileString = Files.readString(Paths.get(
                FileUtils.getUserDirectoryPath() + "/NewNode_info.txt"));

        System.out.println("nodeFileString : " + nodeFileString);

        Scanner nodeFileScanner = new Scanner(new File(
                FileUtils.getUserDirectoryPath() + "/NewNode_info.txt"));

        List<String> nodeFileScannerList = new ArrayList<>();

        while (nodeFileScanner.hasNext()) {
            String str = nodeFileScanner.next();
            System.out.println(str);

            nodeFileScannerList.add(str);
        }

        System.out.println("nodeFileScannerList : " + nodeFileScannerList);

        for (int i = 3; nodeFileScannerList.size() > i; i += 4)
        {
            System.out.println("i : " + i);
            System.out.println(nodeFileScannerList.get(i));
        }

    }
}

