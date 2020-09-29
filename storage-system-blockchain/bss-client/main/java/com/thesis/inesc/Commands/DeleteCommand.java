package com.thesis.inesc.Commands;

import com.thesis.inesc.CloudStorage.CloudConnectivity;
import com.thesis.inesc.Communication;
import com.thesis.inesc.Exceptions.DHTFailureException;
import com.thesis.inesc.Exceptions.NoSuchFileFoundException;
import com.thesis.inesc.Utilities.DHTUtilities;
import com.thesis.inesc.kademliadht.JKademliaNode;

//Imports used for Testing purposes
//import java.io.BufferedWriter;
//import java.io.FileWriter;
//import java.io.IOException;

/**
 * Class that deletes a file from the system
 *
 * @author Marcelo Regra da Silva
 * @created 06/05/2020
 */
public class DeleteCommand extends Command {

    @Override
    public void executeOnDHT(Communication communication, JKademliaNode kademliaNode, CommandContent commandContent) {
        /************************
         * For latency testing  *
         ***********************
        long startTime = System.currentTimeMillis();
        /***********************/
        String dataOwner = kademliaNode.getNode().getNodeId().stringRepresentation();
        String dataHash = commandContent.getDataFile();
        try {
            String dhtContent = DHTUtilities.getDataFromDHT(kademliaNode, dataHash, dataOwner);
            DHTUtilities.removeDataFromDHT(kademliaNode, dataHash, dataOwner);
            communication.removeFile(dhtContent, dataHash, dataOwner);
        } catch (DHTFailureException e) {
            e.printStackTrace();
        }
        /************************
         * For latency testing  *
         ***********************
        long endTime = System.currentTimeMillis();

        long timeElapsed = endTime - startTime;

        System.out.println("Execution time in milliseconds: " + timeElapsed);
        String fileName = "latency_delete.txt";
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
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                String dataOwner = kademliaNode.getNode().getNodeId().stringRepresentation();
                try {
                    new CloudConnectivity().deleteDataFromAWSS3Cloud(commandContent.getDataFile(), dataOwner);
                } catch (NoSuchFileFoundException e) {
                    System.out.println("Couldn't delete file from AWS S3, do not have permissions.");
                }
                /************************
                 * For latency testing  *
                 ***********************
                long endTime = System.currentTimeMillis(); 

                long timeElapsed = endTime - startTime;

                System.out.println("S3-Execution time in milliseconds: " + timeElapsed);
                String fileName = "latency_delete.txt";
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
        try {
            new CloudConnectivity().deleteFromGoogleCloud(kademliaNode.getNode().getNodeId().stringRepresentation(), commandContent.getDataFile());
        } catch (NoSuchFileFoundException e) {
            System.out.println("Couldn't delete file from GCP, do not have permissions.");
        }
        /************************
         * For latency testing  *
         ***********************
        long endTime = System.currentTimeMillis(); 

        long timeElapsed = endTime - startTime;

        System.out.println("GCP-Execution time in milliseconds: " + timeElapsed);
        String fileName = "latency_delete.txt";
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
}
