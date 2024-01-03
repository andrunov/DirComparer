package comparer.model;

import comparer.RowTableData;
import comparer.util.*;

import java.io.File;
import java.util.*;

/**
 * Program for find duplicate files in two different directories
 */
public class FileComparer
{
    /*
    * minimal percent of equal letters in two words
    * that allow considering that words are similar*/
    private static final int WORD_SIMILARITY_COEFF = 75;

    private static final String IGNORE_STRING = "a,'a,an,and,as,at,be,by,de,el,for,if,in,is,it,la,of,oh,on,or,so,the,to,un,up,а,ай,ау,ах,бы,в,во,да,до,жe,за,из,как,ли,на,не,ни,но,ну,об,ой,от,ох,по,со,так,то,уж,эх";

    private static Map<String, WordInfo> tempDictionary;

    private final List<WordInfo> dictionary;

    /*first directory path*/
    private String startDirectoryName;

    /*second directory path*/
    private String endDirectoryName;

    /*report path*/
    private String reportName;

    /*Localization*/
    private ResourceBundle resourceBundle;

    /*first directory with files which we want to check for duplicate */
    private List<FileInfo> startDirectory = new ArrayList<>();

    /*another directory where need to find duplicates files */
    private List<FileInfo> endDirectory = new ArrayList<>();

    public FileInfo getFileToSearch() {
        return fileToSearch;
    }

    public void setFileToSearch(FileInfo fileToSearch) {
        this.fileToSearch = fileToSearch;
    }

    /*  file that needs to de search
    */
    private FileInfo fileToSearch;

    /*list for files matching by names and size, expect full equality*/
    private List<FileInfo> fullEquality = new ArrayList<>();

    /*list for files matching by names only*/
    private List<FileInfo> nameEquality = new ArrayList<>();

    /*list for files matching by sizes*/
    private List<FileInfo> sizeEquality = new ArrayList<>();

    /*list for files similar by names with highest similarity */
    private List<FileInfo> nameSimilarityHighest = new ArrayList<>();

    /*list for files similar by names with high similarity */
    private List<FileInfo> nameSimilarityHigh = new ArrayList<>();

    /*list for files similar by names with middle similarity */
    private List<FileInfo> nameSimilarityMiddle = new ArrayList<>();

    /*list for files similar by names with low similarity */
    private List<FileInfo> nameSimilarityLow = new ArrayList<>();

    /*list for files which no has similarities */
    private List<FileInfo> noSimilarities = new ArrayList<>();


    private List<RowTableData> report = new ArrayList<>();

    /*filter of file types*/
    private FileFilter filter;

    /*indicate that compares files in single directory*/
    private boolean singleDirCompare;

    /*show middle similarity if true*/
    private boolean showSimilarityMiddle;

    /*show low similarity if true*/
    private boolean showSimilarityLow;

    public boolean isAnalyzeByLetters() {
        return analyzeByLetters;
    }

    /*constructor. if extensions undefined filter no use*/
    public FileComparer() {
        String[] extensions = AppPreferences.getFilterExtensions();
        this.filter = new FileFilter(extensions);
        this.showSimilarityMiddle = AppPreferences.getShowSimilarityMiddle();
        this.showSimilarityLow = AppPreferences.getShowSimilarityLow();
        this.analyzeByLetters = AppPreferences.getAnalyseByLetters();
        FileComparer.tempDictionary = new HashMap<>();
        this.dictionary = new ArrayList<>();
    }

    /*getters and setters*/

    public String getStartDirectoryName() {
        return startDirectoryName;
    }

    public String getEndDirectoryName() {
        return endDirectoryName;
    }

    public void setStartDirectoryName(String startDirectoryName) {
        this.startDirectoryName = startDirectoryName;
        setReportName(startDirectoryName);
    }

    public void setEndDirectoryName(String endDirectoryName) {
        this.endDirectoryName = endDirectoryName;
    }

