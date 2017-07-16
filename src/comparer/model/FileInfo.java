package comparer.model;

import comparer.util.AppPreferences;
import comparer.util.Formatter;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for hold info about file
 */
public class FileInfo implements Comparable<FileInfo>
{
    /*words shorted than 3 letters not participate in compare*/
    private static int minLength;

    static {
        minLength = AppPreferences.getMinStringLength();
    }

    /*copy FileInfo excluding List<FileInfo> similarFiles*/
    public static FileInfo copy(FileInfo fileInfo){
        FileInfo newFileInfo = new FileInfo();
        newFileInfo.setName(fileInfo.getName());
        newFileInfo.setSize(fileInfo.getSize());
        newFileInfo.setWords(fileInfo.getWords());
        return newFileInfo;
    }

    /*copy FileInfo including List<FileInfo> with similar file*/
    public static FileInfo copy(FileInfo fileInfo, FileInfo singleSimilar){
        List<FileInfo> singleList = new ArrayList<>();
        singleList.add(singleSimilar);
        FileInfo newFileInfo = FileInfo.copy(fileInfo);
        newFileInfo.setSimilarFiles(singleList);
        return newFileInfo;
    }

    /*copy FileInfo including List<FileInfo> */
    public static FileInfo copy(FileInfo fileInfo, List<FileInfo> similarities){
        FileInfo newFileInfo = FileInfo.copy(fileInfo);
        newFileInfo.setSimilarFiles(similarities);
        return newFileInfo;
    }

    /*name of file*/
    private String name;

    /*size of file*/
    private long size;

    /*split in words filename */
    private List<String> words;

    /*list of files with similar names*/
    private List<FileInfo> similarFiles = new ArrayList<>();

    /*field-marker that this object has participate in compares*/
    private boolean accepted;

    public FileInfo() {
    }

    public FileInfo(String name, long size) {
        this.name = name;
        this.size = size;
        this.words = Formatter.splitString(name, minLength);
        this.accepted = false;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public List<FileInfo> getSimilarFiles()
    {
        return similarFiles;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public List<String> getWords() {
        return words;
    }

    public void setWords(List<String> words) {
        this.words = words;
    }

    public void setSimilarFiles(List<FileInfo> similarFiles) {
        this.similarFiles = similarFiles;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public static int getMinLength() {
        return minLength;
    }

    public static void setMinLength(int minLength) {
        FileInfo.minLength = minLength;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(" ---------------------------------------------------------------------------------------------------------");
        String sizeFormatted = Formatter.doubleFormat("###,###.##",this.size*1.0/1024);
        sb.append(String.format("\r\n%-2s%-87.87s%10.10s%3s%5s","|",this.name,sizeFormatted, "mb","|"));
        if (!this.similarFiles.isEmpty()) {
            sb.append(String.format("\r\n%-5s%102s", "|", "|"));
            for (FileInfo fileInfo : similarFiles) {
                sizeFormatted = Formatter.doubleFormat("###,###.##",fileInfo.getSize()*1.0/1024);
                sb.append(String.format("\r\n%-5s%-87.87s%10.10s%3s%2s","|",fileInfo.getName(),sizeFormatted,"mb","|"));
            }
        }
        sb.append("\r\n ---------------------------------------------------------------------------------------------------------");
        return sb.toString();
    }

    @Override
    public int compareTo(FileInfo other)
    {
        int result = this.name.compareTo(other.name);
        if (result==0){
            result = (this.size < other.size)? -1:(this.size > other.size)? 1:0;
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FileInfo)) return false;

        FileInfo fileInfo = (FileInfo) o;

        if (getSize() != fileInfo.getSize()) return false;
        return getName().equals(fileInfo.getName());

    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + (int) (getSize() ^ (getSize() >>> 32));
        return result;
    }

}
