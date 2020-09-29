package com.thesis.inesc.kademliadht.node;

import com.thesis.inesc.kademliadht.message.Streamable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * A Node in the Kademlia network - Contains basic node network information.
 *
 * @author Joshua Kissoon
 * @since 20140202
 * @version 0.1
 */
public class Node implements Streamable, Serializable
{

    private KademliaId nodeId;
    private InetAddress inetAddress;
    private int port;
    private final String strRep;
    private BigDecimal usedStorage;
    private BigDecimal totalStorage;
    private BigDecimal lendedStorage;

    public Node(KademliaId nid, InetAddress ip, int port, BigDecimal totalStorage)
    {
        this.nodeId = nid;
        this.inetAddress = ip;
        this.port = port;
        this.usedStorage = new BigDecimal("0.0");
        this.lendedStorage = new BigDecimal("0.0");
        this.totalStorage = totalStorage;
        this.strRep = this.nodeId.toString();
    }

    public BigDecimal getUsedStorage() {
        return usedStorage;
    }

    public void setUsedStorage(BigDecimal usedStorage) {
        this.usedStorage = usedStorage;
    }

    public BigDecimal getTotalStorage() {
        return totalStorage;
    }

    public void setTotalStorage(BigDecimal totalStorage) {
        this.totalStorage = totalStorage;
    }

    public BigDecimal getLendedStorage() {
        return lendedStorage;
    }

    public void setLendedStorage(BigDecimal lendedStorage) {
        this.lendedStorage = lendedStorage;
    }

    /**
     * Load the Node's data from a DataInput stream
     *
     * @param in
     *
     * @throws IOException
     */
    public Node(DataInputStream in) throws IOException
    {
        this.fromStream(in);
        this.strRep = this.nodeId.toString();
    }

    /**
     * Set the InetAddress of this node
     *
     * @param addr The new InetAddress of this node
     */
    public void setInetAddress(InetAddress addr)
    {
        this.inetAddress = addr;
    }

    /**
     * @return The NodeId object of this node
     */
    public KademliaId getNodeId()
    {
        return this.nodeId;
    }

    /**
     * Creates a SocketAddress for this node
     *
     * @return
     */
    public InetSocketAddress getSocketAddress()
    {
        return new InetSocketAddress(this.inetAddress, this.port);
    }

    @Override
    public void toStream(DataOutputStream out) throws IOException
    {
        /* Add the NodeId to the stream */
        this.nodeId.toStream(out);

        /* Add the Node's IP address to the stream */
        byte[] a = inetAddress.getAddress();
        if (a.length != 4)
        {
            throw new RuntimeException("Expected InetAddress of 4 bytes, got " + a.length);
        }
        out.write(a);

        /* Add the port to the stream */
        out.writeInt(port);
    }

    @Override
    public final void fromStream(DataInputStream in) throws IOException
    {
        /* Load the NodeId */
        this.nodeId = new KademliaId(in);
        /* Load the IP Address */
        byte[] ip = new byte[4];
        in.readFully(ip);
        this.inetAddress = InetAddress.getByAddress(ip);

        /* Read in the port */
        this.port = in.readInt();
        //this.isPeerStorage = in.readBoolean();
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof Node)
        {
            Node n = (Node) o;
            if (n == this)
            {
                return true;
            }
            return this.getNodeId().equals(n.getNodeId());
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return this.getNodeId().hashCode();
    }

    @Override
    public String toString()
    {
        return this.getNodeId().toString();
    }
}
