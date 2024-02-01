package comparer.util;

import comparer.model.FileComparer;
import comparer.model.FileInfo;

import java.io.*;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Class for output html report
 */
public class HtmlWriter {

    private static final String BASE_PATH = "html/";

    private static String beginHtml;
    private static String singleDirectory;
    private static String twoDirectory;
    private static String beginTableFound;
    private static String beginTableNotFound;
    private static String tableHeader;
    private static String tableHeaderNotFound;
    private static String tableRowLeft;
    private static String tableRowRight;
    private static String tableRowNotFound;
    private static String endHtml;

    static {
        beginHtml = readTemplate("beginTemplate.html");
        singleDirectory = readTemplate("singleDirectoryTemplate.html");
        twoDirectory = readTemplate("twoDirectoryTemplate.html");
        beginTableFound = readTemplate("beginTableTemplate.html");
        beginTableNotFound = readTemplate("beginTableNotFoundTemplate.html");
        tableHeader = readTemplate("tableHeaderTemplate.html");
        tableHeaderNotFound = readTemplate("tableHeaderNotFoundTemplate.html");
        tableRowLeft = readTemplate("tableRowLeftTemplate.html");
        tableRowRight = readTemplate("tableRowRightTemplate.html");
        tableRowNotFound = readTemplate("tableRowNotFoundTemplate.html");
        endHtml = readTemplate("endTemplate.html");
    }

    private static String readTemplate(String pathVal) {

        String result = null;
        InputStream inputStream = HtmlWriter.class.getClassLoader().getResourceAsStream(BASE_PATH + pathVal);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;

        if (inputStream != null) {

            try {
                while ((length = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, length);
                }
                result = outputStream.toString("UTF-8");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
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

            int filesFound = this.comparer.getFullEquality().size();
            filesFound = filesFound + this.comparer.getNameEquality().size();
            filesFound = filesFound + this.comparer.getNameSimilarityHighest().size();
            filesFound = filesFound + this.comparer.getNameSimilarityHigh().size();
            filesFound = filesFound + this.comparer.getNameSimilarityMiddle().size();
            filesFound = filesFound + this.comparer.getNameSimilarityLow().size();


            this.printHtmlTableBegin(writer, filesFound, this.getShortName(this.comparer.getStartDirectoryName()));
            this.printHtmlTableHeader(writer);

            /*1-st level - 100 equality*/
            this.printHtmlTable(writer, this.comparer.getFullEquality());

            /*2 level - 100% names equality*/
            this.printHtmlTable(writer, this.comparer.getNameEquality());

            /*4 level - very high similarity of names*/
            this.printHtmlTable(writer, this.comparer.getNameSimilarityHighest());

            /*5 level - high similarity of names*/
            this.printHtmlTable(writer, this.comparer.getNameSimilarityHigh());

            /*6 level - middle similarity of names*/
            this.printHtmlTable(writer, this.comparer.getNameSimilarityMiddle());

            /*7 level - low similarity of names*/
            this.printHtmlTable(writer, this.comparer.getNameSimilarityLow());

            this.printHtmlTableEnd(writer);

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
        this.printHtmlTitleSingle(writer);
    }

    /*
    * HTML table for report*/
    private void printHtmlTable(PrintWriter writer, List<FileInfo> fileInfoList) {
        if (fileInfoList.size() > 0) {
            for (FileInfo fileInfo : fileInfoList) {
                this.printHtmlTableRowLeft(writer, fileInfo);
            }
        }
    }


    /*
     * HTML table title for report*/
    private void printHtmlTableBegin(PrintWriter writer, int filesFound, String title) {
        ResourceBundle resourceBundle = this.comparer.getResourceBundle();
        if (filesFound > 0) {
            writer.printf(beginTableFound, //format string
                                title,   //...parameters
                                resourceBundle.getString("Found"),
                    filesFound,
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
                    resourceBundle.getString("FileSize"));
    }

    /*
     * HTML table left part of row for report*/
    private void printHtmlTableRowLeft(PrintWriter writer, FileInfo fileInfo) {
        String sizeFormatted = Formatter.doubleFormat("###,###.##",fileInfo.getSize() * 1.0 / 1048576);
        sizeFormatted = String.format("%s%s", sizeFormatted, "mb");
        String path = fileInfo.getAbsolutePath();
        writer.println("<tr>");
        writer.printf(tableRowLeft, //format string
                this.getDirectoryName(path),
                this.getShortName(this.getDirectoryName(path)),
                path,
                fileInfo.getName(),
                sizeFormatted);

    }

    /*
     * HTML table end title for report*/
    private void printHtmlTableEnd(PrintWriter writer) {
        writer.println("</table>");
        writer.println("</div>");
        writer.println("<br>");
    }

}
