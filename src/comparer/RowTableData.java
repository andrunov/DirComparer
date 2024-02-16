package comparer;

import comparer.model.FileInfo;

public class RowTableData {

    public RowTableData(FileInfo fileInfo, int similarity) {
        this.fileInfo = fileInfo;
        this.similarity = similarity;
    }


    private FileInfo fileInfo;

    int similarity;

    public FileInfo getFileInfo() {
        return fileInfo;
    }

    public int getSimilarity() {
        return similarity;
    }

    public String getPercSimilarity() {
        return String.format("  %s%s" ,similarity,"%");
    }

    public String getBaseFolderPath() {
        return this.fileInfo.getBaseFolderPath();
    }

    public String getBaseFolderPathFormatted() {
        return String.format("\uD83D\uDCC1 %s",this.fileInfo.getBaseFolderPath());
    }

    public String getName() {
        if (this.isDirectory()) return String.format("\uD83D\uDCC2 %s", this.fileInfo.getName());
        else return String.format("\uD83D\uDCC4 %s", this.fileInfo.getName());
    }

    public String getSizeFormatted() {
        if (this.isDirectory()) return "";
        else return String.format("%s  ",this.fileInfo.getSizeFormatted());
    }


    public String getAbsolutePath() {
        return this.fileInfo.getAbsolutePath();
    }

    public boolean isDirectory() {
        return this.fileInfo.isDirectory();
    }


}
