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
    private static String beginTableFound;
    private static String beginTableNotFound;
    private static String tableHeader;
    private static String tableRowLeft;
    private static String tableRowRight;
    private static String endHtml;

    static {
        beginHtml = readTemplate("beginTemplate.html");
        singleDirectory = readTemplate("singleDirectoryTemplate.html");
        twoDirectory = readTemplate("twoDirectoryTemplate.html");
        beginTableFound = readTemplate("beginTableFoundTemplate.html");
        beginTableNotFound = readTemplate("beginTableNotFoundTemplate.html");
        tableHeader = readTemplate("tableHeader.html");
        tableRowLeft = readTemplate("tableRowLeft.html");
        tableRowRight = readTemplate("tableRowRight.html");
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
            this.printHtmlTitle(writer);

            /*1-st level - 100 equality*/
            this.printHtmlTable(writer, this.comparer.getFullEquality(), resourceBundle.getString("1stLevelEquality"));

            /*2 level - 100% names equality*/
            this.printHtmlTable(writer, this.comparer.getNameEquality(), resourceBundle.getString("2ndLevelEquality"));

            /*3 level - 100% sizes equality*/
            this.printHtmlTable(writer, this.comparer.getSizeEquality(), resourceBundle.getString("3thLevelEquality"));

            /*4 level - very high similarity of names*/
            this.printHtmlTable(writer, this.comparer.getNameSimilarityHighest(), resourceBundle.getString("4thLevelEquality"));

            /*5 level - high similarity of names*/
            this.printHtmlTable(writer, this.comparer.getNameSimilarityHigh(), resourceBundle.getString("5thLevelEquality"));

            /*6 level - middle similarity of names*/
            if (this.comparer.isShowSimilarityMiddle()) {
                this.printHtmlTable(writer, this.comparer.getNameSimilarityMiddle(), resourceBundle.getString("6thLevelEquality"));
            }

            /*7 level - low similarity of names*/
            if (this.comparer.isShowSimilarityLow()) {
                this.printHtmlTable(writer, this.comparer.getNameSimilarityLow(), resourceBundle.getString("7thLevelEquality"));
            }


            /*8 level - no equalities
            * in this point in this.startDirectory is only filesInfo that no has similarities */
            /*
            if (!this.comparer.isSingleDirCompare()) {
                printTitle(writer, getNotFoundDescription());
                printNoSimilarList(writer, this.comparer.getNoSimilarity());
            }

             */


            writer.printf(endHtml);

            writer.close();
            result = true;
        } catch (IOException e) {
            Message.errorAlert(resourceBundle,"Error in Writer.write()", e);
        }
        return result;
    }

    /*
     * extract name of directory from file path*/
    private String getDirectoryName(String filePath) {
        int lastSlashFilePosition = filePath.lastIndexOf('\\') + 1;
        int lastSlashDirPosition = filePath.lastIndexOf('\\', lastSlashFilePosition);
        return filePath.substring(0, lastSlashDirPosition);
    }

    /*
    * extract short name of file or directory from file path*/
    private String getShortName(String filePath) {
        int lastSlashPosition = filePath.lastIndexOf('\\') + 1;
        return filePath.substring(lastSlashPosition);
    }

    /* HTML title for single directory case*/
    private void printHtmlTitleSingle(PrintWriter writer) {
        ResourceBundle resourceBundle = this.comparer.getResourceBundle();
        writer.printf(singleDirectory, //format string
                        resourceBundle.getString("Analyzed"),   //...parameters
                        this.comparer.getStartDirectory().size(),
                        resourceBundle.getString("Files"),
                        resourceBundle.getString("InDirectory"),
                        this.getShortName(this.comparer.getStartDirectoryName()),
                        this.comparer.getStartDirectoryName());
    }

    /* HTML title for two directory case*/
    private void printHtmlTitleTwo(PrintWriter writer) {
        ResourceBundle resourceBundle = this.comparer.getResourceBundle();
        writer.printf(twoDirectory,  //format string
                        resourceBundle.getString("Analyzed"),   //...parameters
                        this.comparer.getStartDirectory().size(),
                        resourceBundle.getString("Files"),
                        resourceBundle.getString("InDirectory"),
                        this.getShortName(this.comparer.getStartDirectoryName()),
                        this.comparer.getStartDirectoryName(),
                        resourceBundle.getString("And"),
                        this.comparer.getEndDirectory().size(),
                        resourceBundle.getString("Files"),
                        resourceBundle.getString("InDirectory"),
                        this.getShortName(this.comparer.getEndDirectoryName()),
                        this.comparer.getEndDirectoryName());
    }

    /*
    * HTML title for report*/
    private void printHtmlTitle(PrintWriter writer) {

        if (this.comparer.isSingleDirCompare()) {
            this.printHtmlTitleSingle(writer);
        } else {
            this.printHtmlTitleTwo(writer);
        }
    }

    /*
    * HTML table for report*/
    private void printHtmlTable(PrintWriter writer, List<FileInfo> fileInfoList, String title) {
        this.printHtmlTableBegin(writer, fileInfoList, title);
        this.printHtmlTableHeader(writer);
        for (FileInfo fileInfo : fileInfoList) {
            this.printHtmlTableRow(writer, fileInfo);
        }
        this.printHtmlTableEnd(writer);
    }

    /*
     * HTML table title for report*/
    private void printHtmlTableBegin(PrintWriter writer, List<FileInfo> fileInfoList, String title) {
        ResourceBundle resourceBundle = this.comparer.getResourceBundle();
        if (fileInfoList.size() > 0) {
            writer.printf(beginTableFound, //format string
                                title,   //...parameters
                                resourceBundle.getString("Found"),
                                fileInfoList.size(),
                                resourceBundle.getString("Files"));
        } else {
            writer.printf(beginTableNotFound, //format string
                                title,   //...parameters
                                resourceBundle.getString("NotFound"));
        }
    }

    /*
     * HTML table header title for report*/
    private void printHtmlTableHeader(PrintWriter writer) {
        ResourceBundle resourceBundle = this.comparer.getResourceBundle();
            writer.printf(tableHeader, //format string
                    resourceBundle.getString("Folder"),   //...parameters
                    resourceBundle.getString("FileName"),
                    resourceBundle.getString("FileSizeB"),
                    resourceBundle.getString("Folder"),
                    resourceBundle.getString("FileName"),
                    resourceBundle.getString("FileSizeB"));
    }

    /*
     * HTML table row for report*/
    private void printHtmlTableRow(PrintWriter writer, FileInfo fileInfo) {
        writer.println("<tr>");
        this.printHtmlTableRowLeft(writer, fileInfo);
    }

    /*
     * HTML table left part of row for report*/
    private void printHtmlTableRowLeft(PrintWriter writer, FileInfo fileInfo) {
        int similars = fileInfo.getSimilarFiles().size();
        String sizeFormatted = Formatter.doubleFormat("###,###.##",fileInfo.getSize() * 1.0 / 1048576);
        sizeFormatted = String.format("%s%s", sizeFormatted, "mb");
        String path = fileInfo.getAbsolutePath();
        writer.printf(tableRowLeft, //format string
                similars,   //...parameters
                this.getDirectoryName(path),
                this.getShortName(this.getDirectoryName(path)),
                similars,
                fileInfo.getName(),
                path,
                similars,
                sizeFormatted);
        for (FileInfo similar : fileInfo.getSimilarFiles()) {
            this.printHtmlTableRowRight(writer, similar);
        }
    }

    /*
     * HTML table right part of row for report*/
    private void printHtmlTableRowRight(PrintWriter writer, FileInfo fileInfo) {
        String sizeFormatted = Formatter.doubleFormat("###,###.##",fileInfo.getSize() * 1.0 / 1048576);
        sizeFormatted = String.format("%s%s", sizeFormatted, "mb");
        String path = fileInfo.getAbsolutePath();
        writer.printf(tableRowRight, //format string
                this.getDirectoryName(path),
                this.getShortName(this.getDirectoryName(path)),
                fileInfo.getName(),
                path,
                sizeFormatted);
        writer.println("</tr>");
    }

    /*
     * HTML table end title for report*/
    private void printHtmlTableEnd(PrintWriter writer) {

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
        System.out.println(writer.getShortName(writer.getDirectoryName("D:\\MUSIC\\Retro\\COMPILATIONS\\Hronology_USSR\\1992-1999\\22 Поющие Гитары - Вот Это Погода (1996).MP3")));
    }

}
