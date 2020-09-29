package com.thesis.inesc.UtilitiesTests;

import com.thesis.inesc.Utilities.FilesUtilities;
import org.junit.Assert;
import org.junit.Test;

public class TrimTest {

    @Test
    public void trimTest(){
        byte[] array = new byte[10];
        array[0] = 20;
        array[1] = 10;
        array[2] = 30;
        array[3] = 5;
        Assert.assertEquals(4, FilesUtilities.trim(array).length);
        System.out.println("\n-------------------\nTrim Test Passed\n-------------------\n");
    }
}
