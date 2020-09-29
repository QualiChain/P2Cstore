package com.thesis.inesc.Utilities;

import com.thesis.inesc.*;
import com.thesis.inesc.Exceptions.DHTFailureException;
import com.thesis.inesc.Exceptions.WrongInputException;
import com.thesis.inesc.kademliadht.JKademliaNode;
import com.thesis.inesc.kademliadht.dht.GetParameter;
import com.thesis.inesc.kademliadht.dht.KadContent;
import com.thesis.inesc.kademliadht.dht.KademliaStorageEntry;
import com.thesis.inesc.kademliadht.exceptions.ContentNotFoundException;
import com.thesis.inesc.kademliadht.node.KademliaId;
import com.thesis.inesc.kademliadht.node.Node;

import java.io.*;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import static com.thesis.inesc.Utilities.FilesUtilities.*;

/**
 * DHT Utilities Class
 *
 * @author Marcelo Regra da Silva 
 * @created 29/04/2020
 */
public class DHTUtilities {

    private static final String PROPERTIES_FILE = "./Resources/config.properties";

    public static String getDataFromDHT(final JKademliaNode kadnode, final String dataHash, final String ownerID)
            throws DHTFailureException {
        DHTAdapted c = null;
        try {
            c = new DHTAdapted(new KademliaId(dataHash), ownerID);
        } catch (final WrongInputException e) {
            return null;
        } catch (final IllegalArgumentException e1) {
            return null;
        }
        final GetParameter gp = new GetParameter(c.getKey(), DHTAdapted.TYPE);
        gp.setOwnerId(c.getOwnerId());
        KademliaStorageEntry conte = null;
        try {
            conte = kadnode.get(gp);
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final ContentNotFoundException e) {
            System.out.println("Content not found in DHT");
            throw new DHTFailureException();
        }
        final KadContent content = new DHTAdapted().fromSerializedForm(conte.getContent());
        // System.out.println("Content Found: " + content); //Used for debug
        return String.valueOf(content);
    }

