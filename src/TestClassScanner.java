import Exceptions.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class TestClassScanner {
    private static final String PACKAGE = "package";
    private static final String PUBLIC_CLASS = "public class";
    private static final String TEST_ANNOTATION = "@Test";
    private static final String TEST_CASE_ANNOTATION = "@TestCase";
    private static final String ID = "id";
    private static final String RC = "RC_";

    public static final String RC_US = "US";
    public static final String RC_CA = "CA";
    public static final String RC_ATT = "ATT";
    public static final String RC_UK = "UK";
    public static final String RC_BT = "BT";
    public static final String RC_TELUS = "TELUS";


    String testClassPackage = null;
    String brand = null;
    String testCaseId = null;
    String testMethod = null;
    Set<String> testAccounts = new HashSet<>();

    private Path path;
    private StringBuffer stringBuffer = new StringBuffer();
    private TestClass testClass;
    private HashMap<String, TestCase> cases = new HashMap<>();

    public TestClassScanner(Path path) throws IOException{
        this.path = path;
    }

    public TestClass getTestClass(){
        return testClass;
    }

    public StringBuffer getStringBuffer() {
        return stringBuffer;
    }

    public HashMap<String, TestCase> getCases() {
        return cases;
    }

    private TestCase getTestCase(String testCaseId) {
        TestCase testCase;
        if (cases.containsKey(testCaseId))
            testCase = cases.get(testCaseId);
        else {
            testCase = new TestCase(testCaseId);
        }
        return testCase;
    }

    public TestClass scanFile() throws Exception{
        String testClassName, line;
        TestCase testCase;
        BufferedReader bufferedReader =  Files.newBufferedReader(path);

        while ((line=bufferedReader.readLine())!=null) {

            if (line.contains(TEST_CASE_ANNOTATION)&&line.contains(ID)) {
                if (testCaseId == null){
                    testCaseId = getCaseIDFromLine(line);
                }
                else if (!testCaseId.equals(getCaseIDFromLine(line)))
                    throw new MoreThanOneIDForOneTestMethodException();
                addTestAccountIfPresent(line);
            }
            else if(line.contains(TEST_ANNOTATION)&&!line.contains(TEST_CASE_ANNOTATION)){
                brand = getBrandFromLine(line);
            }
            else if (line.contains("void ")) {
                if(brand!=null && testCaseId!=null){
                    checkBrandInTheGroup();
                    testMethod = getTestMethodFromLine(line);
                    testCase = getTestCase(testCaseId);
                    testCase.setTestMethod(brand, testMethod);
                    testCase.setTestAccounts(brand, testAccounts);
                    cases.put(testCaseId, testCase);
                    setVariablesToNull();

                }
            } else if (line.contains(PACKAGE)) {
                testClassPackage = getTestClassPackageFormLine(line);
            }
            else if (line.contains(PUBLIC_CLASS)) {
                testClassName = getTestClassNameFormLine(line);
                testClass = new TestClass(testClassName, path);
                cases = testClass.getCases();
                testClass.setTestClassPackage(testClassPackage);
            }
            stringBuffer = stringBuffer.append(line + "\n");
        }

        bufferedReader.close();

        return testClass;
    }

    private String getTestClassNameFormLine(String line){
        return line.split("extends")[0].replace(" ", "").replace("public", "").replace("class", "").replace("{", "");
    }

    private String getTestClassPackageFormLine(String line){
        return line.replace(" ", "").replace(PACKAGE,"").replace(";","");
    }

    private String getBrandFromLine(String line) throws GroupsAbsentException, BrandAbsentException, MoreThanOneBrandException {
        String groups[] = getGroupsFromLine(line);
        String brand = null;
        for (String group : groups) {
            if (group.contains(RC)) {
                if(brand==null)
                    brand = group.replace(RC,"");
                else
                    throw new MoreThanOneBrandException();
            }
        }
        if(brand==null){
            throw new BrandAbsentException();
        } else
            return brand;
    }

    private String [] getGroupsFromLine(String line) throws GroupsAbsentException {
        if(!line.contains("groups"))
            throw new GroupsAbsentException();
        return line.replace(" ", "").split("groups=\\{")[1].replace("})", "").split(",");
    }

    private String getCaseIDFromLine(String line) throws TestIDIsAbsentException {
        String id = line.replace(" ", "").replace("@TestCase(id=", "").split("\"")[1];
        if(id.length()==0)
            throw new TestIDIsAbsentException();
        return id;
    }

    private void addTestAccountIfPresent(String line){
        if(line.contains("accountType")){
            String account = line.replace(" ", "").split("accountType=")[1].split("\"")[1];
            testAccounts.add(account);
        }
    }

    private String getTestMethodFromLine(String line) {
        return line.replace("\t", "").replace(" ","").replace("public", "").replace("final", "").replace("void", "").split("\\(")[0];
    }

    private void checkBrandInTheGroup() throws Exception {
        String accountBrand = null;
        if(testAccounts.size()!=0) {
            for (String testAccount : testAccounts){
                accountBrand = getBrandByAccount(testAccount);
                if (accountBrand == null)
                    throw new CannotGetBrandFromAccountException();
                else if (!accountBrand.equals(brand))
                    throw new WrongBrandInGroupsException(testAccount);
            }
        }
    }

    private String getBrandByAccount(String testAccount){
        String accountBrand = null;

                if(testAccount.contains("3610")||testAccount.contains("RCCA")||testAccount.contains("RC_CA")||testAccount.contains("Canada"))
                    accountBrand = RC_CA;
                else if(testAccount.contains("3710")||testAccount.contains("RCUK")||testAccount.contains("RC_UK"))
                    accountBrand = RC_UK;
                else if(testAccount.contains("3420")||testAccount.toUpperCase().contains("ATT"))
                    accountBrand = RC_ATT;
                else if(testAccount.contains("7310")||testAccount.toUpperCase().contains("TELUS"))
                    accountBrand = RC_TELUS;
                else if(testAccount.contains("7710")||testAccount.toUpperCase().contains("BT"))
                    accountBrand = RC_BT;
                else if(testAccount.contains("1210")||testAccount.contains("3311")||testAccount.contains("3621")||testAccount.contains(RC_US)||testAccount.toUpperCase().contains("RC_MOBILE")||testAccount.toUpperCase().contains("RC_OFFICE")||testAccount.toUpperCase().contains("RC_FAX"))
                    accountBrand = RC_US;

        return accountBrand;
    }

    private void setVariablesToNull(){
        testClassPackage = null;
        brand = null;
        testCaseId = null;
        testMethod = null;
        testAccounts = new HashSet<>();;
    }
}
