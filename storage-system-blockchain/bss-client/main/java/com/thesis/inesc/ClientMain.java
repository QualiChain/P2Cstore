package com.thesis.inesc;

import com.thesis.inesc.Exceptions.CouldNotConnectToBootstrapNodeException;
import com.thesis.inesc.Utilities.PropertiesUtilitites;
import com.thesis.inesc.kademliadht.JKademliaNode;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Random;

/**
 * This class is responsible to launch the client
 * 
 * @author Marcelo Silva
 * @created 20/02/2020
 */
public class ClientMain{

    private static String STORAGEPEER_BSS_FOLDER = "StoragePeer-Bss";
    private static String CLIENT_BSS_FOLDER = "Client-Bss";
    private static String BSS_NODE_FOLDER = "BSS-Node";

    public static void main(final String[] args){
        JKademliaNode kadnode;
        Communication communication;
        if(args.length != 0 && args[0].equals("--bssNode")){
            final BssUser bssUser = isBSSNode(args[1]);
            try {
                final InetAddress inetAddress = InetAddress.getLocalHost();
                System.out.println(inetAddress.getHostAddress());
            } catch (final UnknownHostException e) {
                e.printStackTrace();
            }
            communication = bssUser.getCommunication();
            kadnode = bssUser.getKademliaNode();
            communication.receiveData(kadnode);
        }
        else {
            BssUser bssUser;
            try {
                bssUser = isClientNode(args);
                if(bssUser == null){
                    System.exit(0);
                    return;
                }
            } catch (final CouldNotConnectToBootstrapNodeException e) {
                System.exit(0);
                return;
            }
            communication = bssUser.getCommunication();
            kadnode = bssUser.getKademliaNode();
            communication.receiveData(kadnode);
        }
        final Terminal terminal = new Terminal(kadnode, communication);
        if(args.length > 2 && args[0].equals("--bssNode")) {
            handleArguments(Arrays.copyOfRange(args, 2, args.length), terminal);
        }
        else if(args.length > 1 && !args[0].equals("--bssNode") && !args[0].equals("--storagePeer")){
            handleArguments(args, terminal);
        }
        terminal.storagePeerTerminal("");
        System.exit(0);
    }

    public static void createBssDirectory(final String ownderID){
        final String path = ownderID;
        final File file = new File(path);
        final boolean bool = file.mkdir();
        if(bool){
            System.out.println("Directory created successfully");
        }
    }

    public static BssUser isBSSNode(final String bssNodeID){
        final Client client = createClient(BSS_NODE_FOLDER);
        final Communication communication = new Communication(client);
        final JKademliaNode kadnode = client.createBootstrapNode(bssNodeID);
        return new BssUser(communication, kadnode);
    }

    public static BssUser isClientNode(final String[] args) throws CouldNotConnectToBootstrapNodeException {
        System.out.println("Welcome to the Blockchain storage system");
        if (args.length > 0 && args[0].equals("--storagePeer")) {
            try {
                final InetAddress inetAddress = InetAddress.getLocalHost();
                for(final String peer : getAutorizedPeers()) {
                    if (peer.equals(inetAddress.getHostAddress())) {
                        final byte[] array = new byte[7]; // length is bounded by 7
                        new Random().nextBytes(array);
                        final String hostName = InetAddress.getLocalHost().getHostName();
                        // Random used for same machine and for testing
                        // Random rand = new Random();
                        // int upperbound = 25;
                        // int int_random = rand.nextInt(upperbound);
                        if (args.length > 1 && args[1] != null) {
                            return handleStoragePeer(createClient(STORAGEPEER_BSS_FOLDER
                                    + hostName /* int_random */ /* Random used for same machine */), args[1]);
                        } else {
                            return handleStoragePeer(createClient(STORAGEPEER_BSS_FOLDER
                                    + hostName /* int_random */ /* Random used for same machine */), null);
                        }
                    }
                }
                System.out.println("Need to ask permission to participate in the system.");
                return null;
            } catch (final UnknownHostException e) {
                e.printStackTrace();
                return null;
            }

        } else if (args.length > 0) {
            return handleClient(createClient(CLIENT_BSS_FOLDER), CLIENT_BSS_FOLDER);
        } else {
            System.out.println("Wrong command, try again.");
            return null;
        }
    }

    //TODO: This should access some server to verify the autorized nodes dynamically
    private static String[] getAutorizedPeers() {
        final String authorizedPeersProperties = PropertiesUtilitites.getPropertyFromFile("Resources/authorizedPeers.properties", "authorizedPeers");
        final String[] authorizedPeers = authorizedPeersProperties.split(",");
        return authorizedPeers;
    }

    public static Client createClient(final String ownerID){
        createBssDirectory(ownerID);
        final Client client = new Client(ownerID);
        return client;
    }

    public static BssUser handleStoragePeer(final Client client, final String ownerID) throws CouldNotConnectToBootstrapNodeException {
        final JKademliaNode kadnode = client.initRouting(ownerID, false);
        if(kadnode == null){
            throw new CouldNotConnectToBootstrapNodeException();
        }
        final Communication communication = new Communication(client);
        return new BssUser(communication, kadnode);
    }

    public static BssUser handleClient(final Client client, String ownerID){
        boolean isClient = false;
        final String storedOwnerID = new SaveNodeState().readOwnerIDFromFile("ownerIDClient.txt");
        if(storedOwnerID != null){
            ownerID = storedOwnerID;
            isClient = true;
        }
        JKademliaNode kadnode;
        if(isClient) {
            kadnode = client.initRouting(ownerID, isClient);
        }
        else{
            kadnode = client.initRouting(null, isClient);
        }
        final Communication communication = new Communication(client);
        return new BssUser(communication, kadnode);
    }

    public static void handleArguments(final String[] args, final Terminal terminal) {
        if(args[0].equals("update")) {
            terminal.clientCommandHandler(args[0], args[1], args[2], null);
        }
        else if(args[0].equals("get-shared")){
            terminal.clientCommandHandler(args[0], args[1], null, args[2]);
        }
        else{
            terminal.clientCommandHandler(args[0], args[1], null, null);
        }
        System.exit(0);
    }
}
