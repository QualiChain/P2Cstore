package com.thesis.inesc;

import com.thesis.inesc.Exceptions.CouldNotReadLineException;
import com.thesis.inesc.Utilities.FilesUtilities;
import com.thesis.inesc.Utilities.HashFunction;
import com.thesis.inesc.kademliadht.JKademliaNode;
import com.thesis.inesc.kademliadht.dht.KademliaStorageEntryMetadata;
import com.thesis.inesc.kademliadht.node.Node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

/**
 * This class is responsible for display
 * 
 * @author Marcelo Silva
 * @created 26/02/2020
 */
public class Display {

    /**
     *
     * @description This is the display of the help command
     * TODO: Complete with missing operations
     * */
    public void helpCommand(){
        System.out.println(FilesUtilities.ANSI_GREEN + "Help command." + FilesUtilities.ANSI_RESET);
        System.out.println(FilesUtilities.ANSI_GREEN + "List of available commands:" + FilesUtilities.ANSI_RESET);
        System.out.println("    " + FilesUtilities.ANSI_BLUE + "add" + FilesUtilities.ANSI_RESET+" -> This commands allos you to add a file to the system");
        System.out.println("    " + FilesUtilities.ANSI_BLUE + "get"+FilesUtilities.ANSI_RESET+" -> This commands allos you to get a file from the system");
        System.out.println("    " + FilesUtilities.ANSI_BLUE + "get-shared"+FilesUtilities.ANSI_RESET+" -> This commands allos you to get a file shared file owned by other user from the system");
        System.out.println("    " + FilesUtilities.ANSI_BLUE + "delete"+FilesUtilities.ANSI_RESET+" -> This commands allos you to delete the given file from the system");
        System.out.println("    " + FilesUtilities.ANSI_BLUE + "update"+FilesUtilities.ANSI_RESET+" -> This commands allos you to update the given file from the system and replace it with a new one");
        System.out.println("    " + FilesUtilities.ANSI_BLUE + "list-nodes"+FilesUtilities.ANSI_RESET+" -> This command lists all nodes known by the current node");
        System.out.println("    " + FilesUtilities.ANSI_BLUE + "list-routing-table"+FilesUtilities.ANSI_RESET+" -> This command lists the routing table of the current node"); 
        System.out.println("    " + FilesUtilities.ANSI_BLUE + "list-dht"+FilesUtilities.ANSI_RESET+" -> This command lists the dht contents for the current node");
        System.out.println("    " + FilesUtilities.ANSI_BLUE + "get-content-from-dht"+FilesUtilities.ANSI_RESET+" -> This command retrieves content from the DHT for the given key"); 
        System.out.println("    " + FilesUtilities.ANSI_BLUE + "pos-algorithm"+FilesUtilities.ANSI_RESET+" -> This command starts the proof-of-storage algorithm for the present node");       
        System.out.println("    " + FilesUtilities.ANSI_BLUE + "get-available-storage"+FilesUtilities.ANSI_RESET+" -> This command retrives the available storage for the present node");      
        System.out.println("    " + FilesUtilities.ANSI_BLUE + "update-lend-storage"+FilesUtilities.ANSI_RESET+" -> This command updates the lends storage for the present node");     
        System.out.println("    " + FilesUtilities.ANSI_BLUE + "list "+FilesUtilities.ANSI_RESET+"-> This command lists the directory contents");
        System.out.println("    " + FilesUtilities.ANSI_RED + "exit"+FilesUtilities.ANSI_RESET+" -> This command closes the program");
    }

    /**
     *
     * @description This displays the nodes that node kadnode knows about
     *
     * */
    public void listNodesCommand(JKademliaNode kadnode){
        for(Object node : kadnode.getRoutingTable().getAllNodes()){
            Node n = (Node) node;
            System.out.println("Node ID: " + n.getNodeId());
            System.out.println("    -> IP: " + n.getSocketAddress().getAddress().getHostAddress());
            System.out.println("    -> Port: " + n.getSocketAddress().getPort());
        }
    }

    public String requestKeyToTheUser(){
        System.out.print("What is the data hash?: ");
        String dataHash = getUserInput();
        return dataHash;
    }

    public String[] requestKeyAndOwnerIDToTheUser(){
        System.out.print("What is the data hash?: ");
        String dataHash = getUserInput();
        System.out.print("What is the data owner?: ");
        String dataOwner = getUserInput();
        return new String[]{dataHash, dataOwner};
    }

    /**
     *
     * @description This displays the node routing table
     *
     * */
    public void listRoutingTableCommand(JKademliaNode kadnode){
        System.out.println("Node routing table: \n" + kadnode.getRoutingTable());
    }

    public String getDHT(JKademliaNode kadnode) {
        StringBuilder sb = new StringBuilder("[StorageEntry: ");
        for (Iterator<KademliaStorageEntryMetadata> it = kadnode.getDHT().getStorageEntries().iterator(); it.hasNext(); ) {
            KademliaStorageEntryMetadata element = it.next();
            sb.append("{Key: ");
            sb.append(HashFunction.fromByteToBase58(element.getKey().getBytes()));
            sb.append("} ");
            sb.append("{Owner: ");
            sb.append(element.getOwnerId());
            sb.append("} ");
            sb.append("\n              ");
        }
        sb.append("]");
        return sb.toString();
    }

    public static String getUserInput(){
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String msg = "";
        try {
            msg = reader.readLine();
        } catch (IOException e) {
            new CouldNotReadLineException();
        }
        return msg;
    }
}
