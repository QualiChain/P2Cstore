package com.thesis.inesc.kademliadht;

/**
 * Interface that defines a KadConfiguration object
 *
 * @author Joshua Kissoon
 * @since 20140329
 */
public interface KadConfiguration
{

    /**
     * @return Interval in milliseconds between execution of RestoreOperations.
     */
    long restoreInterval();

    /**
     * If no reply received from a node in this period (in milliseconds)
     * consider the node unresponsive.
     *
     * @return The time it takes to consider a node unresponsive
     */
    long responseTimeout();

    /**
     * @return Maximum number of milliseconds for performing an operation.
     */
    long operationTimeout();

    /**
     * @return Maximum number of concurrent messages in transit.
     */
    int maxConcurrentMessagesTransiting();

    /**
     * @return K-Value used throughout Kademlia
     */
    int k();

    /**
     * @return Size of replacement cache.
     */
    int replacementCacheSize();

    /**
     * @return # of times a node can be marked as stale before it is actually removed.
     */
    int stale();

    /**
     * Creates the folder in which this node data is to be stored.
     *
     * @param ownerId
     *
     * @return The folder path
     */
    String getNodeDataFolder(String ownerId);

    /**
     * @return Whether we're in a testing or production system.
     */
    boolean isTesting();

    /**
     * @return Whether we're in a testing or production system.
     */
    //FIXME
    boolean isStoragePeer();
}
