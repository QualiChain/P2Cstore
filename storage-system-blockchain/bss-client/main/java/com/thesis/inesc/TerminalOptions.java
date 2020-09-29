package com.thesis.inesc;

import com.thesis.inesc.Commands.*;
import com.thesis.inesc.Exceptions.DHTFailureException;
import com.thesis.inesc.Utilities.DHTUtilities;
import com.thesis.inesc.Utilities.FilesUtilities;
import com.thesis.inesc.Utilities.StorageUtilities;
import com.thesis.inesc.kademliadht.JKademliaNode;
import com.thesis.inesc.kademliadht.node.Node;

import static com.thesis.inesc.Utilities.FilesUtilities.ANSI_RED;
import static com.thesis.inesc.Utilities.FilesUtilities.ANSI_RESET;

import java.io.File;

/**
 * Terminal Options Class
 * 
 * @author Marcelo Silva
 * @created 10/03/2020
 */
public class TerminalOptions {

    private JKademliaNode kadnode;
    private Communication communication;
    public static final String ADD_FILE_COMMAND = "ADDFILECOMMAND";
    public static final String GET_FILE_COMMAND = "GETFILECOMMAND";
    public static final String REMOVE_FILE_COMMAND = "REMOVEFILECOMMAND";
    public static final String INCREMENT_USED_STORAGE_COMMAND = "INCREMENTUSEDSTORAGECOMMAND";
    public static final String DECREMENT_USED_STORAGE_COMMAND = "DECREMENTUSEDSTORAGECOMMAND";
    public static final String GET_FROM_CLOUD_FILE_COMMAND = "GETFROMCLOUDFILECOMMAND";
    public static final String GET_NODE_STORAGE_VALUES = "GETNODESTORAGEVALUES";

    TerminalOptions(Communication communication, JKademliaNode kadnode){
        this.communication = communication;
        this.kadnode = kadnode;
    }

    /**
     * @param command
     *
     * @description This methood handles the available commands
     *
     * */
    public void handleCommands(String command, CommandContent commandContent){
        Display display = new Display();
        if(command.equals("help")){
            display.helpCommand();
            return;
        }
        if(command.equals("add")){
            new AddCommand().execute(communication, kadnode, commandContent);
            return;
        }
        if(command.equals("get")){
            new GetCommand().execute(communication, kadnode, commandContent);
            return;
        }
        if(command.equals("delete")){
            new DeleteCommand().execute(communication, kadnode, commandContent);
            return;
        }
        if(command.equals("update")){
            new UpdateCommand().execute(communication, kadnode, commandContent);
            return;
        }
        if(command.equals("get-shared")){
            new GetSharedCommand().execute(communication, kadnode, commandContent);
            return;
        }
        if(command.equals("list-nodes")){
            display.listNodesCommand(kadnode);
            return;
        }
        if(command.equals("list-routing-table")){
            display.listRoutingTableCommand(kadnode);
            return;
        }
        if(command.equals("list-dht")){
            System.out.println(display.getDHT(this.kadnode));
            return;
        }
        if(command.equals("get-content-from-dht")){
            getContentFromDHTCommand();
            return;
        }
        if(command.equals("save-node-state")){
            saveNodeState();
            return;
        }
        if(command.equals("get-node-state")){
            getNodeState();
            return;
        }
        if(command.equals("update-lend-storage")){
            updateLendStorage();
            return;
        }
        if(command.equals("get-available-storage")){
            getAvailableStorage();
            return;
        }
        if(command.equals("get-used-storage")){
            getUsedStorage();
            return;
        }
        if(command.equals("get-total-storage")){
            getTotalStorage();
            return;
        }
        if(command.equals("get-lended-storage")){
            getLendedStorage();
            return;
        }
        if(command.equals("pos-algorithm")){
            executePoSAlgorithm();
            return;
        }
        if(command.equals("list-files")){
            DHTUtilities.getAllFilesForNode(communication.getClient(), kadnode.getNode().getNodeId().stringRepresentation());
            return;
        }
        if(command.equals("list-nodes-storage")){
            listNodesStorage();
            return;
        }
        if(command.equals("get-from-cloud-no-credentials")){
            getFromCloudNoCredentials();
            return;
        }
        if(command.equals("list")){
            listDirectoryFiles();
            return;
        }
        if(command.equals("exit")){
            System.out.println("Thank you! Bye.");
            return;
        }
        else{
            System.out.println(ANSI_RED+"Wrong command."+ANSI_RESET+"\nFor more information type:\n    $ help");
        }
        return;
    }

