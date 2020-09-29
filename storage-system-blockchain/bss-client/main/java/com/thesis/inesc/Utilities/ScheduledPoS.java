package com.thesis.inesc.Utilities;

import com.thesis.inesc.Communication;
import com.thesis.inesc.Exceptions.DHTFailureException;

import java.util.TimerTask;

/**
 * Schedule PoS thread Class
 *
 * @author Marcelo Regra da Silva 
 * @created 21/06/2020
 */
public class ScheduledPoS extends TimerTask {

    private Communication communication;

    public ScheduledPoS(Communication communication){
        this.communication = communication;
    }

    public void run() {
        String[] files = DHTUtilities.getAllFilesForNode(communication.getClient(), communication.getNode().getNode().getNodeId().stringRepresentation());
        if(files != null && files.length > 0) {
            String owner = communication.getNode().getNode().getNodeId().stringRepresentation();
            for (String key : files) {
                String dhtContent = "";
                try {
                    dhtContent = DHTUtilities.getDataFromDHT(communication.getNode(), key, owner);  //get nodes storing file
                } catch (DHTFailureException e) {
                    System.out.println("File not in DHT");
                }
                String filePath = communication.getClient().getFolderPath() + "/" + owner + "/" + key;
                communication.storageProofRequest(dhtContent, key, owner, filePath);
            }
        }
    }
}
