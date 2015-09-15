package Exceptions;

public class MoreThanOneBrandException extends Exception{
    public String toString(){
        return "More than one brand is present in the groups";
    }
}
