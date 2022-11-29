package com.export.node;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NodeInfo {

    @JsonProperty("nodeName")
    private String nodeName;

    @JsonProperty("nodeIP")
    private String nodeIP;

    @JsonProperty("nodePort")
    private int nodePort;

    @JsonProperty("nodeCPort")
    private int nodeCPort;

}