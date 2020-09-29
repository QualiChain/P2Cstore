package com.thesis.inesc.kademliadht.dht;

import com.thesis.inesc.kademliadht.node.KademliaId;

/**
 * Keeps track of data for a Content stored in the DHT
 * Used by the StorageEntryManager class
 *
 * @author Joshua Kissoon
 * @since 20140226
 */
public interface KademliaStorageEntryMetadata
{

    /**
     * @return The Kademlia ID of this content
     */
    KademliaId getKey();

    /**
     * @return The content's owner ID
     */
    String getOwnerId();

    /**
     * @return The type of this content
     */
    String getType();

    /**
     * @return A hash of the content
     */
    int getContentHash();

    /**
     * @return The last time this content was updated
     */
    long getLastUpdatedTimestamp();

    /**
     * When a node is looking for content, he sends the search criteria in a GetParameter object
     * Here we take this GetParameter object and check if this StorageEntry satisfies the given parameters
     *
     * @param params
     *
     * @return boolean Whether this content satisfies the parameters
     */
    boolean satisfiesParameters(GetParameter params);

    /**
     * @return The timestamp for the last time this content was republished
     */
    long lastRepublished();

    /**
     * Whenever we republish a content or get this content from the network, we update the last republished time
     */
    void updateLastRepublished();
}
