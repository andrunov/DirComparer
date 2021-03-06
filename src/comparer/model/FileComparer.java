package comparer.model;

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
    private static final int WORD_SIMILARITY_COEFF = 50;

    /*
     * percent of equal letters in two words
     * that allow considering that words are equals*/
    private static final int WORD_EQUALITY_COEFF = 100;

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

    /*list for files similar by part names with high similarity */
    private List<FileInfo> partNameSimilarityHigh = new ArrayList<>();

    /*list for files similar by names with middle similarity */
    private List<FileInfo> nameSimilarityMiddle = new ArrayList<>();

    /*list for files similar by names with low similarity */
    private List<FileInfo> nameSimilarityLow = new ArrayList<>();

    /*list for files which no has similarities */
    private List<FileInfo> noSimilarities = new ArrayList<>();

    /*filter of file types*/
    private FileFilter filter;

    /*indicate that compares files in single directory*/
    private boolean singleDirCompare;

    /*show middle similarity if true*/
    private boolean showSimilarityMiddle;

    /*show low similarity if true*/
    private boolean showSimilarityLow;

    /*constructor. if extensions undefined filter no use*/
    public FileComparer() {
        String[] extensions = AppPreferences.getFilterExtensions();
        this.filter = new FileFilter(extensions);
        this.showSimilarityMiddle = AppPreferences.getShowSimilarityMiddle();
        this.showSimilarityLow = AppPreferences.getShowSimilarityLow();
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

    public List<FileInfo> getPartNameSimilarityHigh() {
        return partNameSimilarityHigh;
    }

    public void setPartNameSimilarityHigh(List<FileInfo> partNameSimilarityHigh) {
        this.partNameSimilarityHigh = partNameSimilarityHigh;
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

    /*this method contains main logic of comparing*/
    public boolean compare(){

    //    long startTime = System.currentTimeMillis();

        boolean result = fillFilenames();
        if (result) {
            compareDirectories();
            outputPreparations();
            HtmlWriter writer = new HtmlWriter(this,"UTF8");
            result = writer.writeHtmlReport();
        }
        clean();

    //    long finishTime = System.currentTimeMillis();
    //    System.out.println("Performance: " + (finishTime - startTime) + " ms");

        return result;
    }

    /*preparations before compare directories
    * check directories and fill collections*/
    private boolean fillFilenames() {
        if ((this.startDirectoryName==null)&&(this.endDirectoryName==null)){
            Message.warningAlert(this.resourceBundle,"SelectDirAlertContentTex");
            return false;
            /*condition for single directory comparing*/
        }else if (this.endDirectoryName==null){
            this.endDirectoryName = this.startDirectoryName;
            this.startDirectory = fillDirectory(this.startDirectoryName, this.startDirectoryName);
            this.endDirectory = this.startDirectory;
            this.singleDirCompare = true;
            /*condition for single directory comparing*/
        }else if (this.startDirectoryName==null){
            this.startDirectoryName = this.endDirectoryName;
            this.endDirectory = fillDirectory(this.endDirectoryName, this.endDirectoryName);
            this.startDirectory = this.endDirectory;
            this.singleDirCompare = true;
        }else {
            this.startDirectory = fillDirectory(this.startDirectoryName, this.startDirectoryName);
            this.endDirectory = fillDirectory(this.endDirectoryName, this.endDirectoryName);
            this.singleDirCompare = false;
        }
        return true;
    }

    /*comparing files in directories
    * comparing for full equality is mandatory
    * in other case rest comparings will not works properly*/
    private void compareDirectories(){
        for (FileInfo startFileInfo : startDirectory) {
            for (FileInfo endFileInfo : endDirectory) {

                if (startFileInfo == endFileInfo) continue;

                if (checkSizeEquality(startFileInfo, endFileInfo)) {
                    if (checkNameEquality(startFileInfo, endFileInfo)) {
                        addEqualities(this.fullEquality, startFileInfo, endFileInfo);
                    } else {
                        addEqualities(this.sizeEquality, startFileInfo, endFileInfo);
                    }
                } else {
                    int songSimilarWords = this.comparePhrases(startFileInfo.getSongWords(), endFileInfo.getSongWords());
                    if (startFileInfo.getSingerWords().size() == 0 || endFileInfo.getSingerWords().size() == 0) {
                        insertSimilarity(startFileInfo, endFileInfo, songSimilarWords);
                    } else {
                        int singerSimilarWords = this.comparePhrases(startFileInfo.getSingerWords(), endFileInfo.getSingerWords());
                        insertSimilarity(startFileInfo, endFileInfo, songSimilarWords, singerSimilarWords);
                    }
                }
            }
            if ((!this.singleDirCompare)&&(!startFileInfo.isAccepted())){
                this.noSimilarities.add(startFileInfo);
            }
        }
    }

    /*preparations before print result int file*/
    private void outputPreparations(){
        deleteDuplications(this.fullEquality);
        deleteDuplications(this.sizeEquality);
        deleteDuplications(this.nameEquality);
        deleteDuplications(this.nameSimilarityHighest);
        deleteDuplications(this.nameSimilarityHigh);
        deleteDuplications(this.partNameSimilarityHigh);
        deleteDuplications(this.nameSimilarityMiddle);
        deleteDuplications(this.nameSimilarityLow);
        removeEmpties(this.fullEquality);
        removeEmpties(this.sizeEquality);
        removeEmpties(this.nameEquality);
        removeEmpties(this.nameSimilarityHighest);
        removeEmpties(this.nameSimilarityHigh);
        removeEmpties(this.partNameSimilarityHigh);
        removeEmpties(this.nameSimilarityMiddle);
        removeEmpties(this.nameSimilarityLow);
        Sorter.sort(this.fullEquality);
        Sorter.sort(this.sizeEquality);
        Sorter.sort(this.nameEquality);
        Sorter.sort(this.nameSimilarityHighest);
        Sorter.sort(this.nameSimilarityHigh);
        Sorter.sort(this.partNameSimilarityHigh);
        Sorter.sort(this.nameSimilarityMiddle);
        Sorter.sort(this.nameSimilarityLow);
        Sorter.sort(this.noSimilarities);
    }

    /*find quantity of equal words in two List<String>*/
    private int findEqualWords(List<String> startWords, List<String> endWords){
        int result = 0;
        for (String startWord : startWords){
            for (String endWord: endWords){
                int difference = startWord.compareTo(endWord);
                if (difference == 0){
                    result++;
                    /*words in lists were sort, so if endWord > startWord that means there cant be equals words left*/
                }else if(difference<0){
                    break;
                }
            }
        }
        return result;
    }

    /*find quantity of similar words in two List<String>, return 100 means equality */

    /*
     * find quantity of similar words in two phrases,
     * return 100 means phrases equality (phrases contains equal words,
     * however the order of words may be different)
     * return 0 means that phrases are definitely different
     * return value in range from 1 nj 99 means that phrases are similar in that degree */
    private int comparePhrases(List<String> phrase1, List<String> phrase2){
        int result = 0;
        List<String> longPhrase1;
        List<String> shortPhrase;
        if (phrase1.size() >= phrase2.size()) {
            longPhrase1 = phrase1;
            shortPhrase = phrase2;
        } else {
            longPhrase1 = phrase2;
            shortPhrase = phrase1;
        }
        int[] foundIndexes = new int[shortPhrase.size()];

        for (String startWord : longPhrase1){
            int counter = 0;
            int maxFound = 0;
            int indexForMaxFound = 0;

            for (String endWord: shortPhrase){
                if (foundIndexes[counter] < 100) {
                    int difference = this.compareWords(startWord, endWord);

                    if ((difference == WORD_EQUALITY_COEFF)) {
                        maxFound = difference;
                        indexForMaxFound = counter;
                        break;

                    } else if ((difference >= WORD_SIMILARITY_COEFF)) {
                        if (difference > maxFound) {
                            maxFound = difference;
                            indexForMaxFound = counter;
                        }
                    }
                }
                counter++;
            }
            if (maxFound > foundIndexes[indexForMaxFound]) {
                foundIndexes[indexForMaxFound] = maxFound;
            }
        }
        int length = Math.max(phrase1.size(), phrase2.size());
        for (int i : foundIndexes) {
            result = result + i;
        }
        result = result / length;
        return result;
    }


    /*
    * find quantity of similar letters in two words,
    * return 100 means words equality
    * return 0 means that words are definitely different
    * return value in range from 1 nj 99 means that words are similar in that degree */
    private int compareWords(String word1, String word2){

        double lengthDiff =  ((double)(word1.length()) / word2.length());
        if (lengthDiff > 1.75 || lengthDiff < 0.55) return 0;
        if (lengthDiff == 1.00) {
            if (word1.equals(word2)) return 100;
        }

        String shortWord;
        String longWord;
        if (word1.length() <= word2.length()) {
            shortWord = word1;
            longWord = word2;
        } else {
            shortWord = word2;
            longWord = word1;
        }

        int result = 0;
        int lastDiffPosition = 0;
        int diffChangeCount = 0;
        int[] foundIndexes = new int[longWord.length()];
        for (int i = 0; i < shortWord.length(); i++){
            for (int j = (i == 0 ? 0 : i - 1); (j <= i + 1) && (j < longWord.length()) ; j++){
                if (foundIndexes[j] == 1) continue;
                if (shortWord.charAt(i) == longWord.charAt(j)) {
                    foundIndexes[j] = 1;
                    if ((i - j) != lastDiffPosition) {
                        diffChangeCount++;
                        if (diffChangeCount >= 2) return 0;
                        lastDiffPosition = i - j;
                    }
                    result = result + 1;
                    break;
                }
            }
        }
        int length = Math.max(shortWord.length(), longWord.length());
        result = (int) (Math.round((result - diffChangeCount) * 100.00 / length));
        return result;
    }


    /*second iteration of compare. Find files with 100% matching of names*/
    private boolean checkNameEquality(FileInfo startFileInfo, FileInfo endFileInfo){
        return startFileInfo.getName().equals(endFileInfo.getName());
    }


    /*third iteration of compare. Find files with 100% matching of sizes*/
    private boolean checkSizeEquality(FileInfo startFileInfo, FileInfo endFileInfo){
        return  startFileInfo.getSize() == endFileInfo.getSize();
    }

    /*insert two similar FileInfo in directory send as 1st parameter*/
    private void addEqualities(List<FileInfo> list, FileInfo startFileInfo, FileInfo endFileInfo){
        FileInfo copy = FileInfo.copy(startFileInfo,endFileInfo);
        list.add(copy);
        startFileInfo.setAccepted(true);
    }

    /*insert two similar FileInfo in suitable directory depending of similarity found words*/
    private void insertSimilarity(FileInfo startFileInfo, FileInfo endFileInfo, int songSimilarityDegree, int singerSimilarityDegree){

        if ((songSimilarityDegree == 100)  && (singerSimilarityDegree ==100)) {
            addSimilarity(this.nameEquality, startFileInfo, endFileInfo);
            startFileInfo.setAccepted(true);

        } else if ((songSimilarityDegree >= 80)  && (singerSimilarityDegree > 50)) {
            addSimilarity(this.nameSimilarityHighest, startFileInfo, endFileInfo);
            startFileInfo.setAccepted(true);

        } else if ((songSimilarityDegree >= 60) && (songSimilarityDegree < 80) && (singerSimilarityDegree > 40)) {
            addSimilarity(this.nameSimilarityHigh, startFileInfo, endFileInfo);
            startFileInfo.setAccepted(true);

        } else if ((songSimilarityDegree >= 80)) {
            addSimilarity(this.partNameSimilarityHigh, startFileInfo, endFileInfo);
            startFileInfo.setAccepted(true);

        } else if (this.showSimilarityMiddle && songSimilarityDegree >= 30) {
            addSimilarity(this.nameSimilarityMiddle, startFileInfo, endFileInfo);
            startFileInfo.setAccepted(true);

        } else if (this.showSimilarityLow && (songSimilarityDegree > 0)) {
            addSimilarity(this.nameSimilarityLow, startFileInfo, endFileInfo);
            startFileInfo.setAccepted(true);
        }
    }

    /*insert two similar FileInfo in suitable directory depending of similarity found words*/
    private void insertSimilarity (FileInfo startFileInfo, FileInfo endFileInfo, int similarityDegree){

        if (similarityDegree == 100) {

            if (startFileInfo.getSingerWords().size() == 0 && endFileInfo.getSingerWords().size() == 0) {
                addSimilarity(this.nameEquality, startFileInfo, endFileInfo);
                startFileInfo.setAccepted(true);

            } else {
                addSimilarity(this.nameSimilarityHighest, startFileInfo, endFileInfo);
                startFileInfo.setAccepted(true);
            }

        } else if (similarityDegree >= 80) {
            addSimilarity(this.nameSimilarityHighest, startFileInfo, endFileInfo);
            startFileInfo.setAccepted(true);

        } else if (similarityDegree >= 60) {
            addSimilarity(this.nameSimilarityHigh, startFileInfo, endFileInfo);
            startFileInfo.setAccepted(true);

        } else if (this.showSimilarityMiddle && similarityDegree >= 30) {
            addSimilarity(this.nameSimilarityMiddle, startFileInfo, endFileInfo);
            startFileInfo.setAccepted(true);

        } else if (this.showSimilarityLow && (similarityDegree > 0)) {
            addSimilarity(this.nameSimilarityLow, startFileInfo, endFileInfo);
            startFileInfo.setAccepted(true);
        }
    }

    /*insert found similar pair into list<fileInfo>*/
    private void addSimilarity (List<FileInfo> list, FileInfo fileInfo, FileInfo similarFileInfo){
        int index = list.indexOf(fileInfo);
        if (index != -1){
            List<FileInfo> similarFiles = list.get(index).getSimilarFiles();
            if (!similarFiles.contains(similarFileInfo)) {
                similarFiles.add(FileInfo.copy(similarFileInfo));
            }
        }else {
            List<FileInfo> result = new ArrayList<>();
            result.add(FileInfo.copy(similarFileInfo));
            list.add(FileInfo.copy(fileInfo, result));
        }
    }

    /*delete duplications of similar fileInfo in reports*/
    private void deleteDuplications(List<FileInfo> list)
    {
        for (FileInfo fileInfo : list){
            for (FileInfo similar : fileInfo.getSimilarFiles()){
                if ((similar != fileInfo) && list.contains(similar)) {
                    int index = list.indexOf(similar);
                    FileInfo duplicate = list.get(index);
                    duplicate.getSimilarFiles().remove(fileInfo);
                }
            }
        }
    }

    /*delete duplications of equal fileInfo in reports*/
    private void deleteEqualityDuplications(List<FileInfo> list) {
        for (FileInfo firstLoopFI : list) {
            String primaryFileInfoPath = firstLoopFI.getAbsolutePath();
            ArrayList<String> similarFIPaths = new ArrayList<String>();
            for (FileInfo similarFI : firstLoopFI.getSimilarFiles()) {
                similarFIPaths.add(similarFI.getAbsolutePath());
                for (FileInfo secondLoopFI : list) {
                    if (secondLoopFI==firstLoopFI) continue;
                    if (similarFIPaths.contains(secondLoopFI.getAbsolutePath())){
                        int index=0;
                        for (FileInfo potentialDuplicationFI : secondLoopFI.getSimilarFiles()){
                            if (potentialDuplicationFI.getAbsolutePath().equals(primaryFileInfoPath)){
                                secondLoopFI.getSimilarFiles().remove(index);
                                break;
                            }
                         index++;
                        }
                    }
                }
            }
        }
    }


    /*delete elements which has empty similarFilenames fields*/
    private void removeEmpties(List<FileInfo> list)
    {
        Iterator<FileInfo> iterator = list.iterator();
        while (iterator.hasNext()){
            FileInfo holder = iterator.next();
            if (holder.getSimilarFiles().isEmpty()){
                iterator.remove();
            }
        }
    }

    /*fill map with filenames and their split names by the words */
    private List<FileInfo> fillDirectory(String directoryPath, String baseDirectoryPath){
        List<FileInfo> result = new ArrayList<>();
        File directory = new File(directoryPath);
        if (directory.isDirectory()){
            String[] filePaths = directory.list();;
            for (String filePath: filePaths){
                String absoluteFilePath = directoryPath + "\\" + filePath;
                if (this.filter.accept(absoluteFilePath)) {

                    File file = new File(absoluteFilePath);
                    if (file.isFile()) {
                        result.add(new FileInfo(absoluteFilePath, baseDirectoryPath,filePath, file.length()));
                    } else if (file.isDirectory()) {
                        result.addAll(fillDirectory(absoluteFilePath, baseDirectoryPath));
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
        this.fullEquality.clear();
        this.nameEquality.clear();
        this.sizeEquality.clear();
        this.nameSimilarityHighest.clear();
        this.nameSimilarityHigh.clear();
        this.partNameSimilarityHigh.clear();
        this.nameSimilarityMiddle.clear();
        this.nameSimilarityLow.clear();
        this.noSimilarities.clear();
    }

}
