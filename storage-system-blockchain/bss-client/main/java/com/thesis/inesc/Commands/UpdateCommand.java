package com.thesis.inesc.Commands;

import com.thesis.inesc.CloudStorage.CloudConnectivity;
import com.thesis.inesc.Communication;
import com.thesis.inesc.Exceptions.DHTFailureException;
import com.thesis.inesc.Exceptions.NoSuchFileFoundException;
import com.thesis.inesc.Utilities.DHTUtilities;
import com.thesis.inesc.Utilities.FilesUtilities;
import com.thesis.inesc.kademliadht.JKademliaNode;

//This Command was not tested for Evaluation purposes as it is the same as DeleteCommand + AddCommand

/**
 * Class that updates a file on the system
 *
 * @author Marcelo Regra da Silva
 * @created 06/05/2020
 */
public class UpdateCommand extends Command {

    @Override
    public void executeOnDHT(Communication communication, JKademliaNode kademliaNode, CommandContent commandContent) {
        String dataHash = commandContent.getDataFile();
        String dataOwner = kademliaNode.getNode().getNodeId().stringRepresentation();
        try {
            String dhtContent = DHTUtilities.getDataFromDHT(kademliaNode, dataHash, dataOwner);
            DHTUtilities.removeDataFromDHT(kademliaNode, dataHash, dataOwner);
            communication.removeFile(dhtContent, dataHash, dataOwner);
            if(FilesUtilities.verifyIfFileExists(commandContent.getNewFilePath())){
                communication.sendDataTo(kademliaNode, ADD_FILE_COMMAND + commandContent.getNewFilePath());
            }
        } catch (DHTFailureException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void executeOnClouds(JKademliaNode kademliaNode, CommandContent commandContent) {
        String dataOwner = kademliaNode.getNode().getNodeId().stringRepresentation();
        String fileKey = commandContent.getDataFile();
        String newFilePath = commandContent.getNewFilePath();
        boolean[] storeInClouds = cloudsToUse();
        if(FilesUtilities.verifyIfFileExists(newFilePath)) {
            if (storeInClouds[0]) {
                Thread t1 = new Thread(new Runnable() {
                    public void run() {
                        try {
                            new CloudConnectivity().updateDataOnAWSS3Cloud(dataOwner, fileKey, newFilePath);
                        } catch (NoSuchFileFoundException e) {
                            System.out.println("Couldn't update file from AWS S3, do not have permissions.");
                        }
                    }
                });
                t1.start();
            }
            if (storeInClouds[1]) {
                try {
                    new CloudConnectivity().updateDataOnGoogleCloud(dataOwner, fileKey, newFilePath);
                } catch (NoSuchFileFoundException e) {
                    System.out.println("Couldn't update file from GCP, do not have permissions.");
                }
            } else {
                System.out.println("No data added to the clouds, they are disabled.");
            }
        }
    }

    @Override
    protected String displayUserRequest(){
        System.out.print("File key: ");
        String fileKey = requestUserInput();
        System.out.print("What is the path to the new file version?: ");
        return fileKey;
    }
}
