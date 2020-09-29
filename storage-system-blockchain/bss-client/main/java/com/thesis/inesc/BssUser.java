package com.thesis.inesc;

import com.thesis.inesc.kademliadht.JKademliaNode;

/**
 * BSS User Node Object Class
 *
 * @author Marcelo Silva
 * @created 29/04/2020
 * 
 */
public class BssUser {

    private Communication communication;
    private JKademliaNode kademliaNode;

    public BssUser(Communication communication, JKademliaNode kademliaNode){
        this.communication = communication;
        this.kademliaNode = kademliaNode;
    }

    public Communication getCommunication() {
        return communication;
    }

    public JKademliaNode getKademliaNode() {
        return kademliaNode;
    }
}
