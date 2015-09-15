package Exceptions;

public class WrongTestMethodNameException extends Exception{
    private String methodName;

    public WrongTestMethodNameException(String methodName){
        this.methodName=methodName;
    }

    public String toString(){
        return "Wrong test method name "+methodName;
    }

}
