package com.thesis.inesc;

import com.thesis.inesc.kademliadht.JKademliaNode;

import java.io.*;
import java.util.Scanner;

/**
 * Save Node State Class
 * 
 * @author Marcelo Silva
 * @created 22/04/2020
 * 
 */
public class SaveNodeState {

    public void writeObjectToFile(Object serObj, String filepath) {
        try {
            FileOutputStream fileOut = new FileOutputStream(filepath);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(serObj);
            objectOut.close();
            System.out.println("The Object  was succesfully written to a file");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void storeOwnerIdIntoFile(String ownerID, String filePath){
        try {
            File myObj = new File(filePath);
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
            FileWriter myWriter = new FileWriter(filePath);
            myWriter.write(ownerID);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public String readOwnerIDFromFile(String filePath){
        String data = "";
        try {
            File myObj = new File(filePath);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                data = myReader.nextLine();
                System.out.println(data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            //System.out.println("An error occurred.");
            return null;
        }
        return data;
    }

    public void readObjectFromFile(String filepath){
        FileInputStream fi = null;
        try {
            fi = new FileInputStream(new File(filepath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ObjectInputStream oi = null;
        try {
            oi = new ObjectInputStream(fi);
            JKademliaNode kadnode = (JKademliaNode) oi.readObject();
            System.out.println(kadnode.toString());
            oi.close();
            fi.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}
