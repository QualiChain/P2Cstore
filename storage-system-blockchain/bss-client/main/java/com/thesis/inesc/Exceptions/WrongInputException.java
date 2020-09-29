package com.thesis.inesc.Exceptions;

/**
 * An exception used to indicate that the input given is wrong
 *
 * @author Marcelo Regra da Silva
 * @created 24/04/2020
 */
public class WrongInputException extends Throwable {
    /**
     *
     */
    private static final long serialVersionUID = -6377477417376963367L;

    public WrongInputException() {
        System.out.println("Error: Wrong input. Try again...");
    }
}
