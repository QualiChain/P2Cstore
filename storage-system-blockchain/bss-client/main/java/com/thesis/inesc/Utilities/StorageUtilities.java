package com.thesis.inesc.Utilities;

import com.thesis.inesc.kademliadht.node.Node;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Properties Utilities Class
 *
 * @author Marcelo Regra da Silva 
 * @created 03/05/2020
 */
public class StorageUtilities {

    private static int ONEGIGABYTE = 1073741824; //1073741824 = 1G

    public void updateLendStorage(Node node, double totalStorage){
        node.setTotalStorage(new BigDecimal(String.valueOf(totalStorage)));
        System.out.println("This is the new lended storage to the network: " + fromBytesToGigabytes(node.getTotalStorage().intValue()));
    }

    public static BigDecimal incrementUsedStorage(Node node, long usedStorage){
        int flag = 0;
        while(node.getUsedStorage() == null){
            flag ++;
            if(flag == 10){
                return null;
            }
            continue;
        }
        int alreadyUsedStorage = node.getUsedStorage().intValue();
        System.out.println("Already used storage: " + alreadyUsedStorage);
        int newUsedStorage = new BigInteger(String.valueOf(usedStorage)).intValue();
        node.setUsedStorage(new BigDecimal(String.valueOf(alreadyUsedStorage + newUsedStorage)));
        System.out.println("This is the new used storage: " + node.getUsedStorage() + " bytes");
        System.out.flush();
        return node.getUsedStorage();
    }

    public static BigDecimal decrementUsedStorage(Node node, long usedStorage){
        int flag = 0;
        while(node.getUsedStorage() == null){
            flag ++;
            if(flag == 10){
                return null;
            }
            continue;
        }
        int alreadyUsedStorage = node.getUsedStorage().intValue();
        int newUsedStorage = new BigInteger(String.valueOf(usedStorage)).intValue();
        node.setUsedStorage(new BigDecimal(String.valueOf(alreadyUsedStorage - newUsedStorage)));
        System.out.println("This is the new used storage: " + node.getUsedStorage() + " bytes");
        System.out.flush();
        return node.getUsedStorage();
    }

    public static double fromBytesToGigabytes(double byteValue){
        return byteValue/ONEGIGABYTE;
    }

    public static double fromGigabytesToBytes(double gigabytesValue){
        return gigabytesValue*ONEGIGABYTE;
    }
}