    public static void addDataToDHT(final JKademliaNode kadnode, final String data, final byte[] fileHash,
            final int fileSize) {
        KademliaId key;
        DHTAdapted c;
        final String nodeId = kadnode.getNode().getNodeId().stringRepresentation();

        if (fileHash == null) {
            c = new DHTAdapted(nodeId, data); // Create content
        } else {
            key = new KademliaId(fileHash);
            c = new DHTAdapted(key, nodeId, data); // Create content
        }
        try {
            final int value = kadnode.put(c); // Put the content on the network
            if (fileSize != -1 && value == 1) {
                broadCastRoutingTableUpdate(nodeId, fileSize, kadnode, "ADDCOMMAND");
            }
            while (!verifyProperStorage(kadnode, c)) {
                continue;
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean verifyProperStorage(final JKademliaNode kadnode, final DHTAdapted c) {
        final GetParameter gp = new GetParameter(c.getKey(), DHTAdapted.TYPE);
        gp.setOwnerId(c.getOwnerId());
        System.out.println("Verifying proper storage of data on DHT");
        try {
            kadnode.get(gp);
            return true;
        } catch (final IOException e) {
            return false;
        } catch (final ContentNotFoundException e) {
            return false;
        }
    }

    public static void broadCastRoutingTableUpdate(final String nodeId, final int fileSize, final JKademliaNode kadnode,
            final String command) {
        BigDecimal newUsedStorage = null;
        if (command.equals("ADDCOMMAND")) {
            newUsedStorage = StorageUtilities.incrementUsedStorage(new Communication().getNodeByNodeID(nodeId, kadnode),
                    fileSize);
        } else if (command.equals("DELETECOMMAND")) {
            newUsedStorage = StorageUtilities.decrementUsedStorage(new Communication().getNodeByNodeID(nodeId, kadnode),
                    fileSize);
        }
        if (newUsedStorage == null) {
            System.out.println("Error");
            return;
        }
        new Communication().sendUpdateRoutingTable(newUsedStorage.intValue(), nodeId, kadnode, command);
        System.out.print(FilesUtilities.ANSI_CYAN + "bss-terminal $ " + FilesUtilities.ANSI_RESET);
        System.out.flush();
    }

    /**
     * @param dhtContent
     *
     * @return List<NodeComputer>
     *
     */
    public static List<NodeComputer> parseDHTContent(final String dhtContent) {
        if (dhtContent == null) {
            return null;
        }
        final List<NodeComputer> nodes = new ArrayList<>();
        final String[] strings = dhtContent.split("/");
        for (final String s : strings) {
            final String[] connectionData = s.split(":");
            if (!connectionData[0].equals("")) {
                final String ip = connectionData[0];
                final int port = Integer.parseInt(connectionData[1]);
                final NodeComputer tempNode = new NodeComputer(ip, port);
                nodes.add(tempNode);
            }
        }
        return nodes;
    }

    /**
     * @param senderNode (JKademliaNode)
     *
     * @return ArrayList<InetSocketAddress>
     */
    public static ArrayList<InetSocketAddress> chooseNodesToSendData(final JKademliaNode senderNode,
            final int fileSize) {
        final ArrayList<InetSocketAddress> list = new ArrayList<>();
        int count = 0;
        final int numberOfPeers = PropertiesUtilitites.getNumberOfPeers(PROPERTIES_FILE);
        for (final Object node : senderNode.getRoutingTable().getAllNodes()) {
            // FIXME: Add command adds more than once for BootStrapNodei where i > 0
            final Node n = (Node) node;
            final boolean isFaulty = checkIfNodeIsFaulty("Resources/faultyNodes.txt",
                    ((Node) node).getNodeId().stringRepresentation());
            if (!senderNode.getNode().getNodeId().equals(n.getNodeId()) && count < numberOfPeers && !isFaulty) {
                StorageValues storageValues = null;
                if (n.getTotalStorage() == null || n.getLendedStorage() == null || n.getUsedStorage() == null) {
                    storageValues = new Communication().getNodeStorageValues(n.getSocketAddress());
                }
                if (storageValues == null) {
                    count++;
                    list.add(n.getSocketAddress());
                } else if (storageValues.getLendedStorage() + fileSize < storageValues.getTotalStorage()) {
                    count++;
                    list.add(n.getSocketAddress());
                } else {
                    System.out.println("No space to store this data on this node.");
                }
            }
        }
        return list;
    }

    public static boolean checkIfNodeIsFaulty(final String filePath, final String node) {
        BufferedReader file = null;
        try {
            file = new BufferedReader(new FileReader(filePath));
        } catch (final FileNotFoundException e) {
            return false;
        }
        try{
            String line;
            while ((line = file.readLine()) != null) {
                if(line.startsWith(node)){
                    System.out.println(line);
                    return true;
                }
            }
            file.close();
            return false;
        } catch (final Exception e) {
            System.out.println("Problem reading file.");
            return false;
        }
    }

    public static ArrayList<InetSocketAddress> getAllKnownNodes(final JKademliaNode senderNode){
        final ArrayList<InetSocketAddress> list = new ArrayList<>();
        for(final Object node : senderNode.getRoutingTable().getAllNodes()){
            final Node n = (Node) node;
            if(!senderNode.getNode().getNodeId().equals(n.getNodeId())){
                list.add(n.getSocketAddress());
            }
        }
        return list;
    }

    public static String[] getAllFilesForNode(final Client client, final String ownerID){
        String[] pathnames;
        final File f = new File(client.getFolderPath() + "/" + ownerID);
        pathnames = f.list();
        if(pathnames != null) {
            return pathnames;
        }
        return null;
    }

    public static void removeDataFromDHT(final JKademliaNode kadnode, final String dataHash, final String ownerID) {
        DHTAdapted c = null;
        try {
            c = new DHTAdapted(new KademliaId(dataHash), ownerID);
        } catch (final WrongInputException e) {
            return;
        }
        c.toSerializedForm();
        try {
            kadnode.delete(c);
            System.out.println(ANSI_GREEN + "File deleted: " + dataHash + ANSI_RESET);
            return;
        } catch (final IOException e) {
            System.out.println(ANSI_RED + "Something went wrong." + ANSI_RESET);
            return;
        }
    }
}
