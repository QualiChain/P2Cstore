package com.thesis.inesc.kademliadht.dht;


import com.thesis.inesc.kademliadht.node.KademliaId;

/**
 * Any piece of content that needs to be stored on the DHT
 *
 * @author Joshua Kissoon
 *
 * @since 20140224
 */
public interface KadContent
{

    /**
     * @return NodeId The DHT key for this content
     */
    KademliaId getKey();

    /**
     * @return String The type of content
     */
    String getType();

    /**
     * Each content will have an created date
     * This allows systems to know when to delete a content form his/her machine
     *
     * @return long The create date of this content
     */
    long getCreatedTimestamp();

    /**
     * Each content will have an update timestamp
     * This allows the DHT to keep only the latest version of a content
     *
     * @return long The timestamp of when this content was last updated
     */
    long getLastUpdatedTimestamp();

    /**
     * @return The ID of the owner of this content
     */
    String getOwnerId();

    /**
     * Each content needs to be in byte format for transporting and storage,
     * this method takes care of that.
     *
     * Each object is responsible for transforming itself to byte format since the
     * structure of methods may differ.
     *
     * @return The content in byte format
     */
    byte[] toSerializedForm();

    /**
     * Given the Content in byte format, read it
     *
     * @param data The object in byte format
     *
     * @return A new object from the given
     */
    KadContent fromSerializedForm(byte[] data);
}
