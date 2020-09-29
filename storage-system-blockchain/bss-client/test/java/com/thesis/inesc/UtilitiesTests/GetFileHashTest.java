package com.thesis.inesc.UtilitiesTests;

import com.thesis.inesc.Utilities.FilesUtilities;
import com.thesis.inesc.Utilities.HashFunction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;

public class GetFileHashTest {

    protected File file;
    private static final String FILE_NAME = "file";
    private static final String MESSAGE = "message";

    @Before
    public void setup(){
        file = new File(FILE_NAME);
        try {
            file.createNewFile();
        } catch (IOException e) {
            System.out.println("An error occurred. Could not create the file. (Setup of test GetFileHashTest)");
        }
        try {
            FileWriter myWriter = new FileWriter(FILE_NAME);
            myWriter.write(MESSAGE);
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred. Could not write to the file. (Setup of test GetFileHashTest)");
        }
    }

    @Test
    public void getFileHashTest(){
        byte[] fileData = FilesUtilities.getFileData(FilesUtilities.getFileInputStream(FILE_NAME));
        assertEquals("CXnA6KUkQyeBMSHYggTNZVUkSzTNH9meUsviAboetFr4", HashFunction.fromByteToBase58(FilesUtilities.getFileHash(fileData)));
        System.out.println("\n-------------------\nGet File Hash Test Passed\n-------------------\n");
    }

    @After
    public void cleanUp(){
        file.delete();
    }

}
