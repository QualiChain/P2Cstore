package com.thesis.inesc.Utilities;

import com.thesis.inesc.Commands.AddCommand;
import com.thesis.inesc.Commands.CommandContent;
import com.thesis.inesc.Communication;
import com.thesis.inesc.NodeComputer;
import com.thesis.inesc.kademliadht.node.Node;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * PoS Utilities Class
 *
 * @author Marcelo Regra da Silva 
 * @created 21/06/2020
 */
public class PoSUtilities {

    private Communication communication;

    private static final String PoS_CHALLENGE = "PoSCHALLENGE";

    public PoSUtilities(Communication communication){
        this.communication = communication;
    }

    public void sendChallenge(String dhtContent, String dataHash, String dataOwner, String pathToFile){
        /************************
         * For latency testing  *
         ***********************
        long startTimeEvaluation = System.currentTimeMillis();
        int numberOfNodes = 0;
        /***********************/
        List<NodeComputer> nodes = DHTUtilities.parseDHTContent(dhtContent);
        if(nodes == null){
            System.out.println("No nodes storing the file.");
            return;
        }
        boolean val = false;
        PoSChallenge challengeData = proofOfStorageChallenge(pathToFile);
        for(NodeComputer node : nodes) {
            /************************
            * For latency testing  *
            ***********************
            numberOfNodes++;
            /***********************/
            try {
                communication.createConnection(node.getIp(), node.getPort());
                communication.sendMessage(PoS_CHALLENGE+dataHash+dataOwner);
                String randomString = challengeString((int) (Math.random() * (2) + 5));
                long startTime = System.currentTimeMillis();
                val = sendObject(randomStringAddToData(challengeData, randomString), startTime);
                communication.stopConnection();
            } catch (Exception e) {
                //TODO: Handle in case the node goes down, the receiver node
                e.printStackTrace();
                System.out.println("Node might be down. Continuing...");
                continue;
            }
        }
        if(val){
            synchronized(this){
                checkReplicationAndUpdateDHT(dataHash, dataOwner);
            }
        }
        /************************
         * For latency testing  *
         ***********************
        long endTime = System.currentTimeMillis();

        long timeElapsed = endTime - startTimeEvaluation;

        System.out.println("Execution time in milliseconds: " + timeElapsed + " | with: " + numberOfNodes + " nodes.");
        String fileName = "latency_pos.txt";
        try {
            // Open given file in append mode.
            BufferedWriter out = new BufferedWriter(new FileWriter(fileName, true));
            out.write("Execution time in milliseconds: " + timeElapsed + " | with: " + numberOfNodes + " nodes.\n");
            out.close();
        }
        catch (IOException e) {
            System.out.println("exception occoured" + e);
        }
        /***********************/
    }

    private boolean sendObject(PoSChallenge poSChallenge, long startTime){
        try {
            ObjectOutputStream out2 = new ObjectOutputStream(communication.getClientSocket().getOutputStream());
            out2.writeObject(poSChallenge);
            boolean val = getChallengeResponse(poSChallenge, startTime);
            out2.close();
            return val;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void checkReplicationAndUpdateDHT(String dataHash, String dataOwner){
        System.out.println("Check Replication And Update DHT");
        CommandContent commandContent = new CommandContent(false);
        commandContent.setSharedKey(dataOwner);
        commandContent.setDataFile(communication.getClient().getFolderPath() + "/" + dataOwner + "/" + dataHash);
        new AddCommand().execute(communication, communication.getNode(), commandContent);
    }

    private boolean getChallengeResponse(PoSChallenge poSChallenge, long startTime){
        try {
            DataInputStream in2 = new DataInputStream(communication.getClientSocket().getInputStream());
            String output = in2.readUTF();
            long endTime = System.currentTimeMillis();
            long availableTime = 30000; //30 seconds
            String ipAddress = communication.getClientSocket().getInetAddress().getHostAddress();
            String nodeID = getNodeIdFromIPAddress(ipAddress);
            if((endTime - startTime) > availableTime){
                System.out.println("Time elapsed to long.");
                int counter = FilesUtilities.replaceLine(nodeID+"-"+ipAddress, "Resources/nodesDownCounter.txt");
                if(counter==5){
                    synchronized(this) {
                        handleFaultyNode(nodeID, ipAddress);
                    }
                    return true; //todo: could be removed
                }
                else{
                    return true;
                }
            }
            byte[] c = poSChallenge.getBaos().toByteArray();
            String correctHash = HashFunction.fromByteToBase58(HashFunction.sha256(new String(c) + poSChallenge.getChallengeRandomString()));
            System.out.println("CORRECT HASH: " + output);
            if(output.equals(correctHash)){
                return false;
            }
            else{
                System.out.println("Wrong answer. Handle this case!");
                System.out.println(communication.getClientSocket().getInetAddress().getHostAddress());
                synchronized(this) {
                    handleFaultyNode(nodeID, ipAddress);
                }
                return true;
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
            return true;
        }
    }

    private String getNodeIdFromIPAddress(String ipAddress){
        List<Node> allNodes = communication.getNode().getRoutingTable().getAllNodes();
        for(Node node : allNodes){
            String ipAddressFromNode = node.getSocketAddress().getAddress().getHostAddress();
            if(ipAddressFromNode.equals(ipAddress) && !node.getNodeId().stringRepresentation().equals(communication.getNode().getNode().getNodeId().stringRepresentation())){
                return node.getNodeId().stringRepresentation();
            }
        }
        return "";
    }

    private void handleFaultyNode(String nodeID, String ipAddress){
        System.out.println("Handle Faulty Node");
        FilesUtilities.storeMessageIntoFile(nodeID+":"+ipAddress+"-Faulty", "Resources/faultyNodes.txt");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(communication.getClient().getFolderPath()+"/"+nodeID);
        File file = new File(communication.getClient().getFolderPath()+"/"+nodeID+"/");
        deleteDirectory(file);
    }

    private boolean deleteDirectory(File folderToDelete) {
        File[] allFiles = folderToDelete.listFiles();
        if (allFiles != null) {
            for (File file : allFiles) {
                deleteDirectory(file);
            }
        }
        return folderToDelete.delete();
    }

    private static PoSChallenge proofOfStorageChallenge(String pathToFile){
        int fileSize = Math.toIntExact(new File(pathToFile).length());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] fullFileBytes = new byte[0];
        try {
            fullFileBytes = Files.readAllBytes(Paths.get(pathToFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        PoSChallenge challengeData = null;
        while(baos.size() <=0) {
            challengeData = getRandomChallenge(fileSize, baos, fullFileBytes);
        }
        return challengeData;
    }

    public static PoSChallenge randomStringAddToData(PoSChallenge challengeData, String randomString){
        challengeData.setChallengeRandomString(randomString);
        ByteArrayOutputStream baos = challengeData.getBaos();
        byte[] c = baos.toByteArray();
        return challengeData;
    }

    private static PoSChallenge getRandomChallenge(int fileSize, ByteArrayOutputStream baos, byte[] fullFileBytes){
        List<Integer> intArray = new ArrayList<>();
        int challengeDataSize = 16;//could be fileSize
        while(intArray.size() != challengeDataSize){
            int randomNum = new Random().nextInt(fileSize);
            if (randomNum % 2 != 0) {
                baos.write(fullFileBytes[randomNum]);
                intArray.add(randomNum);
            }
        }
        return new PoSChallenge(baos, intArray, "");
    }

    public static String challengeString(int n){
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            int index = (int)(AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString.charAt(index));
        }
        return sb.toString();
    }
}
