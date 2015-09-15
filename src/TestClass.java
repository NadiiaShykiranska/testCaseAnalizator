import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class TestClass {
    private String testClassName;
    private String testClassPackage;
    private Path path;
    private HashMap<String, TestCase> cases = new HashMap<>();

    public TestClass(String testClassName, Path path){
        this.testClassName =testClassName;
        this.path=path;
    }

    public void setTestClassPackage(String testClassPackage) {
        this.testClassPackage = testClassPackage;
    }

    public void setCases(HashMap<String, TestCase> cases) {
        this.cases = cases;
    }

    public HashMap<String, TestCase> getCases() {
        return cases;
    }

    public String getTestClassName() {
        return testClassName;
    }

    public Path getPath() {
        return path;
    }

    public String getTestClassPackage() {
        return testClassPackage;
    }

    public Set<String> getAllTestCasesIDs(){
        return cases.keySet();
    }

    public HashMap<String, String> getUpdatedTestMethodsMap(){
        HashMap<String, String> updatedTestMethodMap = new HashMap<>();

        for(String testCasesID: getAllTestCasesIDs()){
            for (String oldTest: cases.get(testCasesID).getAllOldTestMethodsNames())
                updatedTestMethodMap.put(oldTest,cases.get(testCasesID).getNewTestMethod(oldTest));
        }

        return updatedTestMethodMap;
    }
}
