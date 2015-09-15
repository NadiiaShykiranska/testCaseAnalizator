import java.io.File;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public class PathScanner {
    private List<Path> list;
    public static String JAVA = ".java";
    public static String XML = ".xml";

    public PathScanner(File file, String fileType)throws Exception{
        list = new LinkedList<>();
        recursionScanner(file, fileType);
    }
    public List<Path> getPathList(){
        return list;
    }

    private List<Path> recursionScanner(File directoryName, String fileType) throws Exception {
        File [] files = directoryName.listFiles();
        for(File file: files) {
            if (file.isDirectory())
                recursionScanner(file, fileType);
            else if(file.toString().contains(fileType))
                list.add(file.toPath());
        }
        return list;
    }

}
