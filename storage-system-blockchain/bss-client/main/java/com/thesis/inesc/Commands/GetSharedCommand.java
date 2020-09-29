package com.thesis.inesc.Commands;

import com.thesis.inesc.CloudStorage.CloudConnectivity;
import com.thesis.inesc.Communication;
import com.thesis.inesc.Exceptions.DHTFailureException;
import com.thesis.inesc.Exceptions.NoSuchFileFoundException;
import com.thesis.inesc.Utilities.DHTUtilities;
import com.thesis.inesc.Utilities.PropertiesUtilitites;
import com.thesis.inesc.kademliadht.JKademliaNode;

//This Command was not tested for Evaluation purposes as it is basically the same as GetCommand
//In this GetSharedCommand one can share the content with other user

/**
 * Class that get a shared a file from the system
 *
 * @author Marcelo Regra da Silva
 * @created 07/05/2020
 */
public class GetSharedCommand extends Command{

    @Override
    public void execute(Communication communication, JKademliaNode kademliaNode, CommandContent commandContent){
        if(PropertiesUtilitites.getNumberOfPeers(PROPERTIES_FILE) > 0) {
            if (commandContent.isPeer()) {
                String fileKey = displayUserRequest();
                commandContent.setDataFile(fileKey);
                String sharedKey = requestUserInput();
                commandContent.setSharedKey(sharedKey);
                executeRequest(communication, kademliaNode, commandContent);
            }
            else{
                executeRequest(communication, kademliaNode, commandContent);
            }
        }
    }

    @Override
    public void executeOnDHT(Communication communication, JKademliaNode kademliaNode, CommandContent commandContent) {
        try {
            String dhtContent = DHTUtilities.getDataFromDHT(kademliaNode, commandContent.getDataFile(), commandContent.getSharedKey());
            communication.requestFile(dhtContent, commandContent.getDataFile(), commandContent.getSharedKey());
        } catch (DHTFailureException e) {
            //TODO: Handle this exception.
            System.out.println("Something went wrong. DHTFailureException.");
        }
    }

    @Override
    public void executeOnClouds(JKademliaNode kademliaNode, CommandContent commandContent) {
        System.out.println("Aqui estamos!!");
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                try {
                    new CloudConnectivity().getDataFromAWSS3Cloud(commandContent.getDataFile(), commandContent.getSharedKey());
                } catch (NoSuchFileFoundException e) {
                    new Communication().requestDataOnInaccessibleClouds(kademliaNode, commandContent.getDataFile(), commandContent.getSharedKey(), AWS_S3_CLOUD);
                }
            }
        });
        t1.start();
        try {
            new CloudConnectivity().getDataFromGoogleCloud(commandContent.getSharedKey(), commandContent.getDataFile());
        } catch (NoSuchFileFoundException e) {
            new Communication().requestDataOnInaccessibleClouds(kademliaNode, commandContent.getDataFile(), commandContent.getSharedKey(), GCP_CLOUD);
        }
    }

    @Override
    protected String displayUserRequest(){
        System.out.print("File key: ");
        String fileKey = requestUserInput();
        System.out.print("Shared key: ");
        return fileKey;
    }
}
