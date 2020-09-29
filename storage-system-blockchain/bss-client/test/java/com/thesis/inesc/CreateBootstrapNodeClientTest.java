package com.thesis.inesc;

import com.thesis.inesc.kademliadht.JKademliaNode;
import org.junit.Test;

import static org.junit.Assert.*;

public class CreateBootstrapNodeClientTest {

    @Test
    public void testCreateBootStrapClient() {
        Client bootNodeClient = new Client("BootNode");
        JKademliaNode bootNode = bootNodeClient.createBootstrapNode("BootStrapNode0");
        System.out.println(bootNode.getNode().getNodeId());
        assertEquals(String.valueOf(bootNode.getNode().getNodeId()), "BF3W1zJQqxN9VNxXL4hGSs9LZBVkvqYkAprSBwW4Cxr6");
    }
}
