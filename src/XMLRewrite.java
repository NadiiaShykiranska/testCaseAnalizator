import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Set;

public class XMLRewrite {
    private static String CLASS_OPEN_TAG = "<class ";
    private static String INCLUDE_OPEN_TAG = "<include ";
    private static String EXCLUDE_OPEN_TAG = "<exclude ";
    private static String TEST_NAME = "<test name";
    private HashMap<String, HashMap<String,String>> testClasses;
    private Set<String> testClassNames;
    private Path path;
    private Boolean isChanged=false;
    private StringBuffer stringBuffer = new StringBuffer();


    public XMLRewrite(HashMap<String, HashMap<String,String>> testClasses, Path path){
        this.testClasses = testClasses;
        this.path = path;
        testClassNames = testClasses.keySet();
    }

    public void writeToFile() throws IOException {
        scanFileAndChangeMethodsName();
        if(isChanged){
            BufferedWriter bufferedWriter = Files.newBufferedWriter(path);
            bufferedWriter.write(String.valueOf(stringBuffer));
            bufferedWriter.close();
        }
    }

    private void scanFileAndChangeMethodsName() throws IOException{
        String testClassName = null;
        String testMethodName, line;
        BufferedReader bufferedReader =  Files.newBufferedReader(path);

        while ((line=bufferedReader.readLine())!=null) {

            if (line.contains(CLASS_OPEN_TAG)) {
                testClassName = getNameFromLine(line);
            }
            else if (line.contains(INCLUDE_OPEN_TAG) || line.contains(EXCLUDE_OPEN_TAG) || line.contains(TEST_NAME)) {
                testMethodName = getNameFromLine(line);
                line = changeOldMethodToNew(line, testClassName, testMethodName);
            }
            stringBuffer = stringBuffer.append(line + "\n");
        }

        stringBuffer.deleteCharAt(stringBuffer.length()-1);
        bufferedReader.close();
    }

    private String getNameFromLine(String line){
        return line.split("\"")[1];
    }

    private String changeOldMethodToNew(String line, String testClassName, String testMethodName){
        String updatedLine=null;

        if(testClassNames.contains(testClassName)){
            for (String testMethod: testClasses.get(testClassName).keySet()){
                if(testMethod.equals(testMethodName)){
                    updatedLine = line.replace(testMethodName, testClasses.get(testClassName).get(testMethodName));
                    isChanged=true;
                }
            }
        }

        if(updatedLine==null)
            updatedLine = line;

        return updatedLine;
    }
}
