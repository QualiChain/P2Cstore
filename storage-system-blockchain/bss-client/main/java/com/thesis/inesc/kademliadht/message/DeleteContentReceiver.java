package com.thesis.inesc.kademliadht.message;

import com.thesis.inesc.Exceptions.NoSuchFileFoundException;
import com.thesis.inesc.kademliadht.KadServer;
import com.thesis.inesc.kademliadht.KademliaNode;
import com.thesis.inesc.kademliadht.dht.KademliaDHT;
import com.thesis.inesc.kademliadht.exceptions.ContentNotFoundException;


/**
 *
 * @author Marcelo Regra da Silva
 * @since 15/03/2020
 */
public class DeleteContentReceiver implements Receiver{

    private final KadServer server;
    private final KademliaNode localNode;
    private final KademliaDHT dht;

    public DeleteContentReceiver(KadServer server, KademliaNode localNode, KademliaDHT dht)
    {
        this.server = server;
        this.localNode = localNode;
        this.dht = dht;
    }

    @Override
    public void receive(Message incoming, int comm)
    {
        /* It's a StoreContentMessage we're receiving */
        DeleteContentMessage msg = (DeleteContentMessage) incoming;

        /* Insert the message sender into this node's routing table */
        //this.localNode.getRoutingTable().(msg.getOrigin());

        try
        {
            /* Delete this Content from the DHT */
            this.dht.remove(msg.getContent().getContentMetadata());
        } catch (ContentNotFoundException e) {
            //e.printStackTrace();
        } catch (NoSuchFileFoundException e) {
            //e.printStackTrace();
        }

    }

    @Override
    public void timeout(int comm)
    {
        /**
         * This receiver only handles Receiving content when we've received the message,
         * so no timeout will happen with this receiver.
         */
    }
}
