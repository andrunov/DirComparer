package comparer.util;

import comparer.model.FileComparer;
import comparer.model.FileInfo;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Class for output strings info
 */
public class HtmlWriter {

    private static final String BASE_PATH = "src/comparer/resources/html/";

    private static String beginHtml;
    private static String singleDirectory;
    private static String twoDirectory;
    private static String endHtml;

    static {
        beginHtml = readTemplate("beginTemplate.html");
        singleDirectory = readTemplate("singleDirectoryTemplate.html");
        twoDirectory = readTemplate("twoDirectoryTemplate.html");
        endHtml = readTemplate("endTemplate.html");
    }

    private static String readTemplate(String pathVal) {
        Path path = Paths.get(BASE_PATH + pathVal);
        List<String> lines = new ArrayList<>();

        try {
            lines.addAll(Files.readAllLines(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuilder sb = new StringBuilder();

        for (String line : lines) {
            sb.append(line);
            sb.append("\n");
        }

        return sb.toString();
    }

    /*link to fileComparer*/
    private FileComparer comparer;

    /*encoding*/
    private String encoding;

    /*constructor*/
    public HtmlWriter(FileComparer comparer, String encoding) {
        this.comparer = comparer;
        this.encoding = encoding;
    }


    /*write logic*/
    public boolean writeHtmlReport(){
        boolean result = false;
        ResourceBundle resourceBundle = this.comparer.getResourceBundle();
        try{
            PrintWriter writer = new PrintWriter(comparer.getReportName(), "UTF-8");
            writer.println(beginHtml);
            if (this.comparer.isSingleDirCompare()) {
                this.printHtmlHeadSingleDirectory(writer);
            } else {
                this.printHtmlHeadTwoDirectory(writer);
            }

            /*1-st level - 100 equality*/
            printTitle(writer,resourceBundle.getString("1stLevelEquality"));
            printFileList(writer,this.comparer.getFullEquality());

            /*2 level - 100% names equality*/
            printTitle(writer,resourceBundle.getString("2ndLevelEquality"));
            printFileList(writer,this.comparer.getNameEquality());

            /*3 level - 100% sizes equality*/
            printTitle(writer,resourceBundle.getString("3thLevelEquality"));
            printFileList(writer,this.comparer.getSizeEquality());

            /*4 level - very high similarity of names*/
            printTitle(writer,resourceBundle.getString("4thLevelEquality"));
            printFileList(writer,this.comparer.getNameSimilarityHighest());

            /*5 level - high similarity of names*/
            printTitle(writer,resourceBundle.getString("5thLevelEquality"));
            printFileList(writer,this.comparer.getNameSimilarityHigh());

            /*6 level - middle similarity of names*/
            if (this.comparer.isShowSimilarityMiddle()) {
                printTitle(writer, resourceBundle.getString("6thLevelEquality"));
                printFileList(writer, this.comparer.getNameSimilarityMiddle());
            }

            /*7 level - low similarity of names*/
            if (this.comparer.isShowSimilarityLow()) {
                printTitle(writer, resourceBundle.getString("7thLevelEquality"));
                printFileList(writer, this.comparer.getNameSimilarityLow());
            }

            /*8 level - no equalities
            * in this point in this.startDirectory is only filesInfo that no has similarities */
            if (!this.comparer.isSingleDirCompare()) {
                printTitle(writer, getNotFoundDescription());
                printNoSimilarList(writer, this.comparer.getNoSimilarity());
            }

            writer.printf(endHtml);

            writer.close();
            result = true;
        } catch (IOException e) {
            Message.errorAlert(resourceBundle,"Error in Writer.write()", e);
        }
        return result;
    }

    /*
    * gets short name of directory or file*/
    private String getDirectoryShortName(String filePath) {
        int lastSlashPosition = filePath.lastIndexOf('\\') + 1;
        return filePath.substring(lastSlashPosition);
    }

    /*head for two directory case*/
    private void printHtmlHeadSingleDirectory(PrintWriter writer) {
        ResourceBundle resourceBundle = this.comparer.getResourceBundle();
        writer.printf(singleDirectory, //format string
                        resourceBundle.getString("Analyzed"),   //...parameters
                        this.comparer.getStartDirectory().size(),
                        resourceBundle.getString("Files"),
                        resourceBundle.getString("InDirectory"),
                        this.getDirectoryShortName(this.comparer.getStartDirectoryName()),
                        this.comparer.getStartDirectoryName());
    }

    /*head for two directory case*/
    private void printHtmlHeadTwoDirectory(PrintWriter writer) {
        ResourceBundle resourceBundle = this.comparer.getResourceBundle();
        writer.printf(twoDirectory,  //format string
                        resourceBundle.getString("Analyzed"),   //...parameters
                        this.comparer.getStartDirectory().size(),
                        resourceBundle.getString("Files"),
                        resourceBundle.getString("InDirectory"),
                        this.getDirectoryShortName(this.comparer.getStartDirectoryName()),
                        this.comparer.getStartDirectoryName(),
                        resourceBundle.getString("And"),
                        this.comparer.getEndDirectory().size(),
                        resourceBundle.getString("Files"),
                        resourceBundle.getString("InDirectory"),
                        this.getDirectoryShortName(this.comparer.getEndDirectoryName()),
                        this.comparer.getEndDirectoryName());
    }

    /*print title*/
    private void printTitle(PrintWriter writer,String title){
        writer.println();
        writer.println();
        writer.println("***********************************************************************************************************");
        writer.printf("%-5s%-100.100s%2s","*",title,"*");
        writer.printf("\r\n%-2s%-100.100s%5s","*","","*");
        writer.println("\r\n* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");
    }

    /*print title*/
    private void printTitle(PrintWriter writer,List<String> titles){
        writer.println();
        writer.println();
        writer.print("***********************************************************************************************************");
        for (String title : titles) {
            writer.printf("\r\n%-5s%-100.100s%2s", "*", title, "*");
        }
        writer.printf("\r\n%-2s%-100.100s%5s","*","","*");
        writer.println("\r\n* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");
    }

    /*print result of files were found*/
    private void printFound(PrintWriter writer, int quantity){
        ResourceBundle resourceBundle = this.comparer.getResourceBundle();
        if (quantity==0){
            writer.printf("%-5s%-100.100s%2s","*",resourceBundle.getString("NotFound"),"*");
        }else {
            writer.printf("%-5s%-8s%-1s%4d%-1s%-86.86s%2s", "*", resourceBundle.getString("Found"), " ",quantity, " ", resourceBundle.getString("Files"), "*");
        }
        writer.println("\r\n***********************************************************************************************************");
    }

    /*print list*/
    private void printFileList(PrintWriter writer, List<? extends Comparable> fileNameList){
        printFound(writer,fileNameList.size());
        for (Comparable fileName : fileNameList)
        {
            writer.println(fileName.toString());
        }
    }

    /*print list of files that not have similariries*/
    private void printNoSimilarList(PrintWriter writer, List<FileInfo> fileNameList){
        printFound(writer,fileNameList.size());
        int counter = 0;
        for (FileInfo fileName : fileNameList)
        {
            if (counter == 0) {
                writer.println(" --------------------------------------------------------------------------------------------------------");
            } else {
                writer.println("|---------------------------------------------------------------------------------------------------------|");
            }
            writer.println(fileName.printWithoutSimilarities());
            counter++;
        }
        writer.println(" ---------------------------------------------------------------------------------------------------------");
    }

    private List<String> getNotFoundDescription() {
        ResourceBundle resourceBundle = this.comparer.getResourceBundle();
        List<String> result = new ArrayList<>();
        result.add(resourceBundle.getString("8thLevelEquality"));

        if (!this.comparer.isShowSimilarityMiddle() || !this.comparer.isShowSimilarityLow()) {
            result.add(resourceBundle.getString("MayExistSimilarFiles") + " " + resourceBundle.getString("SwitchOnForMoreInformation") );
        }

        if (!this.comparer.isShowSimilarityMiddle() && !this.comparer.isShowSimilarityLow()) {
            result.add(resourceBundle.getString("SwitchOnLowSimilarity") +
                                                     " " + resourceBundle.getString("And") +
                                                     " " + resourceBundle.getString("SwitchOnMiddleSimilarity") +
                                                     " " + resourceBundle.getString("InSettingsMenu"));
            //result.add(resourceBundle.getString("InSettingsMenu"));
        } else if (!this.comparer.isShowSimilarityLow()) {
            result.add(resourceBundle.getString("SwitchOnLowSimilarity") + " " + resourceBundle.getString("InSettingsMenu"));
        } else if (!this.comparer.isShowSimilarityMiddle()){
            result.add(resourceBundle.getString("SwitchOnMiddleSimilarity") + " " + resourceBundle.getString("InSettingsMenu"));
        }
        return result;
    }

    public static void main(String[] args) {
        //System.out.println(beginHtml);
        HtmlWriter writer = new HtmlWriter(null, "UTF-8");
        //writer.printHtmlHeadSingleDirectory(null);
        //String string = singleDirectory.replace("$dirSize", "224");
        //string = singleDirectory.replace("$dirURL", "some URL");
        //string = singleDirectory.replace("$dirName", "some Name");

        //System.out.printf(singleDirectory, "225", "some URL", "some name");
        System.out.println(writer.getDirectoryShortName("D:\\MUSIC\\Retro\\COMPILATIONS\\Hronology_USSR"));
    }

}
