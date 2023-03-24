package com.export.node;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

// 외부 컴퓨터 사용을 위한 모니터링 데이터 셋
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "Pool_Name",
        "Computer_Name",
        "Computer_IP",
        "Container_Image",
        "Container_Start_Port",
        "Container_End_Port",
        "Node_Number",
        "Max_Node_Number",
        "SSH_User_Name",
        "SSH_Host_IP",
        "SSH_Port_Number",
        "SSH_Password"
})
public class MonitoringData {

    @JsonProperty("Pool_Name")
    private String poolName;
    @JsonProperty("Computer_Name")
    private String computerName;
    @JsonProperty("Computer_IP")
    private String computerIP;
    @JsonProperty("Container_Image")
    private String containerImage;
    @JsonProperty("Container_Start_Port")
    private Integer containerStartPort;
    @JsonProperty("Container_End_Port")
    private Integer containerEndPort;
    @JsonProperty("Node_Number")
    private Integer nodeNumber;
    @JsonProperty("Max_Node_Number")
    private Integer maxNodeNumber;
    @JsonProperty("SSH_User_Name")
    private String sshUserName;
    @JsonProperty("SSH_Host_IP")
    private String sshHostIp;
    @JsonProperty("SSH_Port_Number")
    private Integer sshPortNumber;
    @JsonProperty("SSH_Password")
    private String sshPassword;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     *
     */
    public MonitoringData() {
    }

    /**
     *
     * @param poolName
     * @param computerIP
     * @param computerName
     * @param containerEndPort
     * @param nodeNumber
     * @param containerImage
     * @param maxNodeNumber
     * @param containerStartPort
     * @param sshUserName
     * @param sshHostIp
     * @param sshPortNumber
     * @param sshPassword
     */
    public MonitoringData(String poolName, String computerName, String computerIP, String containerImage,
                          Integer containerStartPort, Integer containerEndPort, Integer nodeNumber,
                          Integer maxNodeNumber, String sshUserName, String sshHostIp, Integer sshPortNumber,
                          String sshPassword) {
        super();
        this.poolName = poolName;
        this.computerName = computerName;
        this.computerIP = computerIP;
        this.containerImage = containerImage;
        this.containerStartPort = containerStartPort;
        this.containerEndPort = containerEndPort;
        this.nodeNumber = nodeNumber;
        this.maxNodeNumber = maxNodeNumber;
        this.sshUserName = sshUserName;
        this.sshHostIp = sshHostIp;
        this.sshPortNumber = sshPortNumber;
        this.sshPassword = sshPassword;
    }

    @JsonProperty("Pool_Name")
    public String getPoolName() {
        return poolName;
    }

    @JsonProperty("Pool_Name")
    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }

    @JsonProperty("Computer_Name")
    public String getComputerName() {
        return computerName;
    }

    @JsonProperty("Computer_Name")
    public void setComputerName(String computerName) {
        this.computerName = computerName;
    }

    @JsonProperty("Computer_IP")
    public String getComputerIP() {
        return computerIP;
    }

    @JsonProperty("Computer_IP")
    public void setComputerIP(String computerIP) {
        this.computerIP = computerIP;
    }

    @JsonProperty("Container_Image")
    public String getContainerImage() {
        return containerImage;
    }

    @JsonProperty("Container_Image")
    public void setContainerImage(String containerImage) {
        this.containerImage = containerImage;
    }

    @JsonProperty("Container_Start_Port")
    public Integer getContainerStartPort() {
        return containerStartPort;
    }

    @JsonProperty("Container_Start_Port")
    public void setContainerStartPort(Integer containerStartPort) {
        this.containerStartPort = containerStartPort;
    }

    @JsonProperty("Container_End_Port")
    public Integer getContainerEndPort() {
        return containerEndPort;
    }

    @JsonProperty("Container_End_Port")
    public void setContainerEndPort(Integer containerEndPort) {
        this.containerEndPort = containerEndPort;
    }

    @JsonProperty("Node_Number")
    public Integer getNodeNumber() {
        return nodeNumber;
    }

    @JsonProperty("Node_Number")
    public void setNodeNumber(Integer nodeNumber) {
        this.nodeNumber = nodeNumber;
    }

    @JsonProperty("Max_Node_Number")
    public Integer getMaxNodeNumber() {
        return maxNodeNumber;
    }

    @JsonProperty("Max_Node_Number")
    public void setMaxNodeNumber(Integer maxNodeNumber) {
        this.maxNodeNumber = maxNodeNumber;
    }
    @JsonProperty("SSH_User_Name")
    public String getSshUserName() {
        return sshUserName;
    }

    @JsonProperty("SSH_User_Name")
    public void setSshUserName(String sshUserName) {
        this.sshUserName = sshUserName;
    }
    @JsonProperty("SSH_Host_IP")
    public String getSshHostIp() {
        return sshHostIp;
    }

    @JsonProperty("SSH_Host_IP")
    public void setSshHostIp(String sshHostIp) {
        this.sshHostIp = sshHostIp;
    }
    @JsonProperty("SSH_Port_Number")
    public Integer getSshPortNumber() {
        return sshPortNumber;
    }

    @JsonProperty("SSH_Port_Number")
    public void setSshPortNumber(Integer sshPortNumber) {
        this.sshPortNumber = sshPortNumber;
    }
    @JsonProperty("SSH_Password")
    public String getSshPassword() {
        return sshPassword;
    }

    @JsonProperty("SSH_Password")
    public void setSshPassword(String sshPassword) {
        this.sshPassword = sshPassword;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}