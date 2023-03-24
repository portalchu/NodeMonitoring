# 노드 모니터링 시스템
    
Hyperledger indy 기반의 pool이 죽는 것을 방지하고자 만들어진 프로젝트이다. 
특정 pool을 모니터링하며 pool이 죽는 현상을 방지한다.

## pool과 연결

pool과 연결 시 기본적으로 사용자 폴더의 "pool_transactions_genesis" 파일을 읽으며 없을 시 
'\NodeMonitoring\src\main\java\com\export\utils\EnvironmentUtils'의 
'testPoolIp' 값을 통해 IP 값을 가져와 "pool_transactions_genesis" 파일을 만들어
동작한다.

- "pool_transactions_genesis" 파일 경로
  - Window

        C:\Users\사용자이름\

  - Ubuntu

        /home/사용자이름/

현재 모니터링 시스템의 경우 '220.68.5.139'의 pool을 모니터링 한다.

자신만의 pool이 필요할 경우 아래 사이트 확인

- Docker를 사용한 빠른 pool 생성 

https://github.com/hyperledger/indy-node/blob/main/environment/docker/pool/README.md

- von-network를 사용한 pool 생성

https://github.com/bcgov/von-network/blob/main/docs/UsingVONNetwork.md

## 외부 컴퓨터 연결

모니터링 시스템은 외부 컴퓨터와 연결하여 노드 모니터링이 실행되고 있는 컴퓨터 외의의 컴퓨터에도 
새로운 노드를 설치한다. 외부 컴퓨터 정보는 해당 경로에 있다.

    \NodeMonitoring\src\main\resources\Monitoring_Computer_Config.json

## 컨테이너 이미지

모니터링 시스템은 특정 이미지를 기반으로 노드를 생성함으로 이를 위한 이미지가 필요하며 이는 다음과 같이 만들 수 있다.

    cd \NodeMonitoring\docker
    docker load -i indy-container.tar

## 실행 방법

폴더 상단에서 해당 명령어 실행

    // NodeMonitoring 폴더로 이동
    cd NodeMonitoring
    mvn clean install
    mvn exec:java -Dexec.mainClass="com.export.Main"

이후 특정 정수 값 입력을 통해 동작을 확인할 수 있다.

## 동작 방식

전달된 논문을 통해 확인 가능