package com.thesis.inesc;

import com.thesis.inesc.kademliadht.node.Node;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This class is responsible for receiving messages from the communication class
 * 
 * @author Marcelo Silva
 * @created 27/02/2020
 */
public class CommunicationHandler  extends Thread {

    private static final String ADD_FILE_COMMAND = "ADDFILECOMMAND";
    private static final String GET_FILE_COMMAND = "GETFILECOMMAND";
    private static final String REMOVE_FILE_COMMAND = "REMOVEFILECOMMAND";
    private static final String INCREMENT_USED_STORAGE_COMMAND = "INCREMENTUSEDSTORAGECOMMAND";
    private static final String DECREMENT_USED_STORAGE_COMMAND = "DECREMENTUSEDSTORAGECOMMAND";
    private static final String GET_FROM_CLOUD_FILE_COMMAND = "GETFROMCLOUDFILECOMMAND";
    private static final String GET_NODE_STORAGE_VALUES = "GETNODESTORAGEVALUES";
    private static final String PoS_CHALLENGE = "PoSCHALLENGE";

    final DataInputStream dis;
    final DataOutputStream dos;

    private ServerSocket serverSocket;
    private Communication communication;

    public CommunicationHandler(ServerSocket serverSocket, DataInputStream in, DataOutputStream out, Communication communication) {
        this.dis = in;
        this.dos = out;
        this.serverSocket = serverSocket;
        this.communication = communication;
    }

    @Override
    public void run() {
        String received;
        DataInputStream dis;
        while(true){
            try{
                Socket socket = serverSocket.accept();
                dis = new DataInputStream(socket.getInputStream());
                received = dis.readUTF();
                if(received.startsWith(ADD_FILE_COMMAND)){
                    handleAddFileCommand(socket, received);
                } else if(received.startsWith(GET_FILE_COMMAND)){
                    handleGetFileCommand(socket, received);
                } else if(received.startsWith(REMOVE_FILE_COMMAND)){
                    handleRemoveFileCommand(socket, received);
                } else if(received.startsWith(INCREMENT_USED_STORAGE_COMMAND)) {
                    handleIncrementStorageCommand(received);
                } else if(received.startsWith(DECREMENT_USED_STORAGE_COMMAND)) {
                    handleDecrementStorageCommand(received);
                } else if(received.startsWith(GET_FROM_CLOUD_FILE_COMMAND)){
                    String messageContents = received.split(GET_FROM_CLOUD_FILE_COMMAND)[1];
                    String fileKey = messageContents.substring(0, 44);
                    String owner = messageContents.substring(44, 88);
                    String cloud = messageContents.substring(88);
                    communication.getFromCloudFileCommad(socket, fileKey, owner, cloud);
                } else if(received.startsWith(GET_NODE_STORAGE_VALUES)){
                    try {
                        Node node = communication.getNode().getNode();
                        double totalStorage = node.getTotalStorage().doubleValue();
                        double usedStorage = node.getUsedStorage().doubleValue();
                        double lendedStorage = node.getLendedStorage().doubleValue();
                        new ObjectOutputStream(socket.getOutputStream()).writeObject(new StorageValues(usedStorage, totalStorage, lendedStorage));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    communication.getNode().getNode().getTotalStorage();
                } else if(received.startsWith(PoS_CHALLENGE)){
                    handlePoSChallenge(socket, received);
                }
            } catch(IOException e){
                break;
            }
        }
    }

    public void handleAddFileCommand(Socket socket, String received){
        String file = parseMessageContents(received.split(ADD_FILE_COMMAND)[1])[0];
        String ownerNodeId = parseMessageContents(received.split(ADD_FILE_COMMAND)[1])[1];
        communication.handleFileUpload(socket, file, ownerNodeId);
    }

    public void handleGetFileCommand(Socket socket, String received){
        String file = parseMessageContents(received.split(GET_FILE_COMMAND)[1])[0];
        String ownerNodeId = parseMessageContents(received.split(GET_FILE_COMMAND)[1])[1];
        communication.getFileDownload(socket, file, ownerNodeId);
    }

    public void handleRemoveFileCommand(Socket socket, String received){
        String file = parseMessageContents(received.split(REMOVE_FILE_COMMAND)[1])[0];
        String ownerNodeId = parseMessageContents(received.split(REMOVE_FILE_COMMAND)[1])[1];
        communication.removeFileFromStorage(file, ownerNodeId);
    }

    public void handleIncrementStorageCommand(String received){
        String ownerNodeId = parseMessageContents(received.split(INCREMENT_USED_STORAGE_COMMAND)[1])[0];
        String newUsedStorage = parseMessageContents(received.split(INCREMENT_USED_STORAGE_COMMAND)[1])[1];
        communication.getUpdateRoutingTable(newUsedStorage, ownerNodeId, "ADDCOMMAND");
    }

    public void handleDecrementStorageCommand(String received){
        String ownerNodeId = parseMessageContents(received.split(DECREMENT_USED_STORAGE_COMMAND)[1])[0];
        String newUsedStorage = parseMessageContents(received.split(DECREMENT_USED_STORAGE_COMMAND)[1])[1];
        communication.getUpdateRoutingTable(newUsedStorage, ownerNodeId, "DELETECOMMAND");
    }

    public void handlePoSChallenge(Socket socket, String received){
        String file = parseMessageContents(received.split(PoS_CHALLENGE)[1])[0];
        String ownerNodeId = parseMessageContents(received.split(PoS_CHALLENGE)[1])[1];
        communication.getPoSChallenge(socket, file, ownerNodeId);
    }

    public String[] parseMessageContents(String messageContents){
        String ownerNodeId = messageContents.substring(44);
        String file = null;
        file = messageContents.substring(0, 44);
        return new String[]{file, ownerNodeId};
    }
}
