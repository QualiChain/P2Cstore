package com.thesis.inesc.kademliadht.operation;

import com.thesis.inesc.kademliadht.KadConfiguration;
import com.thesis.inesc.kademliadht.KadServer;
import com.thesis.inesc.kademliadht.KademliaNode;
import com.thesis.inesc.kademliadht.dht.JKademliaStorageEntry;
import com.thesis.inesc.kademliadht.dht.KademliaDHT;
import com.thesis.inesc.kademliadht.message.Message;
import com.thesis.inesc.kademliadht.message.StoreContentMessage;
import com.thesis.inesc.kademliadht.node.Node;

import java.io.IOException;
import java.util.List;

/**
 * Operation that stores a DHT Content onto the K closest nodes to the content Key
 *
 * @author Joshua Kissoon
 * @since 20140224
 */
public class StoreOperation implements Operation
{

    private final KadServer server;
    private final KademliaNode localNode;
    private final JKademliaStorageEntry storageEntry;
    private final KademliaDHT localDht;
    private final KadConfiguration config;
    private int stored;

    /**
     * @param server
     * @param localNode
     * @param storageEntry The content to be stored on the DHT
     * @param localDht     The local DHT
     * @param config
     */
    public StoreOperation(KadServer server, KademliaNode localNode, JKademliaStorageEntry storageEntry, KademliaDHT localDht, KadConfiguration config)
    {
        this.server = server;
        this.localNode = localNode;
        this.storageEntry = storageEntry;
        this.localDht = localDht;
        this.config = config;
    }

    @Override
    public synchronized void execute() throws IOException
    {
        /* Get the nodes on which we need to store the content */
        NodeLookupOperation ndlo = new NodeLookupOperation(this.server, this.localNode, this.storageEntry.getContentMetadata().getKey(), this.config);
        ndlo.execute();
        List<Node> nodes = ndlo.getClosestNodes();

        /* Create the message */
        Message msg = new StoreContentMessage(this.localNode.getNode(), this.storageEntry);

        /*Store the message on all of the K-Nodes*/
        for (Node n : nodes)
        {
            if (n.equals(this.localNode.getNode()))
            {
                /* Store the content locally */
                if(this.localDht.store(this.storageEntry)){
                    this.stored = 1;
                }
                else{
                    this.stored = 0;
                }
            }
            else
            {
                /**
                 * @todo Create a receiver that receives a store acknowledgement message to count how many nodes a content have been stored at
                 */
                this.server.sendMessage(n, msg, null);
            }
        }
    }

    /**
     * @return The number of nodes that have stored this content
     *
     * @todo Implement this method
     */
    public int numNodesStoredAt()
    {
        return 1;
    }

    public int stored(){
        return stored;
    }
}
