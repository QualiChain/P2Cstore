package com.thesis.inesc.kademliadht.routing;

import com.thesis.inesc.kademliadht.node.Node;

import java.util.List;

/**
 * A bucket used to store Contacts in the routing table.
 *
 * @author Joshua Kissoon
 * @created 20140215
 */
public interface KademliaBucket
{

    /**
     * Adds a contact to the bucket
     *
     * @param c the new contact
     */
    void insert(Contact c);

    /**
     * Create a new contact and insert it into the bucket.
     *
     * @param n The node to create the contact from
     */
    void insert(Node n);

    /**
     * Checks if this bucket contain a contact
     *
     * @param c The contact to check for
     *
     * @return boolean
     */
    boolean containsContact(Contact c);

    /**
     * Checks if this bucket contain a node
     *
     * @param n The node to check for
     *
     * @return boolean
     */
    boolean containsNode(Node n);

    /**
     * Remove a contact from this bucket.
     * 
     * If there are replacement contacts in the replacement cache,
     * select the last seen one and put it into the bucket while removing the required contact.
     *
     * If there are no contacts in the replacement cache, then we just mark the contact requested to be removed as stale.
     * Marking as stale would actually be incrementing the stale count of the contact.
     *
     * @param c The contact to remove
     *
     * @return Boolean whether the removal was successful.
     */
    boolean removeContact(Contact c);

    /**
     * Remove the contact object related to a node from this bucket
     *
     * @param n The node of the contact to remove
     *
     * @return Boolean whether the removal was successful.
     */
    boolean removeNode(Node n);

    /**
     * Counts the number of contacts in this bucket.
     *
     * @return Integer The number of contacts in this bucket
     */
    int numContacts();

    /**
     * @return Integer The depth of this bucket in the RoutingTable
     */
    int getDepth();

    /**
     * @return An Iterable structure with all contacts in this bucket
     */
    List<Contact> getContacts();
}
