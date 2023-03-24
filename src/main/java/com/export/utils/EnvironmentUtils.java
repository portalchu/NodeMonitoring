package com.export.utils;

import org.apache.commons.io.FileUtils;

// 각 설정 값들 지정
public class EnvironmentUtils {
    
    // 풀의 IP를 반환
    static String getTestPoolIP() {
        String testPoolIp = System.getenv("TEST_POOL_IP"); // indy_pool ip
        return testPoolIp != null ? testPoolIp : "220.68.5.139";
        //return testPoolIp != null ? testPoolIp : "192.168.45.155";
    }

    // 각 파일 경로
    public static String getIndyHomePath() {
        return FileUtils.getTempDirectoryPath() + "/.indy_client/";
    }

    public static String getIndyHomePath(String filename) {
        return getIndyHomePath() + filename;
    }

    static String getTmpPath() {
        return FileUtils.getTempDirectoryPath() + "/indy/";
    }

    static String getTmpPath(String filename) {
        return getTmpPath() + filename;
    }
}