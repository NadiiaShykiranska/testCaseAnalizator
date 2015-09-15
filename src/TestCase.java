import Exceptions.MoreThanOneMethodForOneIDAndBrandException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class TestCase {
    String testCaseID;
    HashMap<String,Set<String>> testAccounts = new HashMap<>(); //<brand, accountsNames>
    HashMap<String,String> testMethods = new HashMap<>(); // <brand, method>
    HashMap<String,String> newTestMethods = new HashMap<>(); // <oldMethod, newMethod>

    public TestCase(String testCaseID){
        this.testCaseID = testCaseID;
    }

    public Set<String> getAllBrands(){
        return testMethods.keySet();
    }

    public Collection<String> getAllTestMethods() {
        return testMethods.values();
    }

    public Collection<String> getAllNewTestMethodsNames() {
        return newTestMethods.values();
    }

    public Collection<String> getAllOldTestMethodsNames() {
        return newTestMethods.keySet();
    }

    public String getTestMethodByBrand(String brand) {
        return testMethods.get(brand);
    }

    public void setTestMethod(String brand, String method) throws MoreThanOneMethodForOneIDAndBrandException {
        if(getAllBrands().contains(brand))
            throw new MoreThanOneMethodForOneIDAndBrandException();
        testMethods.put(brand,method);
    }

    public void setNewTestMethods(String oldMethod, String newMethod){
        newTestMethods.put(oldMethod, newMethod);
        replaceTestMethod(newMethod);
    }

    private void replaceTestMethod(String newMethod){
        String brand = newMethod.replaceAll("test_\\w{3}_\\d{1,6}_","");
        testMethods.replace(brand,newMethod);
    }

    public String getNewTestMethod(String oldMethod){
        return newTestMethods.get(oldMethod);
    }

    public String getTestCaseID() {
        return testCaseID;
    }

    public void setTestAccounts(String brand, Set<String> testAccounts) {
        if(testAccounts.size()!=0)
            this.testAccounts.put(brand, testAccounts);
    }
}
