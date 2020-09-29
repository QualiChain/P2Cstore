package com.thesis.inesc.Exceptions;

/**
 * An exception used to indicate that the DHT Failed
 *
 * @author Marcelo Regra da Silva
 * @created 19/04/2020
 */
public class DHTFailureException extends Throwable {
    /**
     *
     */
    private static final long serialVersionUID = -2732242087072264067L;

    public DHTFailureException() {
        System.out.println("Could not read from DHT.");
    }
}
