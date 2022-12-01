package com.export.node;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "Computer_Name",
        "Computer_IP",
        "Container_Image",
        "Container_Start_Port",
        "Container_End_Port",
        "Node_Number",
        "Max_Node_Number"
})
public class MonitoringData {

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
     * @param computerIP
     * @param computerName
     * @param containerEndPort
     * @param nodeNumber
     * @param containerImage
     * @param maxNodeNumber
     * @param containerStartPort
     */
    public MonitoringData(String computerName, String computerIP, String containerImage, Integer containerStartPort,
                          Integer containerEndPort, Integer nodeNumber, Integer maxNodeNumber) {
        super();
        this.computerName = computerName;
        this.computerIP = computerIP;
        this.containerImage = containerImage;
        this.containerStartPort = containerStartPort;
        this.containerEndPort = containerEndPort;
        this.nodeNumber = nodeNumber;
        this.maxNodeNumber = maxNodeNumber;
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

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}