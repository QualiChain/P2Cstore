package com.thesis.inesc.Exceptions;

/**
 * An exception used to indicate that it was not possible to read a line of a file
 *
 * @author Marcelo Regra da Silva
 * @created 10/03/2020
 */
public class CouldNotReadLineException extends Exception{
    /**
     *
     */
    private static final long serialVersionUID = -630305221415406777L;

    public CouldNotReadLineException() {
        System.out.println("Could not read line.\nPlease try again later.");
    }
}
