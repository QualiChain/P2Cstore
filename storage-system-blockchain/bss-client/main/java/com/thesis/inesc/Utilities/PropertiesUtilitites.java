package com.thesis.inesc.Utilities;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Properties Utilities Class
 *
 * @author Marcelo Regra da Silva 
 * @created 04/05/2020
 */
public class PropertiesUtilitites {

    public static int getNumberOfPeers(String filePath){
        String numberOfPeers = getPropertyFromFile(filePath, "numberOfPeers");
        if(numberOfPeers == null){
            return -1;
        }
        return Integer.parseInt(numberOfPeers);
    }

    //A boolean array in which we have [AWS, GCP, Azure]
    public static boolean[] storeInClouds(String filePath){
        String storeInS3Cloud = getPropertyFromFile(filePath, "storeInAWSS3");
        String storeInGCPCloud = getPropertyFromFile(filePath, "storeInGCP");
        if(storeInS3Cloud == null){
            storeInS3Cloud = "false";
        }
        if (storeInGCPCloud == null) {
            storeInGCPCloud = "false";
        }
        return new boolean[]{Boolean.parseBoolean(storeInS3Cloud), Boolean.parseBoolean(storeInGCPCloud)};
    }

    public static String getPropertyFromFile(String filePath, String property){
        try (InputStream input = FilesUtilities.getFileInputStream(filePath)) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find " + filePath);
                //TODO: throw new UnableToFindFileException();
                return null;
            }
            prop.load(input);
            return prop.getProperty(property);
        } catch (IOException ex) {
            return null;
        }
    }
}
