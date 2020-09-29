package com.thesis.inesc.CloudStorage;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;

/**
 * Class that stores the blob context
 *
 * @author Marcelo Regra da Silva
 * @created 07/05/2020
 */
public class CloudBlobContext {

    private BlobStoreContext context;

    private BlobStore blobStore;


    public BlobStoreContext getContext() {
        return context;
    }

    public void setContext(BlobStoreContext context) {
        this.context = context;
    }

    public BlobStore getBlobStore() {
        return blobStore;
    }

    public void setBlobStore(BlobStore blobStore) {
        this.blobStore = blobStore;
    }
}
