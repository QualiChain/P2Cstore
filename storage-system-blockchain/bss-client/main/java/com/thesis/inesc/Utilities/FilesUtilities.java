package com.thesis.inesc.Utilities;

import com.thesis.inesc.Client;

import java.io.*;
import java.util.Arrays;

/**
 * File Utilities Class
 *
 * @author Marcelo Regra da Silva 
 * @created 29/04/2020
 */
public class FilesUtilities {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    /**
     * @param filePath (String)
     *
     * @return InputStream
     *
     * @tested
     */
    public static InputStream getFileInputStream(String filePath) {
        File file = new File(filePath);
        try {
            InputStream in2 = new FileInputStream(file);
            return in2;
        } catch (FileNotFoundException e) {
            System.err.println("Error: No such file.");
            return null;
        }
    }

    /**
     * @param filePath (String)
     *
     * @return InputStream
     *
     * @tested
     * */
    public static OutputStream getFileOutputStream(String filePath){
        File file = new File(filePath);
        try {
            OutputStream out = new FileOutputStream(file);
            return out;
        } catch (FileNotFoundException e) {
            System.err.println("Error: No such file.");
            return null;
        }
    }

    public static void deleteLocalFile(String filePath){
        File file = new File(filePath);
        file.delete();
    }

    /**
     * @param file (String)
     *
     * @return boolean
     *
     * @tested
     * */
    public static boolean verifyIfFileExists(String file){
        File testFile = new File(file);
        return testFile.exists();
    }

    /**
     * @param intputStream
     *
     * @return byte[]
     *
     * @tested
     * */
    public static byte[] getFileData(InputStream intputStream){
        byte[] byteArray = new byte[1073741824]; //1G
        int bytesCount = 0;

        //Read file data
        while (true) {
            try {
                if (!((bytesCount = intputStream.read(byteArray)) != -1)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return trim(byteArray);
    }

    /**
     * @param bytes
     *
     * @return byte[]
     *
     * @tested
     * */
    public static byte[] trim(byte[] bytes) {
        int i = bytes.length - 1;
        while (i >= 0 && bytes[i] == 0){
            --i;
        }
        return Arrays.copyOf(bytes, i + 1);
    }

    /**
     * @param fileData
     *
     * @return byte[]
     *
     * @tested
     * */
    public static byte[] getFileHash(byte[] fileData){
        byte[] fileHash = HashFunction.sha256(new String(fileData));
        return fileHash;
    }

    public static void storeMessageIntoFile(String message, String filePath){
        try {
            File myObj = new File(filePath);
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
            FileWriter myWriter = new FileWriter(filePath, true);
            myWriter.write(message+"\n");
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static int replaceLine(String userToUpdate, String filePath) {
        BufferedReader file = null;
        try {
            file = new BufferedReader(new FileReader(filePath));
        } catch (Exception e) {
            storeMessageIntoFile(userToUpdate+":"+1, filePath);
            return -1;
        }
        try{
            StringBuffer inputBuffer = new StringBuffer();
            String line;
            String lineToReplace = "";
            while ((line = file.readLine()) != null) {
                if(line.startsWith(userToUpdate)){
                    lineToReplace = line;
                }
                inputBuffer.append(line+'\n');
            }
            file.close();
            if(lineToReplace.equals("")){
                storeMessageIntoFile(userToUpdate+":"+1, filePath);
                return -1;
            }
            else {
                String inputStr = inputBuffer.toString();
                String[] splittedString = lineToReplace.split(":");
                int counter = Integer.parseInt(splittedString[1]) + 1;
                inputStr = inputStr.replaceAll(lineToReplace, userToUpdate + ":" + counter);
                FileOutputStream fileOut = new FileOutputStream(filePath);
                fileOut.write(inputStr.getBytes());
                fileOut.close();
                return counter;
            }
        } catch (Exception e) {
            System.out.println("Problem reading file.");
            return -1;
        }
    }

    public static void storeFileLocally(Client client, String ownerNodeId, String fileName, InputStream in2){
        if(!verifyIfFileExists(client.getFolderPath()+"/"+ownerNodeId+"/"+ fileName) || !client.getNodeID().equals(ownerNodeId)) {
            OutputStream out2 = null;
            try {
                //Creating a File object
                File file = new File(client.getFolderPath() + "/" + ownerNodeId + "/");
                //Creating the directory
                file.mkdir();
                out2 = new FileOutputStream(client.getFolderPath() + "/" + ownerNodeId + "/" + fileName);
            } catch (FileNotFoundException ex) {
                System.out.println("File not found. ");
            }
            try {
                byte[] bytes = new byte[16 * 1024];

                int count;
                while ((count = in2.read(bytes)) > 0) {
                    out2.write(bytes, 0, count);
                }
                assert out2 != null;
                System.out.println(ANSI_GREEN + "\nFile stored: " + fileName + ANSI_RESET);
                out2.close();
                in2.close();
            } catch (IOException eio) {
                System.out.println("ERROR: Can't read file contents. ");
            }
        }
    }
}
