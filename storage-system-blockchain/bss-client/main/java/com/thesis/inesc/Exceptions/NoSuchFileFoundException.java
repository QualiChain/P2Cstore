package com.thesis.inesc.Exceptions;

/**
 * An exception used to indicate that the given file was not found locally
 * @author Marcelo Regra da Silva
 * @created 19/03/2020
 */
public class NoSuchFileFoundException extends Throwable {
    /**
     *
     */
    private static final long serialVersionUID = -429662212215143771L;

    public NoSuchFileFoundException() {
        System.out.println("Error: No such file found.");
    }
}
