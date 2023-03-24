package com.export;

import com.export.node.MonitoringData;
import com.export.node.Node;
import com.export.node.NodeInfo;
import com.export.utils.PoolUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.JSchException;
import org.apache.commons.io.FileUtils;
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

import java.io.*;
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

    String poolNameDefult = "sandbox";
    String nodeNameDefult = "NewNode";
    String monitoringDataDefultName = "Test";
    int containerDefultNumber = 0;
    String containerDefultName = "test";
    String verificationKey;
    String blsPublicKey;
    String proofBlsKey;

    List<String> nodeList = new ArrayList<>();
    List<Node> readyNodeList = new ArrayList<>();
    List<Node> addNodeList = new ArrayList<>();

    List<NodeInfo> nodeInfoList = new ArrayList<>();

    List<MonitoringData> monitoringDataList = new ArrayList<>();

    Connection connection;


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

    // 이미 생서한 지갑에서 필요한 정보를 조회
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

    // 모니터링 시작 함수
    public void StartNodeMonitoring() throws Exception {
        System.out.println("==== StartNodeMonitoring ====");

        // 모니터링을 위한 monitoringData 확인
        // monitoringData는 node.MonitoringData 확인
        List<MonitoringData> monitoringDataList = ReadMonitoringData();

        if (monitoringDataList.size() <= 0) {
            System.out.println("There no monitoring Data in List");
            monitoringDataList.add(CreateMonitoringData());
        }
        
        // 체크 노드 수 입력
        // 해당 정수 값을 문제 노드에 더해 계산
        System.out.println("Check node number : ");
        int n;
        n = sc.nextInt();

        if (n < 1)
        {
            System.out.println("Input wrong Number");
            return;
        }

        // 읽은 monitoringData를 기반으로 모니터링 시작
        for (MonitoringData monitoringData : monitoringDataList)
        {
            while (monitoringData.getMaxNodeNumber() >= addNodeList.size())
            {
                GetValidatorInfo(monitoringData, n);
                Thread.sleep(60000);
            }
            
            // monitoringData의 Max_Node_Number 값을 모두 채울 경우 다음 monitoringData를 읽음
            System.out.println("monitoring node is full");
        }
    }

    // 모니터링 데이터 생성
    // Test용 코드로 임이의 MonitoringData 생성 시 사용
    public MonitoringData CreateMonitoringData() throws Exception {
        System.out.println("==== CreateMonitoringData ====");

        MonitoringData monitoringData = new MonitoringData();

        monitoringData.setPoolName(poolNameDefult);
        System.out.println("PoolName : " + monitoringData.getPoolName());

        monitoringData.setComputerName(monitoringDataDefultName);
        System.out.println("ComputerName : " + monitoringData.getComputerName());

        monitoringData.setComputerIP(GetServerIP());
        System.out.println("ComputerIP : " + monitoringData.getComputerIP());

        monitoringData.setContainerImage("giry0612/indy-node-container");
        System.out.println("ContainerImage : " + monitoringData.getContainerImage());

        monitoringData.setContainerStartPort(9801);
        System.out.println("ContainerStartPort : " + monitoringData.getContainerStartPort());

        monitoringData.setContainerEndPort(0);
        System.out.println("ContainerEndPort : " + monitoringData.getContainerEndPort());

        monitoringData.setNodeNumber(0);
        System.out.println("NodeNumber : " + monitoringData.getNodeNumber());

        monitoringData.setMaxNodeNumber(9);
        System.out.println("MaxNodeNumber : " + monitoringData.getMaxNodeNumber());

        monitoringData.setSshUserName("");
        System.out.println("SshUserName : " + monitoringData.getSshUserName());

        monitoringData.setSshHostIp("");
        System.out.println("SshHostIp : " + monitoringData.getSshHostIp());

        monitoringData.setSshPortNumber(22);
        System.out.println("SshPortNumber : " + monitoringData.getSshPortNumber());

        monitoringData.setSshPassword("");
        System.out.println("SshPassword : " + monitoringData.getSshPassword());

        System.out.println("Add MonitoringData");

        return monitoringData;
    }

    // 모니터링 데이터 읽기
    // resources에 있는 Monitoring_Computer_Config.json 파일을 읽어 리스트에 저장
    public List<MonitoringData> ReadMonitoringData() throws Exception {
        System.out.println("==== ReadMonitoringData ====");

        // MonitoringData가 들어갈 리스트
        List<MonitoringData> monitoringDataList = new ArrayList<>();

        // resources에 있는 Monitoring_Computer_Config.json 파일을 읽음
        File file = new File(
                this.getClass().getClassLoader().getResource("Monitoring_Computer_Config.json").getFile()
        );

        if (!file.exists()) {
            System.out.println("there no Monitoring_Computer_Config.json File");
            return null;
        }

        // Monitoring_Computer_Config.json 파일은 JSONArray 형식이며 이를 파싱해 하나씩 저장
        Reader reader = new FileReader(this.getClass().getClassLoader()
                .getResource("Monitoring_Computer_Config.json").getFile());
        JSONParser parser = new JSONParser();
        ObjectMapper objectMapper = new ObjectMapper();

        Object obj = parser.parse(reader);
        System.out.println("obj : " + obj.toString());
        JSONArray jsonArray = new JSONArray(obj.toString());
        System.out.println("jsonArray : " + jsonArray.toString());

        for (Object jsonObject : jsonArray)
        {
            MonitoringData monitoringData = objectMapper.readValue(jsonObject.toString(), MonitoringData.class);
            System.out.println("monitoringData : " + monitoringData.getComputerName());
            monitoringDataList.add(monitoringData);
        }

        //return monitoringData;
        return monitoringDataList;
    }

    // Validator_Info 요청 함수
    // @입력 값
    // monitoringData: 읽은 모니터링 데이터
    // n: 입력 받은 정수 값
    public void GetValidatorInfo(MonitoringData monitoringData, int n) throws Exception {
        System.out.println("==== getValidatorInfoObj ====");
        Connection connection = null;

        // 풀에 Validator_Info를 요청
        String getValidatorInfoRequest = Ledger.buildGetValidatorInfoRequest(trusteeDid).get();
        String getValidatorInfoResponse = Ledger.signAndSubmitRequest(pool, wallet, trusteeDid,
                getValidatorInfoRequest).get();

        // 받은 Validator_Info 정보를 확인
        JSONObject getValidatorInfoObj = new JSONObject(getValidatorInfoResponse);

        Set<String> keysets = getValidatorInfoObj.keySet();

        for (String key : keysets) {

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

        int checkNodeCount = n + unreachableNodeCount;

        System.out.println("check count : " + n);
        System.out.println("unreachableNodeCount : " + unreachableNodeCount);
        System.out.println("totalNodeCount : " + totalNodeCount);
        System.out.println("reachableNodeCount : " + reachableNodeCount);
        System.out.println("add node number : " + addNodeList.size());
        System.out.println(totalNodeCount + " >= 3 * " + unreachableNodeCount + " + 1");
        System.out.println(totalNodeCount + " >= 3 * " + checkNodeCount + " + 1");

        // 받은 Validator_Info를 통해 풀 정보를 확인 및 검증
        if (totalNodeCount >= 3 * checkNodeCount + 1) {
            // 검증 시 문제가 없는 경우
            System.out.println("Node Check No Problem");
            return;
        }
        else {
            // 검증 시 문제가 있다고 판단되는 경우 노드 추가를 진행
            try {
                System.out.println("Need Node Add");

                // 먼저 컨테이너 생성을 통해 노드 생성
                
                // 노드 연결을 외부 컴퓨터를 통해 실행할 경우
                if (!monitoringData.getComputerIP().equals(GetServerIP()))
                {
                    // 현재 연구실 컴퓨터에 연결
                    System.out.println("Connection Ip is " + monitoringData.getComputerIP());
                    connection = new Connection(monitoringData.getSshUserName(), monitoringData.getComputerIP(),
                            monitoringData.getSshPortNumber(), monitoringData.getSshPassword());
                    connection.connectSSHServer();
                    connection.connectChannelSftp();

                    // 외부 우분투 컴퓨터일 경우 실행
                    RunIndyContainerUbuntu(monitoringData, connection);
                }
                else {
                    // 외부 조작이 필요없는 로컬 윈도우 컴퓨터일 경우 실행
                    RunIndyContainerWindow(monitoringData);
                }
                Thread.sleep(60000);

                // 이후 생성된 노드들을 추가하는 작업 진행
                AddNodeListCheck();

            } catch (JSchException e) {
                System.out.println("error");
                e.printStackTrace();
            } catch (Exception e) {
                System.out.println("error");
                e.printStackTrace();
            } finally {
                System.out.println("==== disConnectSSH ====");
                if (connection != null) connection.disConnectSSH();
            }
        }
    }

    // 로컬 윈도우 컴퓨터의 노드 생성 함수
    // @입력 값
    // monitoringData: 읽은 모니터링 데이터
    public void RunIndyContainerWindow(MonitoringData monitoringData) throws Exception {
        System.out.println("==== RunIndyContainer ====");

        // monitoringData를 읽어 각각의 필요한 값들 정의
        String poolName = monitoringData.getPoolName();
        System.out.println("poolName : " + poolName);
        String containerName = monitoringData.getComputerName() + containerDefultNumber++;
        System.out.println("containerName : " + containerName);
        String containerIp = monitoringData.getComputerIP();
        System.out.println("containerIp : " + containerIp);
        String nodeName = nodeNameDefult;
        System.out.println("nodeName : " + nodeName);
        int nodeNumber = monitoringData.getNodeNumber();
        System.out.println("nodeNumber : " + nodeNumber);
        int startPort = monitoringData.getContainerStartPort() + nodeNumber * 2;
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

        String containerPort = startPort + "-" + endPort;

        // 정의된 값들 기반으로 컨테이너 생성 및 노드 생성 진행
        // 노드의 경우 하나의 컨테이너를 생성한 이후 3개의 노드 생성 및 실행
        cmd = "docker run -itd --name " + containerName + " -p " + containerIp + ":" + containerPort + ":" + containerPort + " -e POOL=\"" + poolName + "\" " + monitoringData.getContainerImage();
        System.out.println("cmd : " + cmd);
        RunWindowCmd(cmd);

        cmd = "docker exec --user root " + containerName + " sh -c \"mkdir /var/lib/indy/" + poolName + "\"";
        System.out.println("cmd : " + cmd);
        RunWindowCmd(cmd);

        cmd = "docker exec --user root " + containerName + " sh -c \"cd etc/indy;sed -i \"s/None/$POOL/g\" indy_config.py\"";
        System.out.println("cmd : " + cmd);
        RunWindowCmd(cmd);

        cmd = "docker cp " + FileUtils.getUserDirectoryPath() + "\\pool_transactions_genesis " + containerName + ":/var/lib/indy/" + poolName;
        System.out.println("cmd : " + cmd);
        RunWindowCmd(cmd);

        // 노드 생성 및 실행
        for(int i = 0; i < 3; i++)
        {
            Node _node = new Node();

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

            // 생성된 노드들을 리스트에 저장
            readyNodeList.add(_node);

            nodePort += 2;
            nodeClientPort += 2;
            _nodeName = nodeName + nodeNumber++;
        }

        int checkNumber = nodeNumber - 1;
        monitoringData.setNodeNumber(checkNumber);
        System.out.println("check nodeNumber : " + checkNumber);
    }

    // 외부 우분투 컴퓨터의 노드 생성 함수
    // @입력 값
    // monitoringData: 읽은 모니터링 데이터
    // connection: ssh 명령어 전달을 위한 연결 값
    public void RunIndyContainerUbuntu(MonitoringData monitoringData, Connection connection) throws Exception {
        System.out.println("==== RunIndyContainer ====");

        if (connection == null)
        {
            System.out.println("connection is null");
            return;
        }

        // monitoringData를 읽어 각각의 필요한 값들 정의
        String poolName = monitoringData.getPoolName();
        System.out.println("poolName : " + poolName);
        String containerName = monitoringData.getComputerName() + containerDefultNumber++;
        System.out.println("containerName : " + containerName);
        String containerIp = monitoringData.getComputerIP();
        System.out.println("containerIp : " + containerIp);
        String nodeName = nodeNameDefult;
        System.out.println("nodeName : " + nodeName);
        int nodeNumber = monitoringData.getNodeNumber();
        System.out.println("nodeNumber : " + nodeNumber);
        int startPort = monitoringData.getContainerStartPort() + nodeNumber * 2;
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

        String containerPort = startPort + "-" + endPort;

        // 정의된 값들 기반으로 컨테이너 생성 및 노드 생성 진행
        // 노드의 경우 하나의 컨테이너를 생성한 이후 3개의 노드 생성 및 실행
        cmd = "docker run -itd --name " + containerName + " -p " + containerIp + ":" + containerPort + ":" + containerPort + " -e POOL=\"" + poolName + "\" " + monitoringData.getContainerImage();
        System.out.println("cmd : " + cmd);
        connection.command(cmd);

        cmd = "docker exec --user root " + containerName + " sh -c \"mkdir /var/lib/indy/" + poolName + "\"";
        System.out.println("cmd : " + cmd);
        connection.command(cmd);

        cmd = "docker exec --user root " + containerName + " sh -c 'cd etc/indy;sed -i \"s/None/$POOL/g\" indy_config.py'";
        System.out.println("cmd : " + cmd);
        connection.command(cmd);

        cmd = "docker cp /root/pool_transactions_genesis " + containerName + ":/var/lib/indy/" + poolName;
        System.out.println("cmd : " + cmd);
        connection.command(cmd);

        // 노드 생성 및 실행
        for(int i = 0; i < 3; i++)
        {
            Node _node = new Node();

            cmd = "docker exec --user root " + containerName + " sh -c \"init_indy_node " + _nodeName +
                    " " + containerIp + " " + nodePort + " " + containerIp + " " + nodeClientPort + " >> " + _nodeName + "_info.txt\"";
            System.out.println("cmd : " + cmd);
            connection.command(cmd);

            cmd = "docker cp " + containerName + ":/" + _nodeName + "_info.txt /root";
            System.out.println("cmd : " + cmd);
            connection.command(cmd);

            connection.download("/root", _nodeName + "_info.txt", FileUtils.getUserDirectoryPath());

            cmd = "docker exec --user root -d " + containerName + " sh -c \"start_indy_node " + _nodeName
                    + " 0.0.0.0 " + nodePort + " 0.0.0.0 " + nodeClientPort + "\"";
            System.out.println("cmd : " + cmd);
            connection.command(cmd);

            _node.setNodeName(_nodeName);
            System.out.println("_nodeName : " + _nodeName);
            _node.setNodeIP(containerIp);
            System.out.println("containerIp : " + containerIp);
            _node.setNodePort(nodePort);
            System.out.println("nodePort : " + nodePort);
            _node.setNodeClientPort(nodeClientPort);
            System.out.println("nodeClientPort : " + nodeClientPort);

            // 생성된 노드들을 리스트에 저장
            readyNodeList.add(_node);

            nodePort += 2;
            nodeClientPort += 2;
            _nodeName = nodeName + nodeNumber++;
        }

        int checkNumber = nodeNumber - 1;
        monitoringData.setNodeNumber(checkNumber);
        System.out.println("check nodeNumber : " + checkNumber);
    }

    // 윈도우 명령어 전달 함수
    // @입력 값
    // cmd: 실행할 윈도우 명령어
    public String RunWindowCmd(String cmd) throws Exception {
        Process p = Runtime.getRuntime().exec("cmd /c " + cmd);
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

        String line = null;

        while((line = br.readLine()) != null) {

            System.out.println(line);
        }

        return line;
    }

    // 추가할 노드 확인 함수
    public void AddNodeListCheck() throws Exception {
        System.out.println("==== AddNodeListCheck ====");

        // 위 노드 생성 및 실행 함수를 통해 리스트에 추가된 노드들을 확인
        while (readyNodeList.size() != 0)
        {
            // 확인된 노드들의 노드 추가 요청을 진행
            Node _node = readyNodeList.get(0);
            System.out.println("NodeName : " + _node.getNodeName());
            
            AddNode(_node);
            addNodeList.add(_node);
            readyNodeList.remove(_node);
        }
    }
    
    // 노드 추가 함수
    // @입력 값
    // node: 추가할 노드 정보
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

        System.out.println("data : " + data);

        String getAddNodeRequest = buildNodeRequest(addNodeDid, dest, data).get();
        nymResponseJson = signAndSubmitRequest(this.pool, this.wallet, addNodeDid, getAddNodeRequest).get();

        System.out.println("nymResponseJson : " + nymResponseJson);

        Thread.sleep(180000);
        System.out.println(node.getNodeName() + " Add Node Clear");
    }

    public String GetServerIP() throws Exception {
        InetAddress server = InetAddress.getLocalHost();

        server_IP = server.getHostAddress();

        System.out.println("Server IP : " + server.getHostAddress());

        return server.getHostAddress();
    }
}

