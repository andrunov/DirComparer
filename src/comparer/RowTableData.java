package comparer;

import comparer.model.FileInfo;

public class RowTableData {

    public RowTableData(FileInfo fileInfo, int similarity) {
        this.fileInfo = fileInfo;
        this.similarity = similarity;
    }

    public int getSimilarity() {
        return similarity;
    }

    public String getBaseFolderPath() {
        return this.fileInfo.getBaseFolderPath();
    }

    public String getName() {
        return this.fileInfo.getName();
    }

    public String getSizeFormatted() {
        return this.fileInfo.getSizeFormatted();
    }

    private FileInfo fileInfo;

    int similarity;

    public String getAbsolutePath() {
        return this.fileInfo.getAbsolutePath();
    }
}
