package com.thesis.inesc.kademliadht.exceptions;

/**
 * An exception used to indicate that a content does not exist on the DHT
 *
 * @author Joshua Kissoon
 * @created 20140322
 */
public class ContentNotFoundException extends Exception
{

    public ContentNotFoundException(String message)
    {
        //super(message);
        System.out.println(message);
    }
}
