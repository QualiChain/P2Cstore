package com.thesis.inesc.Utilities;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.List;

/**
 * PoS Challange Object Class
 *
 * @author Marcelo Regra da Silva 
 * @created 27/05/2020
 */
public class PoSChallenge implements Serializable{
    private static final long serialVersionUID = 6817230554322712862L;
    private List<Integer> intArray;
    private static ByteArrayOutputStream baos;
    private String challengeRandomString;

    public PoSChallenge(ByteArrayOutputStream baos, List<Integer> intArray, String challengeRandomString){
        setBaos(baos);
        setIntArray(intArray);
        setChallengeRandomString(challengeRandomString);
    }

    public List<Integer> getIntArray() {
        return intArray;
    }

    public void setIntArray(List<Integer> intArray) {
        this.intArray = intArray;
    }

    public ByteArrayOutputStream getBaos() {
        return baos;
    }

    public void setBaos(ByteArrayOutputStream baos) {
        this.baos = baos;
    }

    public String getChallengeRandomString() {
        return challengeRandomString;
    }

    public void setChallengeRandomString(String challengeRandomString) {
        this.challengeRandomString = challengeRandomString;
    }
}
