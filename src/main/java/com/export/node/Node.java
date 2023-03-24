package com.export.node;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

// 노드 정보 데이터 셋
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "Node_Name",
        "Node_IP",
        "Node_Port",
        "Node_Client_Port"
})
public class Node {

    @JsonProperty("Node_Name")
    private String nodeName;
    @JsonProperty("Node_IP")
    private String nodeIP;
    @JsonProperty("Node_Port")
    private Integer nodePort;
    @JsonProperty("Node_Client_Port")
    private Integer nodeClientPort;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     */
    public Node() {
    }

    /**
     * @param nodeName
     * @param nodeIP
     * @param nodePort
     * @param nodeClientPort
     */
    public Node(String nodeName, String nodeIP, Integer nodePort, Integer nodeClientPort) {
        super();
        this.nodeName = nodeName;
        this.nodeIP = nodeIP;
        this.nodePort = nodePort;
        this.nodeClientPort = nodeClientPort;
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

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Node.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("nodeName");
        sb.append('=');
        sb.append(((this.nodeName == null)?"<null>":this.nodeName));
        sb.append(',');
        sb.append("nodeIP");
        sb.append('=');
        sb.append(((this.nodeIP == null)?"<null>":this.nodeIP));
        sb.append(',');
        sb.append("nodePort");
        sb.append('=');
        sb.append(((this.nodePort == null)?"<null>":this.nodePort));
        sb.append(',');
        sb.append("nodeClientPort");
        sb.append('=');
        sb.append(((this.nodeClientPort == null)?"<null>":this.nodeClientPort));
        sb.append(',');
        sb.append("additionalProperties");
        sb.append('=');
        sb.append(((this.additionalProperties == null)?"<null>":this.additionalProperties));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }
}
