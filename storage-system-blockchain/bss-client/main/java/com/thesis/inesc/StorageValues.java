package com.thesis.inesc;

import java.io.Serializable;

/**
 * Storage values Class
 * 
 * @author Marcelo Silva
 * @created 11/05/2020
 */
public class StorageValues implements Serializable {

    private static final long serialVersionUID = -6847433850257527105L;
    private double usedStorage;
    private double totalStorage;
    private double lendedStorage;

    StorageValues(double usedStorage, double totalStorage, double lendedStorage){
        setUsedStorage(usedStorage);
        setTotalStorage(totalStorage);
        setLendedStorage(lendedStorage);
    }

    public double getUsedStorage() {
        return usedStorage;
    }

    public void setUsedStorage(double usedStorage) {
        this.usedStorage = usedStorage;
    }

    public double getTotalStorage() {
        return totalStorage;
    }

    public void setTotalStorage(double totalStorage) {
        this.totalStorage = totalStorage;
    }

    public double getLendedStorage() {
        return lendedStorage;
    }

    public void setLendedStorage(double lendedStorage) {
        this.lendedStorage = lendedStorage;
    }
}
