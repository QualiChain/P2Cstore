package com.thesis.inesc.Commands;

import com.thesis.inesc.Communication;
import com.thesis.inesc.Display;
import com.thesis.inesc.Utilities.PropertiesUtilitites;
import com.thesis.inesc.kademliadht.JKademliaNode;

/**
 * Abstract Command Class
 *
 * @author Marcelo Regra da Silva 
 * @created 06/05/2020
 */
public abstract class Command {

    protected static final String ADD_FILE_COMMAND = "ADDFILECOMMAND";
    protected static final String GET_FILE_COMMAND = "GETFILECOMMAND";
    protected static final String REMOVE_FILE_COMMAND = "REMOVEFILECOMMAND";
    protected static final String UPDATE_STORAGE_COMMAND = "UPDATESTORAGECOMMAND";
    protected static final String GET_FROM_CLOUD_FILE_COMMAND = "GETFROMCLOUDFILECOMMAND";
    protected static final String PROPERTIES_FILE = "./Resources/config.properties";
    protected static final String AWS_S3_CLOUD = "AWSS3CLOUD";
    protected static final String GCP_CLOUD = "GCPCLOUD";

    public void execute(Communication communication, JKademliaNode kademliaNode, CommandContent commandContent){
        if(PropertiesUtilitites.getNumberOfPeers(PROPERTIES_FILE) > 0) {
            if (commandContent.isPeer()) {
                String fileKey = displayUserRequest();
                if(!fileKey.equals("")){
                    commandContent.setDataFile(fileKey);
                    String newFilePath = requestUserInput();
                    commandContent.setNewFilePath(newFilePath);
                }
                else {
                    String fileData = requestUserInput();
                    commandContent.setDataFile(fileData);
                }
                executeRequest(communication, kademliaNode, commandContent);
            }
            else{
                executeRequest(communication, kademliaNode, commandContent);
            }
        }
    }

    public abstract void executeOnDHT(Communication communication, JKademliaNode kademliaNode, CommandContent commandContent);

    public abstract void executeOnClouds(JKademliaNode kademliaNode, CommandContent commandContent);

    //A boolean array in which we have [AWS, GCP, Azure]
    //Example: if it is [true, false, true] the system should send the data to AWS and Azure
    protected boolean[] cloudsToUse(){
        boolean storeInAWSS3 = PropertiesUtilitites.storeInClouds(PROPERTIES_FILE)[0];
        boolean storeInGCP = PropertiesUtilitites.storeInClouds(PROPERTIES_FILE)[1];
        return new boolean[]{storeInAWSS3, storeInGCP};
    }

    protected String displayUserRequest(){
        System.out.print("File key: ");
        System.out.flush();
        return "";
    }

    protected String requestUserInput(){
        return Display.getUserInput();
    }

    public void executeRequest(Communication communication, JKademliaNode kademliaNode, CommandContent commandContent){
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                executeOnClouds(kademliaNode, commandContent);
            }
        });
        t1.start();
        executeOnDHT(communication, kademliaNode, commandContent);
    }
}
