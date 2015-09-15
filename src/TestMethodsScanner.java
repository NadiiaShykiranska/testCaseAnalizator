import Exceptions.WrongTestMethodNameException;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

public class TestMethodsScanner {
    private static final String TEST_ANNOTATION = "@Test";
    private Path path;
    private Pattern pattern = Pattern.compile("test_\\w{3}_\\d{1,6}_((US)|(CA)|(UK)|(TELUS)|(BT)|(ATT))");

    public TestMethodsScanner(Path path) throws Exception{
        this.path = path;
    }

    public void scanFile() throws Exception{
        String testClass, line, testMethod;
        BufferedReader bufferedReader =  Files.newBufferedReader(path);

        line=bufferedReader.readLine();
        while (!(line.contains("public class")))
            line=bufferedReader.readLine();

        testClass = getTestClassName(line);

        while ((line=bufferedReader.readLine())!=null){

            if (line.contains(TEST_ANNOTATION)) {

                while (!line.contains("public void")&&!line.contains("public final void")){
                    line=bufferedReader.readLine();
                }
                testMethod = getTestMethodFromLine(line);
                try {
                    checkTestMethodName(testMethod);
                }catch (WrongTestMethodNameException exception){
                    System.out.println(testClass+ " " + exception);
                }
            }
        }
        bufferedReader.close();
    }

    private String getTestClassName(String line){
        return line.split("extends")[0].replace(" ", "").replace("public", "").replace("class", "").replace("{", "");
    }

    private String getTestMethodFromLine(String line) {
        return line.replace("\t", "").replace(" ","").replace("public", "").replace("final", "").replace("void", "").split("\\(")[0];
    }

    private void checkTestMethodName(String testMethod) throws WrongTestMethodNameException {
        if(!pattern.matcher(testMethod).matches())
            throw new WrongTestMethodNameException(testMethod);
    }
}
