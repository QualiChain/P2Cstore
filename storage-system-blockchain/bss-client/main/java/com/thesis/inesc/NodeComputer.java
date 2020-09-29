package com.thesis.inesc;

/**
 * NodeComputer Object Class
 * 
 * @author Marcelo Silva
 * @created 05/03/2020
 */
public class NodeComputer {
    private String ip;
    private int port;

    public NodeComputer(String ip, int port){
        this.ip = ip;
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public String getIp() {
        return ip;
    }
}
