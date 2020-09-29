package com.thesis.inesc.Exceptions;

/**
 * An exception used to indicate that a node could not connect to the Boot Strap Node
 *
 * @author Marcelo Regra da Silva
 * @created 10/03/2020
 */
public class CouldNotConnectToBootstrapNodeException extends Throwable {
    /**
     *
     */
    private static final long serialVersionUID = -1784480461608518891L;

    public CouldNotConnectToBootstrapNodeException() {
        System.out.println("Could not connect to Bootstrap Node. It might me down. Try again later.");
    }
    public CouldNotConnectToBootstrapNodeException(int attemptNumber){
        if(attemptNumber == 5){
            System.out.println("Could not connect to Bootstrap Node. It might me down. Last attempt.");
        }
        else {
            System.out.println("Could not connect to Bootstrap Node. It might me down. Attempt number: " + attemptNumber + ".");
        }
    }
}
