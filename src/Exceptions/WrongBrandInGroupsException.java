package Exceptions;

public class WrongBrandInGroupsException extends Exception{
    String testAccount;
    public WrongBrandInGroupsException(String testAccount){
        this.testAccount = testAccount;
    }
    public String toString(){
        return "Wrong brand in the groups. Account "+testAccount;
    }
}
