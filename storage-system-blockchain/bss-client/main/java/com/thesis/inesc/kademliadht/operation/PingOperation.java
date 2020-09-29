/**
 * Implementation of the Kademlia Ping operation,
 * This is on hold at the moment since I'm not sure if we'll use ping given the improvements mentioned in the paper.
 *
 * @author Joshua Kissoon
 * @since 20140218
 */
package com.thesis.inesc.kademliadht.operation;

import com.thesis.inesc.kademliadht.KadServer;
import com.thesis.inesc.kademliadht.exceptions.RoutingException;
import com.thesis.inesc.kademliadht.node.Node;

import java.io.IOException;

public class PingOperation implements Operation
{

    private final KadServer server;
    private final Node localNode;
    private final Node toPing;

    /**
     * @param server The Kademlia server used to send & receive messages
     * @param local  The local node
     * @param toPing The node to send the ping message to
     */
    public PingOperation(KadServer server, Node local, Node toPing)
    {
        this.server = server;
        this.localNode = local;
        this.toPing = toPing;
    }

    @Override
    public void execute() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