    public void getFromCloudNoCredentials(){
        System.out.print("File key: ");
        String key = Display.getUserInput();
        System.out.print("Owner: ");
        String owner = Display.getUserInput();
        System.out.print("Cloud: ");
        String cloud = Display.getUserInput();
        communication.requestDataOnInaccessibleClouds(kadnode, key, owner, cloud);
    }

    public void getTotalStorage(){
        System.out.println(StorageUtilities.fromBytesToGigabytes(kadnode.getNode().getTotalStorage().doubleValue()));
    }

    public void getLendedStorage(){
        System.out.println(kadnode.getNode().getLendedStorage().doubleValue());
    }

    public void getUsedStorage(){
        System.out.println("Choose one:");
        System.out.println(" 1 - In Gigabytes");
        System.out.println(" 2 - In Bytes");
        String input = Display.getUserInput();
        double usedStorage;
        if(input.equals("1")) {
            usedStorage = StorageUtilities.fromBytesToGigabytes(kadnode.getNode().getUsedStorage().doubleValue());
        }
        else{
            usedStorage = kadnode.getNode().getUsedStorage().longValue();
        }
        System.out.println(usedStorage);
    }

    public void updateLendStorage(){
        System.out.print("What is the new amount in gigabytes of available storage to lend to the network?: ");
        String newLendStorage = Display.getUserInput();
        new StorageUtilities().updateLendStorage(kadnode.getNode(), StorageUtilities.fromGigabytesToBytes(Double.valueOf(newLendStorage)));
    }

    public void getAvailableStorage(){
        System.out.println("Choose one:");
        System.out.println(" 1 - In Gigabytes");
        System.out.println(" 2 - In Bytes");
        String input = Display.getUserInput();
        double usedStorage;
        double lendStorage;
        if(input.equals("1")) {
            usedStorage = StorageUtilities.fromBytesToGigabytes(kadnode.getNode().getUsedStorage().doubleValue());
            lendStorage = StorageUtilities.fromBytesToGigabytes(kadnode.getNode().getTotalStorage().doubleValue());
        }
        else{
            usedStorage = kadnode.getNode().getUsedStorage().doubleValue();
            lendStorage = kadnode.getNode().getTotalStorage().doubleValue();
        }
        System.out.println("Avaliable storage: " + (lendStorage - usedStorage) + "\nTotal Storage: " + lendStorage + "\nUsed Storage: " + usedStorage);
    }

    public void getContentFromDHTCommand(){
        String dataHash = new Display().requestKeyToTheUser();
        String dataOwner = kadnode.getNode().getNodeId().stringRepresentation();
        try {
            System.out.println("Content Found: " + DHTUtilities.getDataFromDHT(kadnode, dataHash, dataOwner));
        } catch (DHTFailureException e) {
            e.printStackTrace();
        }
    }

    public void saveNodeState(){
        new SaveNodeState().writeObjectToFile(kadnode, "nodeState.txt");
        new SaveNodeState().storeOwnerIdIntoFile(kadnode.getNode().getNodeId().stringRepresentation(), "ownerID.txt");
    }

    public void getNodeState(){
        new SaveNodeState().readObjectFromFile("nodeState.txt");
        String ownerID = new SaveNodeState().readOwnerIDFromFile("ownerID.txt");
        System.out.println(ownerID);
    }

    public void executePoSAlgorithm(){
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

    public void listNodesStorage(){
        for(Object node : kadnode.getRoutingTable().getAllNodes()){
            Node n = (Node) node;
            if(!kadnode.getNode().getNodeId().equals(n.getNodeId())){
                StorageValues storageValues = communication.getNodeStorageValues(n.getSocketAddress());
                System.out.println(storageValues.getLendedStorage());
            }
        }
    }

    public void listDirectoryFiles(){
        String[] fileNames;
        String folderPath = "./";
        File f = new File(folderPath);
        fileNames = f.list();
        for (String fileName : fileNames) {
            File file = new File(folderPath+fileName);
            if(file.isDirectory()){
                System.out.println(FilesUtilities.ANSI_BLUE + fileName + FilesUtilities.ANSI_RESET);
            }
            else{
                System.out.println(fileName);
            }
        }
    }
}
