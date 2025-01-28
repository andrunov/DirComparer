package comparer.model;

import comparer.util.Formatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Class for hold info about file
 */
public class FileInfo implements Comparable<FileInfo>
{


    /*for increase ID of wordInfo objects*/
    private static int fileInfoCounter;


    /*copy FileInfo excluding List<FileInfo> similarFiles*/
    public static FileInfo copy(FileInfo fileInfo){
        FileInfo newFileInfo = new FileInfo();
        newFileInfo.ID = fileInfo.ID;
        newFileInfo.setAbsolutePath(fileInfo.getAbsolutePath());
        newFileInfo.setSize(fileInfo.getSize());
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
        String result = null;
        if (dotPosition == -1) {
            result = fileName;
        } else {
            result = fileName.substring(0, dotPosition);
        }
        return result;
    }

    private static String extractFileName(String fileName, String extension){
        String result = null;
        if (extension == null) {
            result = fileName;
        } else {
            result = fileName.substring(0, fileName.length() - extension.length() - 1);
        }
        return result;
    }

    public static String extractFileExtension(String fileName){
        int dotPosition = fileName.lastIndexOf('.');
        String result = null;
        String extension = null;
        if (dotPosition != -1) {
            extension = fileName.substring(dotPosition + 1);
            if ((extension.indexOf(' ') == -1)
                    || extension.length() < 20) {

                result = extension;
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
            result = Formatter.splitStringHard(phrase, 2);
            if (result.size() == 0) {
                result = Formatter.splitStringLight(phrase, 2);
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

    public static List<WordInfo> putWordsIntoDictionary(List<String> list) {
        List<WordInfo> result = new ArrayList<>();
        for (String string : list) {
            Map<String, WordInfo> dictionary = FileComparer.getTempDictionary();
            WordInfo wordInfo = null;
            if (dictionary.containsKey(string)) {
                wordInfo = dictionary.get(string);
                wordInfo.setQuantity(wordInfo.getQuantity() + 1);
            } else {
                wordInfo = new WordInfo(string);
                dictionary.put(string, wordInfo);
            }
            result.add(wordInfo);
        }

        return result;
    }

    /**
     * unique identifier
     * */
    private int ID;

    /*absolute path to file*/
    private String absolutePath;

    /*size of file*/
    private long size;


    private boolean isDirectory;

    private List<WordInfo> dWords;

    private String extension;

    /*list of files with similar names*/
    private List<FileInfo> similarFiles = new ArrayList<>();

    /*field-marker that this object has participate in compares*/
    private boolean accepted;

    /*default constructor*/
    public FileInfo() {
    }

    /*constructor*/
    public FileInfo(String absolutePath, String name, long size, boolean isDirectory) {
        this.ID = FileInfo.fileInfoCounter++;
        this.absolutePath = absolutePath;
        this.size = size;
        this.isDirectory = isDirectory;
        name = cutExtension(name);
        this.dWords = putWordsIntoDictionary(getSplitString(name));
        this.accepted = false;
    }

    /*constructor*/
    public FileInfo(String name) {
        this(name, name, 0, false);
        this.ID = FileInfo.fileInfoCounter++;
        this.absolutePath = name;
        this.size = 0;
        this.isDirectory = false;
        this.extension = extractFileExtension(name);
        name = extractFileName(name, this.extension);
        this.dWords = putWordsIntoDictionary(getSplitString(name));
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
        int lastSlash = this.absolutePath.lastIndexOf('\\') ;
        return this.absolutePath.substring(lastSlash + 1);
    }

    public long getSize() {
        return size;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public String getSizeFormatted() {
        return Formatter.doubleFormat("###,###,###,###,###.##",this.getSize());
    }

    public String getExtension() {
        return extension;
    }

    public void setSize(long size) {
        this.size = size;
    }


    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public String getBaseFolderPath() {
        int lastSlash = this.absolutePath.lastIndexOf('\\') ;
        return this.absolutePath.substring(0, lastSlash);
    }

    public List<FileInfo> getSimilarFiles() {
        return similarFiles;
    }

    public void setSimilarFiles(List<FileInfo> similarFiles) {
        this.similarFiles = similarFiles;
    }

    /*to string method*/
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(" ---------------------------------------------------------------------------------------------------------");
        String sizeFormatted = Formatter.doubleFormat("###,###.##",this.size*1.0/1048576);
        sb.append(String.format("\r\n%-2s%-87.87s%10.10s%3s%5s","|",this.showName(),sizeFormatted, "mb","|"));
        sb.append("\r\n ---------------------------------------------------------------------------------------------------------");
        return sb.toString();
    }

    /*to string without similarities*/
    public String printWithoutSimilarities() {
        String sizeFormatted = Formatter.doubleFormat("###,###.##",this.size*1.0/1048576);
        return String.format("%-2s%-87.87s%10.10s%3s%5s","|",this.showName(),sizeFormatted, "mb","|");
    }

    public String getShortDirectoryName() {
        int lastSlashFilePosition = this.absolutePath.lastIndexOf('\\') + 1;
        int lastSlashDirPosition = this.absolutePath.lastIndexOf('\\', lastSlashFilePosition);
        String dirName = this.absolutePath.substring(0, lastSlashDirPosition);
        int lastSlashPosition = dirName.lastIndexOf('\\') + 1;
        return dirName.substring(lastSlashPosition);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileInfo fileInfo = (FileInfo) o;
        return ID == fileInfo.ID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID);
    }

    /*compare to method*/
    @Override
    public int compareTo(FileInfo other)
    {
        return this.ID - other.ID;
    }

    /*show file path according static boolean showAbsolutePath*/
    private String showName(){
        return this.getAbsolutePath().substring(this.getBaseFolderPath().length()+1);
    }

    public List<WordInfo> getdWords() {
        return dWords;
    }


    public boolean nameIsEquals(FileInfo other) {
        if (this.dWords.size() != other.getdWords().size()) return false;
        for (int i = 0; i < this.dWords.size(); i++) {
            int ID = this.dWords.get(i).getID();
            int otherID = other.dWords.get(i).getID();
            if (ID != otherID) return false;
        }
        return true;
    }


}
