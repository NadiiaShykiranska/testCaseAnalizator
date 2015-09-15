package Exceptions;

public class BrandAbsentException extends Exception{
    public String toString(){
        return "Brand is absent in groups";
    }
}
