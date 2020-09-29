package com.thesis.inesc.UtilitiesTests;

import com.thesis.inesc.Utilities.FilesUtilities;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class GetFileInputstreamTest {

    protected File file;
    private static final String FILE_NAME = "file";
    private static final String FAKE_FILE_NAME = "fake_file";

    @Before
    public void setup(){
        file = new File(FILE_NAME);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * It shouldn't return null
     * */
    @Test
    public void getFileInputstreamFileExistsTest() {
        assertNotNull(FilesUtilities.getFileInputStream(FILE_NAME));
        System.out.println("\n-------------------\nGet File Inputsteram When File Exists Test Passed\n-------------------\n");
    }

    /**
     * It should return null
     * */
    @Test
    public void getFileInputstreamFileDoesNotExistTest() {
        assertNull(FilesUtilities.getFileInputStream(FAKE_FILE_NAME));
        System.out.println("\n-------------------\nGet File Inputsteram When Does Not Exist Test Passed\n-------------------\n");
    }

    @After
    public void cleanUp(){
        file.delete();
    }
}
