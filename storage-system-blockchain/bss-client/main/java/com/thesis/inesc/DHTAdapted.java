package com.thesis.inesc;

import com.google.gson.Gson;
import com.thesis.inesc.Utilities.FilesUtilities;
import com.thesis.inesc.kademliadht.dht.KadContent;
import com.thesis.inesc.kademliadht.node.KademliaId;

/**
 * 
 * @author Marcelo Silva
 * @created 03/03/2020
 */
public class DHTAdapted implements KadContent
{

    public static final transient String TYPE = "DHTAdapted";

    private KademliaId key;
    private String data;
    private String ownerId;
    private final long createTs;
    private long updateTs;

    {
        this.createTs = this.updateTs = System.currentTimeMillis() / 1000L;
    }

    public DHTAdapted()
    {

    }

    public DHTAdapted(KademliaId key, String ownerId, String data)
    {
        this.ownerId = ownerId;
        this.data = data;
        this.key = key;
    }

    public DHTAdapted(String ownerId, String data)
    {
        this.ownerId = ownerId;
        this.data = data;
        byte[] hash = FilesUtilities.getFileHash(data.getBytes());
        this.key = new KademliaId(hash);
    }

    public DHTAdapted(KademliaId key, String ownerId)
    {
        this.key = key;
        this.ownerId = ownerId;
    }

    public void setData(String newData)
    {
        this.data = newData;
        this.setUpdated();
    }

    @Override
    public KademliaId getKey()
    {
        return this.key;
    }

    @Override
    public String getType()
    {
        return TYPE;
    }

    @Override
    public String getOwnerId()
    {
        return this.ownerId;
    }

    @Override
    public byte[] toSerializedForm() {
        Gson gson = new Gson();
        return gson.toJson(this).getBytes();
    }

    @Override
    public KadContent fromSerializedForm(byte[] bytes) {
        Gson gson = new Gson();
        DHTAdapted val = gson.fromJson(new String(bytes), DHTAdapted.class);
        return val;
    }

    /**
     * Set the content as updated
     */
    public void setUpdated()
    {
        this.updateTs = System.currentTimeMillis() / 1000L;
    }

    @Override
    public long getCreatedTimestamp()
    {
        return this.createTs;
    }

    @Override
    public long getLastUpdatedTimestamp()
    {
        return this.updateTs;
    }

    @Override
    public String toString()
    {
        return this.data;
    }
}

