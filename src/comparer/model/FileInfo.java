package comparer.model;

import comparer.util.AppPreferences;
import comparer.util.Formatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class for hold info about file
 */
public class FileInfo implements Comparable<FileInfo>
{
    /*words shorted than minLength letters not participate in compare*/
    private static int minLength;

    /*for increase ID of wordInfo objects*/
    private static int fileInfoCounter;

    /*static getter for minLength*/
    static {
        minLength = AppPreferences.getMinStringLength();
    }

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
     * gets position of delimeter singer name from song name
     * in file name*/
    private static int getDashPosition(String fileName) {
        return  getDashPosition(fileName, 0);
    }

    /*
    * gets position of delimeter in file name from "from" position*/
    private static int getDashPosition(String fileName, int from) {

        int result = fileName.indexOf(" - ", from);

        if (result == -1) {

            result = fileName.indexOf(" -", from);
            if (result == -1) {

                result = fileName.indexOf("- ", from);
                if (result == -1) {

                    result = fileName.indexOf("_-_", from);
                    if (result == -1) {

                        result = fileName.indexOf("_", from);
                        if (result == -1) {

                            result = fileName.indexOf('-', from);
                            if (result == -1) {

                                result = fileName.indexOf(8211, from);
                                if (result == -1) {

                                    result = fileName.indexOf(8212, from);
                                }
                            }
                        }
                    }
                }
            }
        }

        //try to split filename more successfully by recursion
        if (result != -1) {
            if (!isName(fileName.substring(0, result)) || !isName(fileName.substring(result))){

                    result = getDashPosition(fileName, result + 1);
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

    /**
     *
     * @param string
     * @return true if string param contains at least 1 letter
     */
    private static boolean isName(String string) {
        Pattern p = Pattern.compile("[а-яА-Яa-zA-Z]");
        Matcher m = p.matcher(string);
        return m.find();
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

    private List<WordInfo> dSongWords;

    private List<WordInfo> dSingerWords;

    /*list of files with similar names*/
    private List<FileInfo> similarFiles = new ArrayList<>();

    /*field-marker that this object has participate in compares*/
    private boolean accepted;

    /*default constructor*/
    public FileInfo() {
    }

    /*constructor*/
    public FileInfo(String absolutePath, String baseFolderPath, String name, long size) {
        this.ID = FileInfo.fileInfoCounter++;
        this.absolutePath = absolutePath;
        this.size = size;
        name = cutExtension(name);
        this.dSongWords = putWordsIntoDictionary(getSplitString(getSongName(name)));
        this.dSingerWords = putWordsIntoDictionary(getSplitString(getSingerName(name)));
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
        int lastSlash = this.absolutePath.lastIndexOf('\\') ;
        return this.absolutePath.substring(0, lastSlash);
    }

    public static int getMinLength() {
        return minLength;
    }

    public static void setMinLength(int minLength) {
        FileInfo.minLength = minLength;
    }

    /*to string method*/
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(" ---------------------------------------------------------------------------------------------------------");
        String sizeFormatted = Formatter.doubleFormat("###,###.##",this.size*1.0/1048576);
        sb.append(String.format("\r\n%-2s%-87.87s%10.10s%3s%5s","|",this.showName(),sizeFormatted, "mb","|"));
        if (!this.similarFiles.isEmpty()) {
            sb.append(String.format("\r\n%-5s%102s", "|", "|"));
            for (FileInfo fileInfo : similarFiles) {
                sizeFormatted = Formatter.doubleFormat("###,###.##",fileInfo.getSize()*1.0/1048576);
                sb.append(String.format("\r\n%-5s%-87.87s%10.10s%3s%2s","|",fileInfo.showName(),sizeFormatted,"mb","|"));
            }
        }
        sb.append("\r\n ---------------------------------------------------------------------------------------------------------");
        return sb.toString();
    }

    /*to string without similarities*/
    public String printWithoutSimilarities() {
        String sizeFormatted = Formatter.doubleFormat("###,###.##",this.size*1.0/1048576);
        return String.format("%-2s%-87.87s%10.10s%3s%5s","|",this.showName(),sizeFormatted, "mb","|");
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

    public List<WordInfo> getdSongWords() {
        return dSongWords;
    }

    public List<WordInfo> getdSingerWords() {
        return dSingerWords;
    }

    public boolean nameIsEquals(FileInfo other) {
        if (this.dSingerWords.size() != other.getdSingerWords().size()) return false;
        if (this.dSongWords.size() != other.getdSongWords().size()) return false;
        for (int i = 0; i < this.dSingerWords.size(); i++) {
            int ID = this.dSingerWords.get(i).getID();
            int otherID = other.dSingerWords.get(i).getID();
            if (ID != otherID) return false;
        }
        for (int i = 0; i < this.dSongWords.size(); i++) {
            int ID = this.dSongWords.get(i).getID();
            int otherID = other.dSongWords.get(i).getID();
            if (ID != otherID) return false;
        }
        return true;
    }


}
