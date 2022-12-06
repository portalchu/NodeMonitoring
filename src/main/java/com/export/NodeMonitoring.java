package com.export;

import com.export.node.MonitoringData;
import com.export.node.Node;
import com.export.node.NodeInfo;
import com.export.utils.PoolUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.hyperledger.indy.sdk.ledger.Ledger;
import org.hyperledger.indy.sdk.pool.Pool;
import org.hyperledger.indy.sdk.did.Did;
import org.hyperledger.indy.sdk.did.DidJSONParameters;
import org.hyperledger.indy.sdk.did.DidResults.CreateAndStoreMyDidResult;
import org.hyperledger.indy.sdk.wallet.Wallet;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.simple.parser.JSONParser;

import static org.hyperledger.indy.sdk.ledger.Ledger.*;
import static com.export.utils.PoolUtils.PROTOCOL_VERSION;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.*;

public class NodeMonitoring {

    // 풀 핸들러
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
    // 서버가 종료된 후, 새로 실행 시 이전에 사용했던 데이터들을 저장하고 있기 위한 메타데이터
    JSONObject myDidMetadata;

    // 노드 모니터링의 서버 IP
    String server_IP;

    //
    int unreachableNodeCount = -1;
    int totalNodeCount = -1;
    int reachableNodeCount = -1;

    int checkCount = -1;

    MonitoringData monitoringData;
    NodeInfo nodeInfo;
    String nodeNameDefult = "NewNode";
    String monitoringDataDefultName = "Test";
    int containerDefultNumber = 0;
    String containerDefultName = "MonitoringContainer";
    String verificationKey;
    String blsPublicKey;
    String proofBlsKey;

    List<String> nodeList = new ArrayList<>();
    List<Node> readyNodeList = new ArrayList<>();
    List<Node> addNodeList = new ArrayList<>();

    List<NodeInfo> nodeInfoList = new ArrayList<>();

    List<MonitoringData> monitoringDataList = new ArrayList<>();


