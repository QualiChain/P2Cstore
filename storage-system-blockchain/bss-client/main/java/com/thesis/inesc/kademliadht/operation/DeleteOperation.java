package com.thesis.inesc.kademliadht.operation;

import com.thesis.inesc.kademliadht.KadConfiguration;
import com.thesis.inesc.kademliadht.KadServer;
import com.thesis.inesc.kademliadht.KademliaNode;
import com.thesis.inesc.kademliadht.dht.JKademliaStorageEntry;
import com.thesis.inesc.kademliadht.dht.KadContent;
import com.thesis.inesc.kademliadht.dht.KademliaDHT;
import com.thesis.inesc.kademliadht.message.DeleteContentMessage;
import com.thesis.inesc.kademliadht.message.Message;
import com.thesis.inesc.kademliadht.node.Node;

import java.io.IOException;
import java.util.List;

/**
 * Class that Deletes an entrance from DHT
 *
 * @author Marcelo Regra da Silva
 * @since 15/03/2020
 */
public class DeleteOperation implements Operation {

    private final KadServer server;
    private final KademliaNode localNode;
    private final JKademliaStorageEntry storageEntry;
    private final KadContent content;
    private final KademliaDHT localDht;
    private final KadConfiguration config;

    /**
     * @param server
     * @param localNode
     * @param storageEntry The content to be stored on the DHT
     * @param localDht     The local DHT
     * @param config
     */

    public DeleteOperation(KadServer server, KademliaNode localNode, JKademliaStorageEntry storageEntry, KademliaDHT localDht, KadConfiguration config, KadContent content) {
        this.server = server;
        this.localNode = localNode;
        this.storageEntry = storageEntry;
        this.localDht = localDht;
        this.config = config;
        this.content = content;
    }

    @Override
    public synchronized void execute() throws IOException
    {
        /* Get the nodes on which we need to store the content */
        NodeLookupOperation ndlo = new NodeLookupOperation(this.server, this.localNode, this.storageEntry.getContentMetadata().getKey(), this.config);
        ndlo.execute();
        List<Node> nodes = ndlo.getClosestNodes();

        /* Create the message */
        Message msg = new DeleteContentMessage(this.localNode.getNode(), this.storageEntry);

        /*Store the message on all of the K-Nodes*/
        for (Node n : nodes)
        {
            this.server.sendMessage(n, msg, null);
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
}
