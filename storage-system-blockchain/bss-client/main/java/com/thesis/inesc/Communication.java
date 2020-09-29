package com.thesis.inesc;

import com.thesis.inesc.CloudStorage.CloudConnectivity;
import com.thesis.inesc.Exceptions.DHTFailureException;
import com.thesis.inesc.Exceptions.NoSuchFileFoundException;
import com.thesis.inesc.Utilities.*;
import com.thesis.inesc.kademliadht.JKademliaNode;
import com.thesis.inesc.kademliadht.node.Node;

import java.io.*;
import java.math.BigDecimal;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.thesis.inesc.Utilities.FilesUtilities.*;

/**
 * This is the class that handles communications
 * 
 * @author Marcelo Silva
 * @created 27/02/2020
 */
public class Communication {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private DataOutputStream out;
    private DataInputStream in;
    private Client client;
    private JKademliaNode kadnone;
    private static final String AWS_S3_CLOUD = "AWSS3CLOUD";
    private static final String GCP_CLOUD = "GCPCLOUD";

    public Communication(Client client){
        this.client = client;
    }

    public Communication(){
        super();
    }

    public Client getClient(){
        return this.client;
    }

    public Socket getClientSocket(){
        return this.clientSocket;
    }

    public void sendDataTo(JKademliaNode senderNode, String msg) {
        String data = "";
        byte[] fileHash = null;
        String pathToFile = msg.split(TerminalOptions.ADD_FILE_COMMAND)[1];
        int fileSize = Math.toIntExact(new File(pathToFile).length());
        ArrayList<InetSocketAddress> nodes = DHTUtilities.chooseNodesToSendData(senderNode, fileSize);
        if(nodes.isEmpty()){
            System.out.println("No nodes available for storage");
            return;
        }
        if(!verifyIfItExistsAvailableStorageForThisNode(senderNode, fileSize)){
            System.out.println("No storage available to store this file.");
            return;
        }
        int numberOfReplicas = 0;

        for(InetSocketAddress node : nodes) {
            try {
                createConnection(node.getAddress().getHostAddress(), node.getPort());
                fileHash = addFile(node, pathToFile, senderNode);
                InputStream in2 = FilesUtilities.getFileInputStream(pathToFile);
                FilesUtilities.storeFileLocally(client, senderNode.getNode().getNodeId().stringRepresentation(), HashFunction.fromByteToBase58(fileHash), in2);
                data = data + node.toString();
                stopConnection();
                System.out.print(ANSI_CYAN + "bss-terminal $ " + ANSI_RESET);
                System.out.flush();
                numberOfReplicas++;
            } catch (NoSuchFileFoundException e) {
                System.out.println("Something went wrong, the program closed unexpectedly.");
                System.exit(0);
                return;
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
        DHTUtilities.addDataToDHT(senderNode, data, fileHash, fileSize*numberOfReplicas);
    }

    public void storageProofRequest(String dhtContent, String dataHash, String dataOwner, String pathToFile){
        new PoSUtilities(this).sendChallenge(dhtContent, dataHash, dataOwner, pathToFile);
    }

    public void getPoSChallenge(Socket uploadFileSocket, String dataHash, String dataOwner){
        try {
            ObjectInputStream in2 = new ObjectInputStream(uploadFileSocket.getInputStream());
            PoSChallenge poSChallengeIndexList = (PoSChallenge) in2.readObject();
            byte[] fullFileBytes = new byte[0];
            try {
                fullFileBytes = Files.readAllBytes(Paths.get(this.client.getFolderPath()+ "/" + dataOwner + "/" + dataHash));
            } catch (IOException e) {
                e.printStackTrace();
            }
            String res = "";
            for(Integer val : poSChallengeIndexList.getIntArray()){
                byte b = fullFileBytes[val];
                res += (char)b;
            }
            String hashChallenge = res+poSChallengeIndexList.getChallengeRandomString();
            sendChallengeResponse(uploadFileSocket, HashFunction.fromByteToBase58(HashFunction.sha256(hashChallenge)));
            //System.out.println("Challenge response sent.");
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("Can't get socket input stream. ");
        }
    }

    public void sendChallengeResponse(Socket uploadFileSocket, String challengeHash){
        try {
            DataOutputStream out2 = new DataOutputStream(uploadFileSocket.getOutputStream());
            out2.writeUTF(challengeHash);
            out2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean verifyIfItExistsAvailableStorageForThisNode(JKademliaNode node, int fileSize){
        double usedStorage = node.getNode().getUsedStorage().doubleValue();
        double totalStorage = node.getNode().getTotalStorage().doubleValue();
        System.out.println("File size: " + fileSize + "  Used Storage: " + usedStorage + "   Total Storage: " + totalStorage);
        return usedStorage + (double) fileSize <= totalStorage;
    }

    public byte[] addFile(InetSocketAddress node, String pathToFile, JKademliaNode senderNode) throws Exception, NoSuchFileFoundException {
        InputStream in2 = FilesUtilities.getFileInputStream(pathToFile);
        if(!(in2 == null)){
            byte[] fileData = FilesUtilities.getFileData(in2);
            in2.close();
            byte[] fileHash = FilesUtilities.getFileHash(fileData);
            sendMessage(TerminalOptions.ADD_FILE_COMMAND+
                    HashFunction.fromByteToBase58(fileHash)+senderNode.getNode().getNodeId().stringRepresentation());
            sendFileUpload(node, senderNode, pathToFile, fileHash);
            return fileHash;
        }
        throw new NoSuchFileFoundException();
    }

    public void receiveData(JKademliaNode kadnode){
        this.kadnone = kadnode;
        try {
            serverSocket = new ServerSocket(kadnode.getPort());
            Thread t = new CommunicationHandler(serverSocket, in, out, this);
            //System.out.println("Number of active threads from the given thread: " + Thread.activeCount());
            t.start();
        } catch (IOException e) {
            System.out.println("Something went wrong, try again.");
            System.exit(0);
            //e.printStackTrace();
        }
    }

    public void getFileDownload(Socket socket, String file, String ownerID){
        InputStream in2 = FilesUtilities.getFileInputStream(this.client.getFolderPath()+ "/" + ownerID + "/" + file);
        byte[] bytes = new byte[16 * 1024];
        try {
            OutputStream out2 = socket.getOutputStream();
            int count;
            while ((count = in2.read(bytes)) > 0) {
                out2.write(bytes, 0, count);
            }
            out2.close();
            in2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean sendFileUpload(InetSocketAddress node, JKademliaNode senderNode, String pathToFile, byte[] fileHash){
        byte[] bytes = new byte[16 * 1024];
        InputStream in2 = FilesUtilities.getFileInputStream(pathToFile);
        try {
            OutputStream out2 = clientSocket.getOutputStream();
            int count;
            while ((count = in2.read(bytes)) > 0) {
                out2.write(bytes, 0, count);
            }
            out2.close();
            in2.close();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return true;
        }
    }

    /**
     *
     *
     * @param dhtContent
     * @param dataHash
     * @param dataOwner
     * */
    public void requestFile(String dhtContent, String dataHash, String dataOwner) throws DHTFailureException {
        List<NodeComputer> nodes = DHTUtilities.parseDHTContent(dhtContent);
        if(nodes == null){
            return;
        }
        for(NodeComputer node : nodes) {
            try {
                createConnection(node.getIp(), node.getPort());
                //Sends the file to the nodes that have it
                sendMessage(TerminalOptions.GET_FILE_COMMAND+dataHash+dataOwner);
                String filePath = getFile(this.clientSocket, dataOwner, dataHash);
                stopConnection();
                if(!HashFunction.verifyChecksum(filePath, dataHash)){
                    System.out.println("The file's integrity has been compromised!");
                }
                return;
            } catch (Exception e) {
                //TODO: Handle in case the node goes down, the receiver node
                System.out.println("Node might be down. Continuing...");
                continue;
            }
        }
    }

    public String getFile(Socket uploadFileSocket, String ownerNodeId, String fileName){
        OutputStream out = null;
        InputStream in = null;
        File file = null;
        try {
            in = uploadFileSocket.getInputStream();
            try {
                //Creating a File object
                file = new File(this.client.getFolderPath()+"/"+ownerNodeId+"/");
                //Creating the directory
                boolean bool = file.mkdir();
                if(bool){
                    System.out.println("Directory created successfully");
                }
                out = new FileOutputStream(this.client.getFolderPath()+"/"+ownerNodeId+"/"+ fileName);
            } catch (FileNotFoundException ex) {
                System.out.println("File not found. ");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Can't get socket input stream. ");
        }
        try {
            byte[] bytes = new byte[16 * 1024];

            int count;
            while ((count = in.read(bytes)) > 0) {
                out.write(bytes, 0, count);
            }
            out.close();
            in.close();
        } catch(IOException eio){
            System.out.println("ERROR: Can't read file contents. ");
        }
        return this.client.getFolderPath()+"/"+ownerNodeId+"/"+ fileName;
    }

    /**
     *
     * SERVER SIDE
     *
     * */
    public void handleFileUpload(Socket uploadFileSocket, String fileName, String ownerNodeId){
        InputStream in2 = null;
        try {
            in2 = uploadFileSocket.getInputStream();
        } catch (IOException ex) {
            System.out.println("Can't get socket input stream. ");
        }
        FilesUtilities.storeFileLocally(client, ownerNodeId, fileName, in2);
        System.out.print(ANSI_CYAN + "bss-terminal $ " + ANSI_RESET);
        System.out.flush();
    }

    public StorageValues getNodeStorageValues(InetSocketAddress inetNode){
        try {
            createConnection(inetNode.getAddress().getHostAddress(), inetNode.getPort());
            //Sends the file to the nodes that have it
            sendMessage(TerminalOptions.GET_NODE_STORAGE_VALUES);
            StorageValues storageValues = (StorageValues) new ObjectInputStream(clientSocket.getInputStream()).readObject();
            stopConnection();
            return storageValues;
        } catch (Exception e) {
            System.out.println("Node might be down. Continuing...");
            return null;
        }
    }

    public JKademliaNode getNode(){
        return kadnone;
    }

    public void sendUpdateRoutingTable(double newUsedStorage, String ownerID, JKademliaNode kadnone, String command){
        for(Object node : kadnone.getRoutingTable().getAllNodes()){
            //FIXME: Add command adds more than once for BootStrapNodei where i > 0
            Node n = (Node) node;
            if(!kadnone.getNode().getNodeId().equals(n.getNodeId())){
                InetSocketAddress inetNode = n.getSocketAddress();
                try {
                    createConnection(inetNode.getAddress().getHostAddress(), inetNode.getPort());
                    //Sends the file to the nodes that have it
                    if(command.equals("ADDCOMMAND")) {
                        sendMessage(TerminalOptions.INCREMENT_USED_STORAGE_COMMAND + ownerID + newUsedStorage);
                    }
                    else if(command.equals("DELETECOMMAND")){
                        sendMessage(TerminalOptions.DECREMENT_USED_STORAGE_COMMAND + ownerID + newUsedStorage);
                    }
                    stopConnection();
                    return;
                } catch (Exception e) {
                    System.out.println("Node might be down. Continuing...");
                    continue;
                }
            }
        }
    }

    public void getUpdateRoutingTable(String newUsedStorage, String ownerNodeId, String command){
        Node node = getNodeByNodeID(ownerNodeId, this.kadnone);
        BigDecimal currentUsedStorage = node.getUsedStorage();
        BigDecimal updatedUsedStorage = new BigDecimal(newUsedStorage);
        if(currentUsedStorage != null && updatedUsedStorage != null) {
            if (currentUsedStorage.doubleValue() < updatedUsedStorage.doubleValue()) {
                node.setUsedStorage(updatedUsedStorage);
            } else if (currentUsedStorage.doubleValue() > updatedUsedStorage.doubleValue()) {
                sendUpdateRoutingTable(currentUsedStorage.doubleValue(), ownerNodeId, this.kadnone, command);
            }
        }
    }

    public Node getNodeByNodeID(String nodeId, JKademliaNode kadnone){
        Node originNode = null;
        for(Object node : kadnone.getRoutingTable().getAllNodes()){
            //FIXME: Add command adds more than once for BootStrapNodei where i > 0
            Node n = (Node) node;
            if(nodeId.equals(n.getNodeId().stringRepresentation())){
                originNode = n;
            }
        }
        return originNode;
    }

    public void requestDataOnInaccessibleClouds(JKademliaNode kademliaNode, String dataHash, String dataOwner, String cloudProvider){
        ArrayList<InetSocketAddress> nodes = DHTUtilities.getAllKnownNodes(kademliaNode);
        for(InetSocketAddress node : nodes) {
            try {
                createConnection(node.getAddress().getHostAddress(), node.getPort());
                sendMessage(TerminalOptions.GET_FROM_CLOUD_FILE_COMMAND+dataHash+dataOwner+cloudProvider);
                getFile(this.clientSocket, dataOwner, dataHash);
                stopConnection();
                System.out.print("bss-termianl $ ");
            } catch (Exception e) {
                continue;
            }
        }
    }

    public void getFromCloudFileCommad(Socket socket, String fileKey, String owner, String cloud){
        String file = null;
        String cloudProvider = "";
        try {
            if(cloud.equals(GCP_CLOUD)){
                cloudProvider = "GCP";
                file = new CloudConnectivity().getDataFromGoogleCloud(fileKey, owner);
            }
            else if(cloud.equals(AWS_S3_CLOUD)) {
                cloudProvider = "AWS S3";
                file = new CloudConnectivity().getDataFromAWSS3Cloud(fileKey, owner);
            }
            else{
                System.out.println("Wrong cloud provider.");
            }
        } catch (NoSuchFileFoundException e) {
            System.out.println("Couldn't access to the cloud " + cloudProvider + ", do not have permissions.");
            System.out.print("bss-termianl $ ");
        }
        try {
            OutputStream out2 = socket.getOutputStream();
            if(file == null){
                out2.write("Error: do not have permissions".getBytes(StandardCharsets.UTF_8));
            } else{
                out2.write(file.getBytes(StandardCharsets.UTF_8));
            }
            out2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createConnection(String IP, int port) throws Exception {
        clientSocket = new Socket(IP, port);
        out = new DataOutputStream(clientSocket.getOutputStream());
        in = new DataInputStream(clientSocket.getInputStream());
    }

    public void sendMessage(String msg) throws Exception {
        out.writeUTF(msg);
        out.flush();
    }

    public String getMessage() throws IOException {
        String response = in.readUTF();
        return response;
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

    public void removeFile(String dhtContent, String dataHash, String dataOwner) {
        List<NodeComputer> nodes = DHTUtilities.parseDHTContent(dhtContent);
        String filePath = this.client.getFolderPath() + "/" + dataOwner + "/" + dataHash;
        int fileSize = Math.toIntExact(new File(filePath).length());
        int numberOfReplicas = 0;
        for(NodeComputer node : nodes) {
            try {
                createConnection(node.getIp(), node.getPort());
                //Sends the file to the nodes that have it
                sendMessage(TerminalOptions.REMOVE_FILE_COMMAND+dataHash+dataOwner);
                stopConnection();
                FilesUtilities.deleteLocalFile(filePath);
                numberOfReplicas++;
            } catch (Exception e) {
                //TODO: Handle in case the node goes down, the receiver node
                e.printStackTrace();
            }
        }
        DHTUtilities.broadCastRoutingTableUpdate(dataOwner, fileSize*numberOfReplicas, kadnone, "DELETECOMMAND");
    }

    public void removeFileFromStorage(String fileName, String ownerNodeId){
        File file = new File(this.client.getFolderPath()+"/"+ownerNodeId+"/"+ fileName);
        if(file.delete())
        {
            System.out.println(ANSI_GREEN + "File deleted successfully" + ANSI_RESET);
            System.out.flush();
        }
        else
        {
            System.out.println(ANSI_RED + "Failed to delete the file" + ANSI_RESET);
            System.out.flush();
        }
        System.out.print(ANSI_CYAN + "bss-terminal $ " + ANSI_RESET);
        System.out.flush();
    }
}
