package com.thesis.inesc.Utilities;

import com.thesis.inesc.Exceptions.WrongInputException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Hash Function Class
 *
 * @author Marcelo Regra da Silva 
 * @created 29/04/2020
 */
public class HashFunction {

    /*******************
     *  Hash function  *
     *******************/
    public static byte[] sha256(String data){
        /* Create a MessageDigest */
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        /* Add password bytes to digest */
        md.update(data.getBytes());
        /* Get the hashed bytes */
        return md.digest();
    }

    /**
     * Sha256 with salt
     * */
    public static byte[] sha256(String toHash, String salt) throws NoSuchAlgorithmException
    {
        /* Create a MessageDigest */
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        /* Add password bytes to digest */
        md.update(toHash.getBytes());
        /* Get the hashed bytes */
        return md.digest(salt.getBytes());
    }

    public static boolean verifyChecksum(String filePath, String testChecksum) {
        byte[] fileData = FilesUtilities.getFileData(FilesUtilities.getFileInputStream(filePath));
        byte[] fileHash = FilesUtilities.getFileHash(fileData);
        return fromByteToBase58(fileHash).equals(testChecksum);
    }

    /**
     * @param fileHash
     *
     * @return String
     * */
    public static String fromByteToBase58(byte[] fileHash){
        return io.ipfs.multibase.Base58.encode(fileHash);
    }

    /**
     * @param hash
     *
     * @return byte[]
     * */
    public static byte[] fromBase58ToBytes(String hash) throws WrongInputException {
        try{
            byte[] bytes = io.ipfs.multibase.Base58.decode(hash);
            return bytes;
        } catch(java.lang.IllegalStateException e){
            throw new WrongInputException();
        }
    }
}