    public void ConnectIndyPool() throws Exception{
        System.out.println("Start Ledger");

        // Pool 연결 Protocol Version: 고정값
        // PROTOCOL_VERSION = 2;
        Pool.setProtocolVersion(PROTOCOL_VERSION).get();
        System.out.println("Set Protocol");
        // /com.com.export.utils/PoolUtils.java: Pool 연결을 위한 Config File 생성 함수
        // DEFAULT_POOL_NAME = "issuer";
        // Pool Config 생성 API 사용: Pool.createPoolLedgerConfig(원장 이름, Pool Genesis Transaction File)
        this.poolName = PoolUtils.createPoolLedgerConfig();
        System.out.println("poolName : " + poolName);
        // Pool 연결 API 사용: Pool.openPoolLedger(원장 이름, 연결 런타임 구성)
        this.pool = Pool.openPoolLedger(poolName, "{}").get();
        System.out.println("pool : " + pool);
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

        myDidMetadata = new JSONObject();

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

    public void CreateMonitoringData() throws Exception {
        System.out.println("==== CreateMonitoringData ====");

        this.monitoringData = new MonitoringData();

        monitoringData.setComputerName(monitoringDataDefultName);
        System.out.println("ComputerName : " + monitoringData.getComputerName());

        monitoringData.setComputerIP(GetServerIP());
        System.out.println("ComputerIP : " + monitoringData.getComputerIP());

        monitoringData.setContainerImage("indy-test");
        System.out.println("ContainerImage : " + monitoringData.getContainerImage());

        monitoringData.setContainerStartPort(9801);
        System.out.println("ContainerStartPort : " + monitoringData.getContainerStartPort());

        monitoringData.setContainerEndPort(0);
        System.out.println("ContainerEndPort : " + monitoringData.getContainerEndPort());

        monitoringData.setNodeNumber(0);
        System.out.println("NodeNumber : " + monitoringData.getNodeNumber());

        monitoringData.setMaxNodeNumber(0);
        System.out.println("MaxNodeNumber : " + monitoringData.getMaxNodeNumber());

        System.out.println("Add MonitoringData");
    }

    public void CheckPoolNode() throws Exception {
        int n;
        n = sc.nextInt();

        if (n < 1)
        {
            System.out.println("Input wrong Number");
            return;
        }

        while (true)
        {
            if (!GetValidatorInfo(n)) {
                RunIndyContainer();
                AddNodeListCheck();
            }

            Thread.sleep(60000);
        }
    }

    public boolean GetValidatorInfo(int n) throws Exception {
        System.out.println("==== getValidatorInfoObj ====");

        String getValidatorInfoRequest = Ledger.buildGetValidatorInfoRequest(trusteeDid).get();
        String getValidatorInfoResponse = Ledger.signAndSubmitRequest(pool, wallet, trusteeDid,
                getValidatorInfoRequest).get();

        JSONObject getValidatorInfoObj = new JSONObject(getValidatorInfoResponse);

        Set<String> keysets = getValidatorInfoObj.keySet();

        for (String key : keysets) {

            System.out.println("key: " + key);
            String validatorCheck = getValidatorInfoObj.getString(key);
            if (validatorCheck.equals("timeout"))
                continue;

            JSONObject validatorInfo = new JSONObject(getValidatorInfoObj.getString(String.format(key)));

            JSONObject result = validatorInfo.getJSONObject("result");
            JSONObject data = result.getJSONObject("data");
            JSONObject poolInfo = data.getJSONObject("Pool_info");

            unreachableNodeCount = poolInfo.getInt("Unreachable_nodes_count");
            totalNodeCount = poolInfo.getInt("Total_nodes_count");
            reachableNodeCount = poolInfo.getInt("Reachable_nodes_count");
        }

        System.out.println("check count : " + reachableNodeCount);
        System.out.println("unreachableNodeCount : " + unreachableNodeCount);
        System.out.println("totalNodeCount : " + totalNodeCount);
        System.out.println("reachableNodeCount : " + reachableNodeCount);
        System.out.println("reachableNodeCount : " + reachableNodeCount);

        int checkNodeCount = n + unreachableNodeCount;

        if (totalNodeCount >= 3 * checkNodeCount + 1) {
            System.out.println("Node Check No Problem");
            return true;
        }
        else {
            System.out.println("Need Node Add");
            return false;
        }
    }

    public boolean CheckMonitoringInfo() throws Exception {

        System.out.println("====== CheckMonitoringInfo ======");

        if (monitoringData == null)
        {
            System.out.println("monitoring Data is Null");
            return false;
        }

        return true;
    }

    public void RunIndyContainer() throws Exception {
        System.out.println("==== RunIndyContainer ====");

        String containerName = containerDefultName + containerDefultNumber++;
        System.out.println("containerName : " + containerName);
        String containerIp = monitoringData.getComputerIP();
        System.out.println("containerIp : " + containerIp);
        String nodeName = nodeNameDefult;
        System.out.println("nodeName : " + nodeName);
        int nodeNumber = monitoringData.getNodeNumber();
        System.out.println("nodeNumber : " + nodeNumber);
        int startPort = monitoringData.getContainerStartPort() + nodeNumber;
        System.out.println("startPort : " + startPort);

        int endPort = startPort + 5;
        System.out.println("endPort : " + endPort);
        String _nodeName = nodeName + nodeNumber++;
        System.out.println("_nodeName : " + _nodeName);
        int nodePort = startPort;
        System.out.println("nodePort : " + nodePort);
        int nodeClientPort = nodePort + 1;
        System.out.println("nodeClientPort : " + nodeClientPort);

        String cmd;

        cmd = "ipconfig";
        System.out.println("cmd : " + cmd);
        RunWindowCmd(cmd);

        String containerPort = startPort + "-" + endPort;

        cmd = "docker run -itd --name " + containerName + " -p " + containerIp + ":" + containerPort + ":" + containerPort + " -e POOL='sandbox' " + monitoringData.getContainerImage();
        System.out.println("cmd : " + cmd);
        RunWindowCmd(cmd);

        cmd = "docker exec --user root " + containerName + " sh -c \"cd etc/indy;sed -i \"s/None/$POOL/g\" indy_config.py\"";
        System.out.println("cmd : " + cmd);
        RunWindowCmd(cmd);

        cmd = "docker exec --user root " + containerName + " sh -c \"init_indy_node " + _nodeName +
                " " + containerIp + " " + nodePort + " " + containerIp + " " + nodeClientPort + " >> " + _nodeName + "_info.txt\"";
        System.out.println("cmd : " + cmd);
        RunWindowCmd(cmd);

        cmd = "docker cp indy-test:/var/lib/indy " + FileUtils.getUserDirectoryPath();
        System.out.println("cmd : " + cmd);
        RunWindowCmd(cmd);

        cmd = "docker cp " + FileUtils.getUserDirectoryPath() + "/indy/sandbox/pool_transactions_genesis " + containerName + ":/var/lib/indy/sandbox";
        System.out.println("cmd : " + cmd);
        RunWindowCmd(cmd);

        cmd = "docker cp " + containerName + ":/" + _nodeName + "_info.txt " + FileUtils.getUserDirectoryPath();
        System.out.println("cmd : " + cmd);
        RunWindowCmd(cmd);

        cmd = "docker exec --user root -d " + containerName + " sh -c \"start_indy_node " + _nodeName
                + " 0.0.0.0 " + nodePort + " 0.0.0.0 " + nodeClientPort + "\"";
        System.out.println("cmd : " + cmd);
        RunWindowCmd(cmd);

        Node _node = new Node();
        _node.setNodeName(_nodeName);
        System.out.println("_nodeName : " + _nodeName);
        _node.setNodeIP(containerIp);
        System.out.println("containerIp : " + containerIp);
        _node.setNodePort(nodePort);
        System.out.println("nodePort : " + nodePort);
        _node.setNodeClientPort(nodeClientPort);
        System.out.println("nodeClientPort : " + nodeClientPort);

        readyNodeList.add(_node);

        for(int i = 0; i < 2; i++)
        {
            _node = new Node();
            nodePort += 2;
            nodeClientPort +=2;
            _nodeName = nodeName + nodeNumber++;

            cmd = "docker exec --user root " + containerName + " sh -c \"init_indy_node " + _nodeName +
                    " " + containerIp + " " + nodePort + " " + containerIp + " " + nodeClientPort + " >> " + _nodeName + "_info.txt\"";
            System.out.println("cmd : " + cmd);
            RunWindowCmd(cmd);

            cmd = "docker cp " + containerName + ":/" + _nodeName + "_info.txt " + FileUtils.getUserDirectoryPath();
            System.out.println("cmd : " + cmd);
            RunWindowCmd(cmd);

            cmd = "docker exec --user root -d " + containerName + " sh -c \"start_indy_node " + _nodeName
                    + " 0.0.0.0 " + nodePort + " 0.0.0.0 " + nodeClientPort + "\"";
            System.out.println("cmd : " + cmd);
            RunWindowCmd(cmd);

            _node.setNodeName(_nodeName);
            System.out.println("_nodeName : " + _nodeName);
            _node.setNodeIP(containerIp);
            System.out.println("containerIp : " + containerIp);
            _node.setNodePort(nodePort);
            System.out.println("nodePort : " + nodePort);
            _node.setNodeClientPort(nodeClientPort);
            System.out.println("nodeClientPort : " + nodeClientPort);

            readyNodeList.add(_node);
        }

        monitoringData.setNodeNumber(nodeNumber);
        System.out.println("check nodeNumber : " + nodeNumber);
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

    public void NodeListCheck() throws Exception {

        System.out.println("readyNodeList size : " + readyNodeList.size());
        for (Node node : readyNodeList)
        {
            System.out.println("NodeName : " + node.getNodeName());
        }
    }

    public void AddNodeListCheck() throws Exception {

        while (readyNodeList.size() != 0)
        {
            Node _node = readyNodeList.get(0);
            System.out.println("NodeName : " + _node.getNodeName());
            AddNode(_node);
            addNodeList.add(_node);
            readyNodeList.remove(_node);
        }
    }

    public void AddNode(Node node) throws Exception {
        System.out.println("======== AddNode ========");

        Scanner nodeFileScanner = new Scanner(new File(
                FileUtils.getUserDirectoryPath() + "/" + node.getNodeName() + "_info.txt"));

        List<String> nodeFileScannerList = new ArrayList<>();

        while (nodeFileScanner.hasNext()) {
            String str = nodeFileScanner.next();
            nodeFileScannerList.add(str);
        }

        int index[] = {3, 26, 44, 52};

        String nodeName = nodeFileScannerList.get(index[0]);
        System.out.println("nodeName : " + nodeName);

        if (node.getNodeName().equals(nodeName))
            System.out.println("node Name Clear!");

        verificationKey = nodeFileScannerList.get(index[1]);
        System.out.println("verificationKey : " + verificationKey);
        blsPublicKey = nodeFileScannerList.get(index[2]);
        System.out.println("blsPublicKey : " + blsPublicKey);
        proofBlsKey = nodeFileScannerList.get(index[3]);
        System.out.println("ProofBlsKey : " + proofBlsKey);

        CreateAndStoreMyDidResult createMyDidResult = Did.createAndStoreMyDid(this.wallet, "{}").get();
        String addNodeDid = createMyDidResult.getDid();
        String addNodeVerkey = createMyDidResult.getVerkey();
        nodeList.add(addNodeDid);

        String nymRequest = buildNymRequest(this.trusteeDid, addNodeDid, addNodeVerkey, null, "STEWARD").get();
        String nymResponseJson = signAndSubmitRequest(this.pool, this.wallet, this.trusteeDid, nymRequest).get();
        System.out.println("nymResponseJson : " + nymResponseJson);

        String dest = verificationKey;
        String data = "{\"node_ip\":\"" + node.getNodeIP() + "\"," +
                "\"node_port\":" + node.getNodePort() + "," +
                "\"client_ip\":\"" + node.getNodeIP() + "\"," +
                "\"client_port\":" + node.getNodeClientPort() +"," +
                "\"alias\":\"" + nodeName + "\"," +
                "\"services\":[\"VALIDATOR\"]," +
                "\"blskey\":\"" + blsPublicKey + "\"," +
                "\"blskey_pop\":\"" + proofBlsKey + "\"" +
                "}";

        String getAddNodeRequest = buildNodeRequest(addNodeDid, dest, data).get();
        nymResponseJson = signAndSubmitRequest(this.pool, this.wallet, addNodeDid, getAddNodeRequest).get();

        System.out.println("nymResponseJson : " + nymResponseJson);

        Thread.sleep(300000);
        System.out.println(node.getNodeName() + " Add Node Clear");
    }

    public String GetServerIP() throws Exception {
        InetAddress server = InetAddress.getLocalHost();

        server_IP = server.getHostAddress();

        System.out.println("Server IP : " + server.getHostAddress());

        return server.getHostAddress();
    }

    public void GetResourceFile() throws Exception {

        String something = IOUtils.toString(
                getClass().getResourceAsStream("/clientIP.json"), "UTF-8");

        ObjectMapper objectMapper = new ObjectMapper();

        nodeInfo = objectMapper.readValue(something, NodeInfo.class);

        System.out.println("nodeInfo ContainerName : " + nodeInfo.getContainerName());
        System.out.println("nodeInfo ContainerIP : " + nodeInfo.getContainerIP());
        System.out.println("nodeInfo ContainerStartPort() : " + nodeInfo.getContainerStartPort());
        System.out.println("nodeInfo ContainerEndPort() : " + nodeInfo.getContainerEndPort());

        nodeInfoList.add(nodeInfo);
    }


    public void NodeMonitoringAndAddNode() throws Exception {
        // 1. 풀과 연결 확인
        ConnectIndyPool();

        // 2. 지갑 및 DID 생성

    }
}

