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

            /*1-st level - 100 equality*/
            this.printHtmlTable(writer, this.comparer.getFullEquality(), resourceBundle.getString("1stLevelEquality"));

            /*3 level - 100% sizes equality*/
            this.printHtmlTable(writer, this.comparer.getSizeEquality(), resourceBundle.getString("3thLevelEquality"));

            /*2 level - 100% names equality*/
            this.printHtmlTable(writer, this.comparer.getNameEquality(), resourceBundle.getString("2ndLevelEquality"));

            /*4 level - very high similarity of names*/
            this.printHtmlTable(writer, this.comparer.getNameSimilarityHighest(), resourceBundle.getString("4thLevelEquality"));

            /*5 level - high similarity of names*/
            this.printHtmlTable(writer, this.comparer.getNameSimilarityHigh(), resourceBundle.getString("5thLevelEquality"));

            /*9 level - high similarity of names*/
            this.printHtmlTable(writer, this.comparer.getPartNameSimilarityHigh(), resourceBundle.getString("9thLevelEquality"));

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
            if (!this.comparer.isSingleDirCompare()) {
                this.printHtmlTableNotFound(writer, this.comparer.getNoSimilarity(), resourceBundle.getString("8thLevelEquality"));
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
        if (fileInfoList.size() > 0) {
            this.printHtmlTableHeader(writer);
            for (FileInfo fileInfo : fileInfoList) {
                this.printHtmlTableRowLeft(writer, fileInfo);
            }
        }
        this.printHtmlTableEnd(writer);
    }

    /*
     * HTML table for report*/
    private void printHtmlTableNotFound(PrintWriter writer, List<FileInfo> fileInfoList, String title) {
        this.printHtmlTableBegin(writer, fileInfoList, title);
        if (fileInfoList.size() > 0) {
            this.printHtmlTableHeaderNotFound(writer);
            for (FileInfo fileInfo : fileInfoList) {
                this.printHtmlTableRowNotFound(writer, fileInfo);
            }
        }
        this.printHtmlTableEnd(writer);
    }

    /*
     * HTML table row not found for report*/
    private void printHtmlTableRowNotFound(PrintWriter writer, FileInfo fileInfo) {
        String sizeFormatted = Formatter.doubleFormat("###,###.##",fileInfo.getSize() * 1.0 / 1048576);
        sizeFormatted = String.format("%s%s", sizeFormatted, "mb");
        String path = fileInfo.getAbsolutePath();
        writer.println("<tr>");
        writer.printf(tableRowNotFound, //format string
                this.getDirectoryName(path), //...parameters
                this.getShortName(this.getDirectoryName(path)),
                path,
                fileInfo.getName(),
                sizeFormatted);
        writer.println("</tr>");
    }


    /*
     * HTML table header not found title for report*/
    private void printHtmlTableHeaderNotFound(PrintWriter writer) {
        ResourceBundle resourceBundle = this.comparer.getResourceBundle();
        writer.printf(tableHeaderNotFound, //format string
                resourceBundle.getString("Folder"),   //...parameters
                resourceBundle.getString("FileName"),
                resourceBundle.getString("FileSizeB"));
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
     * HTML table left part of row for report*/
    private void printHtmlTableRowLeft(PrintWriter writer, FileInfo fileInfo) {
        int similars = fileInfo.getSimilarFiles().size();
        String sizeFormatted = Formatter.doubleFormat("###,###.##",fileInfo.getSize() * 1.0 / 1048576);
        sizeFormatted = String.format("%s%s", sizeFormatted, "mb");
        String path = fileInfo.getAbsolutePath();
        writer.println("<tr>");
        writer.printf(tableRowLeft, //format string
                similars,   //...parameters
                this.getDirectoryName(path),
                similars,
                this.getShortName(this.getDirectoryName(path)),
                similars,
                path,
                similars,
                fileInfo.getName(),
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
                path,
                fileInfo.getName(),
                sizeFormatted);
        writer.println("</tr>");
    }

    /*
     * HTML table end title for report*/
    private void printHtmlTableEnd(PrintWriter writer) {
        writer.println("</table>");
        writer.println("</div>");
        writer.println("<br>");
    }

}
