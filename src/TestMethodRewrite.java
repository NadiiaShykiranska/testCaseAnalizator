import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class TestMethodRewrite {
    private Path path;
    private StringBuffer stringBuffer;
    private TestClass testClass;
    private HashMap<String, TestCase> cases = new HashMap<>();


    public TestMethodRewrite(Path path) throws Exception{
        this.path = path;
        TestClassScanner testClassScanner = new TestClassScanner(path);
        testClassScanner.scanFile();
        stringBuffer = testClassScanner.getStringBuffer();
        testClass = testClassScanner.getTestClass();
        cases=testClass.getCases();
    }

    public TestClass writeToFile() throws Exception{
        if(!cases.isEmpty()){
            BufferedWriter bufferedWriter = Files.newBufferedWriter(path);
            renameAllTestMethods();
            bufferedWriter.write(String.valueOf(stringBuffer));
            bufferedWriter.close();
        }
        return testClass;
    }

    private void renameAllTestMethods(){
        String testMethod, newTestMethod;
        for(String caseID: cases.keySet()) {
            for(String brand: cases.get(caseID).getAllBrands()){
                testMethod = cases.get(caseID).getTestMethodByBrand(brand);
                newTestMethod = getNewTestName(caseID, brand);
                cases.get(caseID).setNewTestMethods(testMethod, newTestMethod);
                if(!testMethod.equals(newTestMethod)) {
                    while (stringBuffer.indexOf(" "+testMethod+"(") != -1)
                        stringBuffer = stringBuffer.replace(stringBuffer.indexOf(" "+testMethod+"("), stringBuffer.indexOf(" "+testMethod+"(") + testMethod.length()+1, " "+newTestMethod);
                    while (stringBuffer.indexOf(testMethod+" (") != -1)
                        stringBuffer = stringBuffer.replace(stringBuffer.indexOf(" "+testMethod+" ("), stringBuffer.indexOf(" "+testMethod+" (") + testMethod.length()+2, " "+newTestMethod);
                }
            }
        }
    }

    public String getNewTestName(String caseID, String brand){
        return "test_"+caseID.replace("-","_")+"_"+brand;
    }
}
