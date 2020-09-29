package com.thesis.inesc.Commands;

/**
 * Class Command content
 *
 * @author Marcelo Regra da Silva
 * @created 06/05/2020
 */
public class CommandContent {

    private String dataFile;

    private String newFilePath;

    private String sharedKey;

    private boolean isPeer;

    public CommandContent(boolean isPeer){
        this.isPeer = isPeer;
    }

    public CommandContent(String dataFile, String newFilePath, String sharedKey){
        this.dataFile = dataFile;
        this.newFilePath = newFilePath;
        this.sharedKey = sharedKey;
    }

    public CommandContent(String dataFile, String newFilePath){
        new CommandContent(dataFile, newFilePath, null);
    }

    public CommandContent(String dataFile){
        new CommandContent(dataFile, null);
    }

    public String getDataFile() {
        return dataFile;
    }

    public void setDataFile(String dataFile) {
        this.dataFile = dataFile;
    }

    public String getNewFilePath() {
        return newFilePath;
    }

    public void setNewFilePath(String newFilePath) {
        this.newFilePath = newFilePath;
    }

    public String getSharedKey() {
        return sharedKey;
    }

    public void setSharedKey(String sharedKey) {
        this.sharedKey = sharedKey;
    }

    public boolean isPeer() {
        return isPeer;
    }

    public void setPeer(boolean peer) {
        isPeer = peer;
    }
}
