import jxl.Workbook;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

public class Demo {
    private static String localPackage = "D:\\work\\amarosa_trunk_autotest_fixes_SS_and_MW\\";
    private static Path directoryPath = Paths.get(localPackage+"ServiceTests\\src\\test");
    private static Path knownFailuresPath = Paths.get(localPackage+"\\ServiceTests\\known_failures_custom_cases.xml");
    private static HashMap<String, HashMap<String,String>> testClasses = new HashMap<>();
    private static int row = 0;
    private static  WritableSheet sheet;

    public static void main(String [] args) throws Exception{
        WritableWorkbook workbook = Workbook.createWorkbook(new File("c:\\tmp\\output.xls"));

         sheet = workbook.createSheet("First Sheet", 0);

        PathScanner pathJavaScanner = new PathScanner(directoryPath.toFile(), PathScanner.JAVA);
        PathScanner pathXMLScanner = new PathScanner(directoryPath.toFile(), PathScanner.XML);
        List<Path> javaFilePaths = pathJavaScanner.getPathList();
        List<Path> xmlFilePaths = pathXMLScanner.getPathList();

        changeTestMethodsInTestClasses(javaFilePaths);
//        changeTestMethodsInSuits(xmlFilePaths);
//        changeTestMethodsInXML(knownFailuresPath);

        workbook.write();
        workbook.close();
    }

    private static void changeTestMethodsInTestClasses(List<Path> javaFilePaths){
        for (Path path: javaFilePaths){
            try{
                TestMethodRewrite testMethodRewrite = new TestMethodRewrite(path);
               // TestClass updatedTestClass = testMethodRewrite.writeToFile();
               // testClasses.put(getClassName(updatedTestClass), getClassMethods(updatedTestClass));
            }catch (Exception e){
                System.out.println(path+" "+e.toString());
                jxl.write.Label column1 = new jxl.write.Label(0, row, path.toString());
                jxl.write.Label column2 = new jxl.write.Label(1, row, e.toString());
                try {
                    sheet.addCell(column1);
                    sheet.addCell(column2);
                } catch (WriteException e1) {
                    e1.printStackTrace();
                }
                row++;
            }
        }
    }

    private static void changeTestMethodsInSuits(List<Path> xmlFilePaths){
        for (Path path: xmlFilePaths){
            changeTestMethodsInXML(path);
        }
    }

    private static void changeTestMethodsInXML (Path xmlFilePaths){
        try{
            XMLRewrite xmlRewrite = new XMLRewrite(testClasses, xmlFilePaths);
            xmlRewrite.writeToFile();
        }catch (Exception e){
            System.out.println(xmlFilePaths+" "+e.toString());
        }
    }

    private static String getClassName(TestClass updatedTestClass){
        return updatedTestClass.getTestClassPackage()+"."+updatedTestClass.getTestClassName();
    }

    private static HashMap<String,String> getClassMethods (TestClass updatedTestClass){
        return updatedTestClass.getUpdatedTestMethodsMap();
    }
}
