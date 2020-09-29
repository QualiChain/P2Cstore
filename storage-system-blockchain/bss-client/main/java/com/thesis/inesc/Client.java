package com.thesis.inesc;

import com.thesis.inesc.Exceptions.CouldNotConnectToBootstrapNodeException;
import com.thesis.inesc.Exceptions.WrongInputException;
import com.thesis.inesc.Utilities.FilesUtilities;
import com.thesis.inesc.Utilities.HashFunction;
import com.thesis.inesc.Utilities.PropertiesUtilitites;
import com.thesis.inesc.kademliadht.JKademliaNode;
import com.thesis.inesc.kademliadht.dht.KademliaDHT;
import com.thesis.inesc.kademliadht.node.KademliaId;
import com.thesis.inesc.kademliadht.node.Node;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.UnknownHostException;

/**
 * Client class of the system
 *
 * @author Marcelo Silva
 * @created 21/02/2020
 */
public class Client {

    private JKademliaNode kademliaNode = null;
    private static int UDP_PORT = 15049;
    private String ownerID = "";
    private KademliaDHT dht;
    private String nodeID;
    private final String CONFIG_FILE_PATH = "./Resources/config.properties";
    private String storageLendSpace;

    public String getFolderPath() {
        return ownerID;
    }

    Client(String ownerID){
        storageLendSpace = PropertiesUtilitites.getPropertyFromFile(CONFIG_FILE_PATH, "StorageToLend");
        this.ownerID = ownerID;
    }

    /**
     * @return The Kademlia node for this bootstrap node instance
     * */
    protected JKademliaNode createBootstrapNode(String bssNodeID){
        byte[] dataBytes = FilesUtilities.getFileHash(bssNodeID.getBytes());
        System.out.println("Creating bootstrap node...");
        try {
            JKademliaNode kadbootnode = new JKademliaNode(this.getFolderPath(), new KademliaId(HashFunction.fromByteToBase58(dataBytes)), UDP_PORT, new BigDecimal(storageLendSpace));
            System.out.println("Bootstrap node created.");
            nodeID = kadbootnode.getNode().getNodeId().stringRepresentation();
            this.kademliaNode = kadbootnode;
            this.dht = kadbootnode.getDHT();
            return kadbootnode;
        } catch (UnknownHostException e) {
            System.out.println("Error");
            return null;
        } catch (IOException e) {
            System.out.println("Error");
            return null;
        } catch (WrongInputException e) {
            return null;
        }
    }

    /**
     * @param ownerId
     * @param udpPort
     *
     * @return The Kademlia node for this instance
     *
     * @throws IOException
     * */
    public JKademliaNode connectNodeToBootstrapNode(String ownerId, int udpPort, KademliaId kademliaId, BigDecimal totalStorage) throws IOException {
        JKademliaNode kad1 = new JKademliaNode(ownerId, kademliaId, udpPort, totalStorage);
        nodeID = kad1.getNode().getNodeId().stringRepresentation();
        this.dht = kad1.getDHT();
        Node bootStrapNode = new BSSConfig().getBootstrapNode();
        kad1.bootstrap(bootStrapNode);
        return kad1;
    }

    public String getNodeID(){
        return nodeID;
    }

    public JKademliaNode initRouting(String ownerId, boolean client){
        if(kademliaNode == null){
            boolean isConnected = false;
            int trials = 0;
            while(!isConnected && trials < 5){
                try {
                    //Storage Peer
                    if(!client) {
                        //No ownerId pre configured, first login maybe -> Registration
                        if(ownerId==null) {
                            kademliaNode = connectNodeToBootstrapNode(this.getFolderPath(), (int) ((Math.random() * 20000) + 5000), new KademliaId(), new BigDecimal(storageLendSpace));
                        }
                        //Already known user
                        else{
                            kademliaNode = connectNodeToBootstrapNode(this.getFolderPath(), (int) ((Math.random() * 20000) + 5000), new KademliaId(ownerId), new BigDecimal(storageLendSpace));
                        }
                    }
                    //Client
                    else{
                        try {
                            kademliaNode = connectNodeToBootstrapNode(this.getFolderPath(), (int) ((Math.random() * 20000) + 5000), new KademliaId(ownerId), new BigDecimal("0"));
                        } catch (WrongInputException e) {
                            return null;
                        }
                    }
                    isConnected = true;
                } catch (IOException e) {
                    trials++;
                    new CouldNotConnectToBootstrapNodeException(trials);
                } catch (WrongInputException e) {
                    System.out.println("Wrong Peer Node ID");
                    return null;
                }
            }
        }
        return kademliaNode;
    }
}
