package com.thesis.inesc.Commands;

import com.thesis.inesc.CloudStorage.CloudConnectivity;
import com.thesis.inesc.Communication;
import com.thesis.inesc.Exceptions.NoSuchFileFoundException;
import com.thesis.inesc.Utilities.FilesUtilities;
import com.thesis.inesc.kademliadht.JKademliaNode;

//Imports used for Testing purposes
//import java.io.BufferedWriter;
//import java.io.FileWriter;
//import java.io.IOException;

/**
 * Class that adds a file into the system
 *
 * @author Marcelo Regra da Silva
 * @created 06/05/2020
 */
public class AddCommand extends Command {

    @Override
    public void executeOnDHT(Communication communication, JKademliaNode kademliaNode, CommandContent commandContent) {
        /************************
         * For latency testing  *
         ***********************
        long startTime = System.currentTimeMillis();
        /***********************/
        String filePath = commandContent.getDataFile();
        if(FilesUtilities.verifyIfFileExists(filePath)){
            communication.sendDataTo(kademliaNode, ADD_FILE_COMMAND + filePath);
        }
        /************************
         * For latency testing  *
         ***********************
        long endTime = System.currentTimeMillis();

        long timeElapsed = endTime - startTime;

        System.out.println("Execution time in milliseconds: " + timeElapsed);
        String fileName = "latency_add.txt";
        try {
            // Open given file in append mode.
            BufferedWriter out = new BufferedWriter(new FileWriter(fileName, true));
            out.write("Execution time in milliseconds: " + timeElapsed+"\n");
            out.close();
        }
        catch (IOException e) {
            System.out.println("exception occoured" + e);
        }
        /***********************/
    }

    @Override
    public void executeOnClouds(JKademliaNode kademliaNode, CommandContent commandContent) {
        /************************
         * For latency testing  *
         ***********************
        long startTime = System.currentTimeMillis();
        /***********************/
        String filePath = commandContent.getDataFile();
        boolean[] storeInClouds = cloudsToUse();
        if(FilesUtilities.verifyIfFileExists(filePath)) {
            if (storeInClouds[0]) {
                Thread t1 = new Thread(new Runnable() {
                    public void run() {
                        try {
                            new CloudConnectivity().addDataToAWSS3Cloud(kademliaNode.getNode().getNodeId().stringRepresentation(), filePath);
                            System.out.println("File added to S3 bucket");
                        } catch (NoSuchFileFoundException e) {
                            System.out.println("Couldn't add file to AWS S3, do not have permissions.");
                        }
                        /************************
                         * For latency testing  *
                         ***********************
                        long endTime = System.currentTimeMillis();

                        long timeElapsed = endTime - startTime;

                        System.out.println("S3-Execution time in milliseconds: " + timeElapsed);
                        String fileName = "latency_add.txt";
                        try {
                            // Open given file in append mode.
                            BufferedWriter out = new BufferedWriter(new FileWriter(fileName, true));
                            out.write("S3-Execution time in milliseconds: " + timeElapsed+"\n");
                            out.close();
                        }
                        catch (IOException e) {
                            System.out.println("exception occoured" + e);
                        }
                        /***********************/
                    }
                });
                t1.start();
            }
            if (storeInClouds[1]) {
                try {
                    new CloudConnectivity().addDataToGoogleCloud(kademliaNode.getNode().getNodeId().stringRepresentation(), filePath);
                    System.out.println("File added to GCP bucket");
                } catch (NoSuchFileFoundException e) {
                    System.out.println("Couldn't add file to GCP, do not have permissions.");
                }
                /************************
                 * For latency testing  *
                 ***********************
                long endTime = System.currentTimeMillis(); 

                long timeElapsed = endTime - startTime;

                System.out.println("GCP-Execution time in milliseconds: " + timeElapsed);
                String fileName = "latency_add.txt";
                try {
                    // Open given file in append mode.
                    BufferedWriter out = new BufferedWriter(new FileWriter(fileName, true));
                    out.write("GCP-Execution time in milliseconds: " + timeElapsed+"\n");
                    out.close();
                }
                catch (IOException e) {
                    System.out.println("exception occoured" + e);
                }
                /***********************/
            }
            else{
                System.out.println("No data added to the clouds, they are disabled.");
            }
        }
    }

    @Override
    protected String displayUserRequest() {
        System.out.print("Path to file: ");
        System.out.flush();
        return "";
    }
}
