package com.thesis.inesc;

import com.thesis.inesc.Commands.CommandContent;
import com.thesis.inesc.Utilities.FilesUtilities;
import com.thesis.inesc.Utilities.ScheduledPoS;
import com.thesis.inesc.kademliadht.JKademliaNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Timer;

/**
 * Terminal Object Class -> Interface choosed for simplicity
 * 
 * @author Marcelo Silva
 * @created 10/03/2020
 */
public class Terminal {

    private JKademliaNode kadnode;
    private Communication communication;

    /**
     * @param kadnode
     * @param communication
     * */
    public Terminal(JKademliaNode kadnode, Communication communication){
        this.kadnode = kadnode;
        this.communication = communication;
    }

    /**
     * @param command
     *
     * @description This methood is the terminal that the user
     *              sees when he interacts with the application
     *
     * */
    public void storagePeerTerminal(String command){
        TerminalOptions terminalOptions = new TerminalOptions(communication, kadnode);
        //Remove the comment if one wants to have schedule PoS
        //startSchedulePoS();
        while(!command.equals("exit")) {
            System.out.print(FilesUtilities.ANSI_CYAN + "bss-terminal $ " + FilesUtilities.ANSI_RESET);
            System.out.flush();
            synchronized (this) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                try {
                    command = reader.readLine();
                    terminalOptions.handleCommands(command, new CommandContent(true));
                } catch (IOException e) {
                    System.out.println("Something went wrong. \nPlease verify your network connectivity and try again later.");
                }
            }
        }
    }

    public void startSchedulePoS(){
        Timer time = new Timer(); // Instantiate Timer Object
        ScheduledPoS st = new ScheduledPoS(communication); // Instantiate SheduledTask class
        time.schedule(st, 0, 10000); // Create Repetitively task for every 10 secs
    }

    /**
     * Test scenario (think about how to make it permanent to clients)
     *
     * */
    public void clientCommandHandler(String command, String filePath, String newFilePath, String sharedKey){
        TerminalOptions terminalOptions = new TerminalOptions(communication, kadnode);
        terminalOptions.handleCommands(command, new CommandContent(filePath, newFilePath, sharedKey));
        new SaveNodeState().storeOwnerIdIntoFile(kadnode.getNode().getNodeId().stringRepresentation(), "ownerIDClient.txt");
    }
}
