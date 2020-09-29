package com.thesis.inesc.kademliadht;

import com.thesis.inesc.kademliadht.dht.GetParameter;
import com.thesis.inesc.kademliadht.dht.JKademliaStorageEntry;
import com.thesis.inesc.kademliadht.dht.KadContent;
import com.thesis.inesc.kademliadht.dht.KademliaDHT;
import com.thesis.inesc.kademliadht.exceptions.ContentNotFoundException;
import com.thesis.inesc.kademliadht.exceptions.RoutingException;
import com.thesis.inesc.kademliadht.node.Node;
import com.thesis.inesc.kademliadht.routing.KademliaRoutingTable;

import java.io.IOException;
import java.util.NoSuchElementException;

/**
 * The main Kademlia Node on the network, this node manages everything for this local system.
 *
 * @author Joshua Kissoon
 * @since 20140523
 *
 */
public interface KademliaNode
{

    /**
     * Schedule the recurring refresh operation
     */
    void startRefreshOperation();

    /**
     * Stop the recurring refresh operation
     */
    void stopRefreshOperation();

    /**
     * @return Node The local node for this system
     */
    Node getNode();

    /**
     * @return The KadServer used to send/receive messages
     */
    KadServer getServer();

    /**
     * @return The DHT for this kad instance
     */
    KademliaDHT getDHT();

    /**
     * @return The current KadConfiguration object being used
     */
    KadConfiguration getCurrentConfiguration();

    /**
     * Connect to an existing peer-to-peer network.
     *
     * @param n The known node in the peer-to-peer network
     *
     * @throws RoutingException      If the bootstrap node could not be contacted
     * @throws IOException           If a network error occurred
     * @throws IllegalStateException If this object is closed
     * */
    void bootstrap(Node n) throws IOException, RoutingException;

    /**
     * Stores the specified value under the given key
     * This value is stored on K nodes on the network, or all nodes if there are > K total nodes in the network
     *
     * @param content The content to put onto the DHT
     *
     * @return Integer How many nodes the content was stored on
     *
     * @throws IOException
     *
     */
    int put(KadContent content) throws IOException;

    /**
     * Stores the specified value under the given key
     * This value is stored on K nodes on the network, or all nodes if there are > K total nodes in the network
     *
     * @param entry The StorageEntry with the content to put onto the DHT
     *
     * @return Integer How many nodes the content was stored on
     *
     * @throws IOException
     *
     */
    int put(JKademliaStorageEntry entry) throws IOException;

    /**
     * Store a content on the local node's DHT
     *
     * @param content The content to put on the DHT
     *
     * @throws IOException
     */
    void putLocally(KadContent content) throws IOException;

    /**
     * Get some content stored on the DHT
     *
     * @param param The parameters used to search for the content
     *
     * @return DHTContent The content
     *
     * @throws IOException
     * @throws ContentNotFoundException
     */
    JKademliaStorageEntry get(GetParameter param) throws NoSuchElementException, IOException, ContentNotFoundException;

    /**
     * Allow the user of the System to call refresh even out of the normal Kad refresh timing
     *
     * @throws IOException
     */
    void refresh() throws IOException;

    /**
     * @return String The ID of the owner of this local network
     */
    String getOwnerId();

    /**
     * @return Integer The port on which this kad instance is running
     */
    int getPort();

    /**
     * Here we handle properly shutting down the Kademlia instance
     *
     * @param saveState Whether to save the application state or not
     *
     * @throws java.io.FileNotFoundException
     */
    void shutdown(final boolean saveState) throws IOException;

    /**
     * Saves the node state to a text file
     *
     * @throws java.io.FileNotFoundException
     */
    void saveKadState() throws IOException;

    /**
     * @return The routing table for this node.
     */
    KademliaRoutingTable getRoutingTable();

    /**
     * @return The statistician that manages all statistics
     */
    KadStatistician getStatistician();
}
