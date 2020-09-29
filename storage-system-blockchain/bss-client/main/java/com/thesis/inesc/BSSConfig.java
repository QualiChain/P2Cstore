package com.thesis.inesc;

import com.thesis.inesc.Exceptions.WrongInputException;
import com.thesis.inesc.Utilities.FilesUtilities;
import com.thesis.inesc.Utilities.HashFunction;
import com.thesis.inesc.Utilities.PropertiesUtilitites;
import com.thesis.inesc.kademliadht.node.KademliaId;
import com.thesis.inesc.kademliadht.node.Node;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Configuration for bootstrap node of the system.
 *
 * @author Marcelo Silva
 * @created 26/02/2020
 * 
 * @Tests: Fully tested
 */
public class BSSConfig {

    private String BOOTSTRAP_IP;
    private final String CONFIG_FILE_PATH = "./Resources/config.properties";
    private final static int BOOTSTRAP_PORT = 15049;

    private static final String[] bootStrapNodeList = {"BootStrapNode0", "BootStrapNode1", "BootStrapNode2", "BootStrapNode3"}; //TODO change to IP/NodeID based affiliation
     /**
     * @return The Kademlia node that serves as the bootstrap node for the network
     *
     * @throws java.net.UnknownHostException
     */
     public Node getBootstrapNode() throws UnknownHostException {
         BOOTSTRAP_IP = PropertiesUtilitites.getPropertyFromFile(CONFIG_FILE_PATH, "BOOTSTRAP_IP");
         byte[] dataBytes;
         for(String bssNode : bootStrapNodeList){
             dataBytes = FilesUtilities.getFileHash(bssNode.getBytes());
             try {
                 return new Node(new KademliaId(HashFunction.fromByteToBase58(dataBytes)), InetAddress.getByName(BOOTSTRAP_IP), BOOTSTRAP_PORT, new BigDecimal("0") /* = 10G */);
             } catch (WrongInputException e) {
                 continue;
             }
         }
         return null;
     }
}
