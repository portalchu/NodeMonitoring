package com.export.node;

import com.fasterxml.jackson.annotation.*;


import java.util.HashMap;
import java.util.List;
import java.util.Map;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "Container_Name",
        "Container_IP",
        "Container_Start_Port",
        "Container_End_Port",
        "NodeList"
})
public class NodeInfo {

    @JsonProperty("Container_Name")
    private String containerName;
    @JsonProperty("Container_Image_Name")
    private String containerImageName;
    @JsonProperty("Container_IP")
    private String containerIP;
    @JsonProperty("Container_Start_Port")
    private Integer containerStartPort;
    @JsonProperty("Container_End_Port")
    private Integer containerEndPort;
    @JsonProperty("NodeList")
    private List<Node> nodeList = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     *
     */
    public NodeInfo() {
    }

    /**
     *
     * @param containerName
     * @param containerIP
     * @param containerImageName
     * @param containerEndPort
     * @param nodeList
     * @param containerStartPort
     */
    public NodeInfo(String containerName, String containerImageName, String containerIP,
                    Integer containerStartPort, Integer containerEndPort, List<Node> nodeList) {
        super();
        this.containerName = containerName;
        this.containerImageName = containerImageName;
        this.containerIP = containerIP;
        this.containerStartPort = containerStartPort;
        this.containerEndPort = containerEndPort;
        this.nodeList = nodeList;
    }

    @JsonProperty("Container_Name")
    public String getContainerName() {
        return containerName;
    }

    @JsonProperty("Container_Name")
    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    @JsonProperty("Container_Image_Name")
    public String getContainerImageName() {
        return containerImageName;
    }

    @JsonProperty("Container_Image_Name")
    public void setContainerImageName(String containerImageName) {
        this.containerImageName = containerImageName;
    }

    @JsonProperty("Container_IP")
    public String getContainerIP() {
        return containerIP;
    }

    @JsonProperty("Container_IP")
    public void setContainerIP(String containerIP) {
        this.containerIP = containerIP;
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

    @JsonProperty("NodeList")
    public List<Node> getNodeList() {
        return nodeList;
    }

    @JsonProperty("NodeList")
    public void setNodeList(List<Node> nodeList) {
        this.nodeList = nodeList;
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
        sb.append(NodeInfo.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("containerName");
        sb.append('=');
        sb.append(((this.containerName == null)?"<null>":this.containerName));
        sb.append(',');
        sb.append("containerIP");
        sb.append('=');
        sb.append(((this.containerIP == null)?"<null>":this.containerIP));
        sb.append(',');
        sb.append("containerStartPort");
        sb.append('=');
        sb.append(((this.containerStartPort == null)?"<null>":this.containerStartPort));
        sb.append(',');
        sb.append("containerEndPort");
        sb.append('=');
        sb.append(((this.containerEndPort == null)?"<null>":this.containerEndPort));
        sb.append(',');
        sb.append("nodeList");
        sb.append('=');
        sb.append(((this.nodeList == null)?"<null>":this.nodeList));
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