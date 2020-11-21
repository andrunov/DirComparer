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
    /*words shorted than minLength letters not participate in compare*/
    private static int minLength;

    /*show absolute path in reports or not*/
    private static boolean showAbsolutePath;

    /*static getter for minLength*/
    static {
        minLength = AppPreferences.getMinStringLength();
        showAbsolutePath = AppPreferences.getShowAbsolutePath();
    }

    /*copy FileInfo excluding List<FileInfo> similarFiles*/
    public static FileInfo copy(FileInfo fileInfo){
        FileInfo newFileInfo = new FileInfo();
        newFileInfo.setAbsolutePath(fileInfo.getAbsolutePath());
        newFileInfo.setBaseFolderPath(fileInfo.getBaseFolderPath());
        newFileInfo.setName(fileInfo.getName());
        newFileInfo.setSize(fileInfo.getSize());
        newFileInfo.setSongWords(fileInfo.getSongWords());
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

    /*cuts file extension*/
    private static String cutExtension(String fileName){
        int dotPosition = fileName.lastIndexOf('.');
        return fileName.substring(0,dotPosition);
    }

    /*cuts song name*/
    private static String getSongName(String fileName){
        String result = null;
        int dashPosition = getDashPosition(fileName);
        if (dashPosition == -1) {
            result = fileName;
        } else {
            result = fileName.substring(dashPosition);
        }
        return result;
    }

    /*cuts song name*/
    private static String getSingerName(String fileName){
        String result = null;
        int dashPosition = getDashPosition(fileName);
        if (dashPosition == -1) {
            result = "";
        } else {
            result = fileName.substring(0,dashPosition);
        }
        return result;
    }

    /*
    * gets position of delimeter singer name fron song name
    * in file name*/
    private static int getDashPosition(String fileName) {

        int result = fileName.indexOf(" - ");
        if (result == -1) {

            result = fileName.indexOf(" -");
            if (result == -1) {

                result = fileName.indexOf("- ");
                if (result == -1) {

                    result = fileName.indexOf("_-_");
                    if (result == -1) {

                        result = fileName.indexOf("_");
                        if (result == -1) {

                            result = fileName.indexOf('-');
                            if (result == -1) {

                                result = fileName.indexOf(8211);
                                if (result == -1) {

                                    result = fileName.indexOf(8212);
                                }
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    /*
    * split phrase into list of words*/
    private static List<String> getSplitString(String phrase) {
        List<String> result = null;
        if (phrase.isEmpty()) {
            result = new ArrayList<>();
        } else {
            result = Formatter.splitStringHard(phrase, minLength);
            if (result.size() == 0) {
                result = Formatter.splitStringLight(phrase, minLength);
            }
            if (result.size() == 0) {
                result = Formatter.splitStringLight(phrase, 1);
            }
            if (result.size() == 0) {
                result.add(phrase);
            }
        }
        return result;
    }

    /*absolute path to file*/
    private String absolutePath;

    /*base folder from to show file path*/
    private String baseFolderPath;

    /*name of file*/
    private String name;

    /*size of file*/
    private long size;

    /*split in words song name */
    private List<String> songWords;

    /*split in words singer name */
    private List<String> singerWords;

    /*list of files with similar names*/
    private List<FileInfo> similarFiles = new ArrayList<>();

    /*field-marker that this object has participate in compares*/
    private boolean accepted;

    /*default constructor*/
    public FileInfo() {
    }

    /*constructor*/
    public FileInfo(String absolutePath, String baseFolderPath, String name, long size) {
        this.absolutePath = absolutePath;
        this.baseFolderPath = baseFolderPath;
        this.name = name;
        this.size = size;
        name = cutExtension(name);
        this.songWords = getSplitString(getSongName(name));
        this.singerWords = getSplitString(getSingerName(name));
        this.accepted = false;
    }

    /*getters and setters*/

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
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

    public List<String> getSongWords() {
        return songWords;
    }

    public void setSongWords(List<String> songWords) {
        this.songWords = songWords;
    }

    public List<String> getSingerWords() {
        return singerWords;
    }

    public void setSingerWords(List<String> singerWords) {
        this.singerWords = singerWords;
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

    public String getBaseFolderPath() {
        return baseFolderPath;
    }

    public void setBaseFolderPath(String baseFolderPath) {
        this.baseFolderPath = baseFolderPath;
    }

    public static int getMinLength() {
        return minLength;
    }

    public static void setMinLength(int minLength) {
        FileInfo.minLength = minLength;
    }

    public static boolean isShowAbsolutePath() {
        return showAbsolutePath;
    }

    public static void setShowAbsolutePath(boolean showAbsolutePath) {
        FileInfo.showAbsolutePath = showAbsolutePath;
    }

    /*to string method*/
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(" ---------------------------------------------------------------------------------------------------------");
        String sizeFormatted = Formatter.doubleFormat("###,###.##",this.size*1.0/1048576);
        sb.append(String.format("\r\n%-2s%-87.87s%10.10s%3s%5s","|",this.showPath(),sizeFormatted, "mb","|"));
        if (!this.similarFiles.isEmpty()) {
            sb.append(String.format("\r\n%-5s%102s", "|", "|"));
            for (FileInfo fileInfo : similarFiles) {
                sizeFormatted = Formatter.doubleFormat("###,###.##",fileInfo.getSize()*1.0/1048576);
                sb.append(String.format("\r\n%-5s%-87.87s%10.10s%3s%2s","|",fileInfo.showPath(),sizeFormatted,"mb","|"));
            }
        }
        sb.append("\r\n ---------------------------------------------------------------------------------------------------------");
        return sb.toString();
    }

    /*to string without similarities*/
    public String printWithoutSimilarities() {
        String sizeFormatted = Formatter.doubleFormat("###,###.##",this.size*1.0/1048576);
        return String.format("%-2s%-87.87s%10.10s%3s%5s","|",this.showPath(),sizeFormatted, "mb","|");
    }

    /*compare to method*/
    @Override
    public int compareTo(FileInfo other)
    {
        int result = this.name.compareTo(other.name);
        if (result==0){
            result = Long.compare(this.size, other.size);
        }
        return result;
    }

    /*equals method*/
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FileInfo)) return false;

        FileInfo fileInfo = (FileInfo) o;

        return getSize() == fileInfo.getSize() && getName().equals(fileInfo.getName());

    }

    /*hash code method*/
    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + (int) (getSize() ^ (getSize() >>> 32));
        return result;
    }

    /*show file path according static boolean showAbsolutePath*/
    private String showPath(){
        if (showAbsolutePath) return this.getAbsolutePath();
        else {
            return this.getAbsolutePath().substring(this.getBaseFolderPath().length()+1);
        }
    }


}
