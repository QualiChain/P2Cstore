package com.thesis.inesc.UtilitiesTests;

import com.thesis.inesc.Utilities.FilesUtilities;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GetFileDataTest {

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
    public void getFileDataTest(){
        byte[] fileData = FilesUtilities.getFileData(FilesUtilities.getFileInputStream(FILE_NAME));
        String value = new String(fileData);
        Assert.assertEquals(MESSAGE, value);
        System.out.println("\n-------------------\nGet File Data Test Passed\n-------------------\n");
    }

    @After
    public void cleanUp(){
        file.delete();
    }
}