    public void setResourceBundle(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public void setReportName(String startDirectoryName) {
        this.reportName = startDirectoryName + "\\report.html";
    }

    public String getReportName() {
        return reportName;
    }

    public FileFilter getFilter() {
        return filter;
    }

    public void setFilter(FileFilter filter) {
        this.filter = filter;
    }

    public List<RowTableData> getReport() {
        return report;
    }

    public List<FileInfo> getFullEquality() {
        return fullEquality;
    }

    public List<FileInfo> getNameEquality() {
        return nameEquality;
    }

    public List<FileInfo> getSizeEquality() {
        return sizeEquality;
    }

    public List<FileInfo> getNameSimilarityHigh() {
        return nameSimilarityHigh;
    }

    public List<FileInfo> getNameSimilarityLow() {
        return nameSimilarityLow;
    }

    public List<FileInfo> getNoSimilarity() {
        return noSimilarities;
    }

    public List<FileInfo> getStartDirectory() {
        return startDirectory;
    }

    public List<FileInfo> getEndDirectory() {
        return endDirectory;
    }

    public boolean isSingleDirCompare() {
        return singleDirCompare;
    }

    public List<FileInfo> getNameSimilarityMiddle() {
        return nameSimilarityMiddle;
    }

    public List<FileInfo> getNameSimilarityHighest() {
        return nameSimilarityHighest;
    }


    public boolean isShowSimilarityMiddle() {
        return showSimilarityMiddle;
    }

    public void setShowSimilarityMiddle(boolean showSimilarityMiddle) {
        this.showSimilarityMiddle = showSimilarityMiddle;
    }

    public boolean isShowSimilarityLow() {
        return showSimilarityLow;
    }

    public void setShowSimilarityLow(boolean showSimilarityLow) {
        this.showSimilarityLow = showSimilarityLow;
    }

    public static Map<String, WordInfo> getTempDictionary() {
        return tempDictionary;
    }

    public void setAnalyzeByLetters(boolean analyzeByLetters) {
        this.analyzeByLetters = analyzeByLetters;
    }

    /*show analyze by letters*/
    private boolean analyzeByLetters;

    /*this method contains main logic of comparing*/
    public boolean search(){

        /* memory and performance test
        System.gc();
        long startTime = System.currentTimeMillis();
        Runtime runtime = Runtime.getRuntime();
        long memoryBefore = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
         */

        boolean result = fillFilenamesForSearch();
        if (result) {
            compareDirectories();
            Sorter.sort(this.report);
            HtmlWriter writer = new HtmlWriter(this,"UTF8");
            result = writer.writeHtmlReport();
        }
        //clean();

        /*
        long finishTime = System.currentTimeMillis();
        long memoryAfter = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
        System.out.println("Memory use: " + (memoryAfter - memoryBefore) + " mb");
        System.out.println("Performance: " + (finishTime - startTime) + " ms");
        System.gc();
         */

        return result;
    }

    private boolean fillFilenamesForSearch() {
        if ((this.startDirectoryName==null)&&(this.fileToSearch==null)){
            Message.warningAlert(this.resourceBundle,"SelectDirAndWordAlert");
            return false;
        } else  if (this.startDirectoryName==null){
            Message.warningAlert(this.resourceBundle,"SelectDirAlert");
            return false;
        } else if (this.fileToSearch==null){
            Message.warningAlert(this.resourceBundle,"SelectWordAlert");
            return false;
        } else {
            this.startDirectory = fillDirectory(this.startDirectoryName, this.startDirectoryName);
            this.endDirectory.add(this.fileToSearch);
            this.singleDirCompare = false;
        }
        updateDictionaries();
        return true;
    }

    /*comparing files in directories
    * comparing for full equality is mandatory
    * in other case rest comparings will not works properly*/
    private void compareDirectories(){
        for (FileInfo startFileInfo : startDirectory) {
            for (FileInfo endFileInfo : endDirectory) {

                if (startFileInfo == endFileInfo) continue;

                if (startFileInfo.nameIsEquals(endFileInfo)) {
                    RowTableData rowTableData = new RowTableData(startFileInfo, 100);
                    this.report.add(rowTableData);
                } else {
                    int songSimilarWords = this.comparePhrases(startFileInfo.getdWords(), endFileInfo.getdWords());
                    if (songSimilarWords > 0) {
                        RowTableData rowTableData = new RowTableData(startFileInfo, songSimilarWords);
                        this.report.add(rowTableData);
                    }
                }
            }
        }
    }

    /*find quantity of similar words in two List<String>, return 100 means equality */

    /*
     * find quantity of similar words in two phrases,
     * return 100 NOT means phrases equality (phrases contains equal words,
     * however the order of words may be different)
     * return 0 means that phrases are definitely indifferent
     * return value in range from 1 nj 99 means that phrases are similar in that degree */
    private int comparePhrases(List<WordInfo> phrase1, List<WordInfo> phrase2){
        Difference difference = new Difference(phrase1, phrase2);
        return difference.getСoincidence(this.analyzeByLetters);
    }

    private void updateDictionaries() {

        int counter = 0;
        int sumQuantity = 0;

        // add ignore words
        for (String ignoreWord : IGNORE_STRING.split(",")) {
            if (tempDictionary.containsKey(ignoreWord)) {
                tempDictionary.get(ignoreWord).setIgnorance(true);
            }
        }

        for (Map.Entry<String, WordInfo> entry : tempDictionary.entrySet()) {
            WordInfo wordInfo = entry.getValue();
            wordInfo.setID(counter);
            dictionary.add(wordInfo);
            sumQuantity = sumQuantity + entry.getValue().getQuantity();
            counter++;
        }

        tempDictionary.clear();

        //TODO remove later if no need
    /*
        double averageQuantity = (double) sumQuantity/counter;

        for (WordInfo wordInfo : dictionary) {
            wordInfo.setWeight(averageQuantity/wordInfo.getQuantity());
            //System.out.println(wordInfo.getWord() +"\t" + wordInfo.getQuantity() + "\t" + wordInfo.getWeight());

            for (WordInfo otherWordInfo : dictionary) {

                if (wordInfo.getID() != otherWordInfo.getID()) {

                    int difference = compareWords(wordInfo.getWord(), otherWordInfo.getWord());
                    if ((difference >= WORD_SIMILARITY_COEFF)) {
                        if (wordInfo.getSimilarWords() == null) {
                            wordInfo.setSimilarWords(new HashMap<>());
                        }
                        wordInfo.getSimilarWords().put(otherWordInfo, difference);
                        //System.out.println(wordInfo.getWord() +"\t" + otherWordInfo.getWord() + "\t" + difference);
                    }
                }
            }
        }

     */
        dictionary.clear();
    }


    /*insert two similar FileInfo in directory send as 1st parameter*/
    private void addEqualities(List<FileInfo> list, FileInfo startFileInfo, FileInfo endFileInfo){
        FileInfo copy = FileInfo.copy(startFileInfo,endFileInfo);
        list.add(copy);
        startFileInfo.setAccepted(true);
    }


    /*fill map with filenames and their split names by the words */
    private List<FileInfo> fillDirectory(String directoryPath, String baseDirectoryPath){
        List<FileInfo> result = new ArrayList<>();
        File directory = new File(directoryPath);
        if (directory.isDirectory()){
            String[] filePaths = directory.list();
            if (filePaths != null) {
                for (String filePath : filePaths) {
                    String absoluteFilePath = directoryPath + "\\" + filePath;
                    if (this.filter.accept(absoluteFilePath)) {

                        File file = new File(absoluteFilePath);
                        if (file.isFile()) {
                            result.add(new FileInfo(absoluteFilePath, baseDirectoryPath, filePath, file.length()));
                        } else if (file.isDirectory()) {
                            result.addAll(fillDirectory(absoluteFilePath, baseDirectoryPath));
                        }

                    }
                }
            }
        }
        return result;
    }

    /*clear fields and collections*/
    public void clean() {
        this.startDirectoryName = null;
        this.endDirectoryName = null;
        this.startDirectory.clear();
        this.endDirectory.clear();
        this.report.clear();
    }

}
