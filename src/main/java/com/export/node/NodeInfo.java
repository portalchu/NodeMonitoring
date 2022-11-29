package com.export.node;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.processing.Generated;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "Node_Name",
        "Node_IP",
        "Node_Port",
        "Node_Client_Port"
})
@Generated("jsonschema2pojo")
public class NodeInfo {

    @JsonProperty("nodeName")
    private String nodeName;

    @JsonProperty("nodeIP")
    private String nodeIP;

    @JsonProperty("nodePort")
    private int nodePort;

    @JsonProperty("nodeClientPort")
    private int nodeClientPort;

    public NodeInfo(String _nodeName, String _nodeIP, int _nodePort, int _nodeClientPort) {
        nodeName = _nodeName;
        nodeIP = _nodeIP;
        nodePort = _nodePort;
        nodeClientPort = _nodeClientPort;
    }

    @JsonProperty("Node_Name")
    public String getNodeName() {
        return nodeName;
    }

    @JsonProperty("Node_Name")
    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    @JsonProperty("Node_IP")
    public String getNodeIP() {
        return nodeIP;
    }

    @JsonProperty("Node_IP")
    public void setNodeIP(String nodeIP) {
        this.nodeIP = nodeIP;
    }

    @JsonProperty("Node_Port")
    public Integer getNodePort() {
        return nodePort;
    }

    @JsonProperty("Node_Port")
    public void setNodePort(Integer nodePort) {
        this.nodePort = nodePort;
    }

    @JsonProperty("Node_Client_Port")
    public Integer getNodeClientPort() {
        return nodeClientPort;
    }

    @JsonProperty("Node_Client_Port")
    public void setNodeClientPort(Integer nodeClientPort) {
        this.nodeClientPort = nodeClientPort;
    }

}