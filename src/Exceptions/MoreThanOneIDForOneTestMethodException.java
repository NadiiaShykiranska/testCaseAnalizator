package Exceptions;

public class MoreThanOneIDForOneTestMethodException extends Exception{
    public String toString(){
        return "More than one ID for one test method";
    }
}
