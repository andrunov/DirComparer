package comparer.model;

import comparer.util.*;

import java.io.File;
import java.util.*;

/**
 * Program for find duplicate files in two different directories
 */
public class FileComparer
{
    /*use for comparing sequences, upper level limit for high similarity
    * means maximum quantity of equals words - 100*/
    private static final int HIGH_SIMILARITY_UPPER_LIMIT = 100;

    /*use for comparing sequences, upper level limit for high similarity
    * means maximum quantity of equals words - 3*/
    private static final int HIGH_SIMILARITY_LOWER_LIMIT = 6;

    /*use for comparing sequences, upper level limit for high similarity
    * means maximum quantity of equals words - 100*/
    private static final int LOW_SIMILARITY_UPPER_LIMIT = 3;

    /*use for comparing sequences, upper level limit for high similarity
    * means maximum quantity of equals words - 100*/
    private static final int LOW_SIMILARITY_LOWER_LIMIT = 3;

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

    /*list for files similar by names with high similarity */
    private List<FileInfo> nameSimilarityHigh = new ArrayList<>();

    /*list for files similar by names with low similarity */
    private List<FileInfo> nameSimilarityLow = new ArrayList<>();

    /*list for files which no has similarities */
    private List<FileInfo> noSimilarities = new ArrayList<>();

    /*filter of file types*/
    private FileFilter filter;

    /*constructor. if extensions undefined filter no use*/
    public FileComparer() {
        String[] extensions = AppPreferences.getFilterExtensions();
        this.filter = new FileFilter(extensions);
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
        this.reportName = startDirectoryName + "\\report.txt";
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

    /*this method contains main logic of comparing*/
    public boolean compare(){
        boolean result = startPreparations();
        if (result) {
            compareDirectories();
            finishPreparations();
            Writer writer = new Writer(this,"UTF8");
            result = writer.write();
        }
        return result;
    }

    /*preparations before compare directories
    * check directories and fill collections*/
    private boolean startPreparations() {
        if ((this.startDirectoryName==null)&&(this.endDirectoryName==null)){
            Message.warningAlert(this.resourceBundle,"SelectDirAlertContentTex");
            return false;
            /*condition for single directory comparing*/
        }else if (this.endDirectoryName==null){
            this.endDirectoryName = this.startDirectoryName;
            /*condition for single directory comparing*/
        }else if (this.startDirectoryName==null){
            this.startDirectoryName = this.endDirectoryName;
        }
        this.startDirectory = fillDirectory(this.startDirectoryName, this.startDirectoryName);
        this.endDirectory = fillDirectory(this.endDirectoryName, this.endDirectoryName);
        return true;
    }

    /*comparing files in directories*/
    private void compareDirectories(){
        // TODO: 08.06.2018  remove minLength
        //max word length
        int wordsQuantity = Integer.MAX_VALUE;
        int maxLengthLow = 3;
//        if (!this.startDirectoryName.equals(this.endDirectoryName)){
//            this.fullEquality = getFullEqualitiesInDifferentDirectories();
//        }else {
//            this.fullEquality = getFullEqualitiesInSingleDirectory();
//        }
//        this.nameEquality = getNamesEqualities();
//        this.sizeEquality = getSizeEqualities();

          getSimilarities();


//        this.nameSimilarityLow = getSimilarities(maxLengthLow, LOW_SIMILARITY_LOWER_LIMIT, LOW_SIMILARITY_UPPER_LIMIT);
//        this.noSimilarities = getNoSimilarities();
    }

    /*fill list with files that no have similarities*/
    private List<FileInfo> getNoSimilarities() {
        ArrayList<FileInfo> result = new ArrayList<>();
        for (FileInfo fileInfo : this.startDirectory){
            if (!fileInfo.isAccepted()){
                result.add(fileInfo);
            }
        }
        return result;
    }

    /*preparations before print result int file*/
    private void finishPreparations(){
        if (this.startDirectoryName.equals(this.endDirectoryName)){
            deleteEqualityDuplications(this.fullEquality);
            deleteEqualityDuplications(this.nameEquality);
            removeEmpties(this.fullEquality);
            removeEmpties(this.nameEquality);
        }
        deleteDuplications(this.sizeEquality);
        deleteDuplications(this.nameSimilarityHigh);
        deleteDuplications(this.nameSimilarityLow);
        removeEmpties(this.sizeEquality);
        removeEmpties(this.nameSimilarityHigh);
        removeEmpties(this.nameSimilarityLow);
        Sorter.sort(this.fullEquality);
        Sorter.sort(this.nameEquality);
        Sorter.sort(this.sizeEquality);
        Sorter.sort(this.nameSimilarityHigh);
        Sorter.sort(this.nameSimilarityLow);
        Sorter.sort(this.noSimilarities);
    }

    /*second comparing
     find files with similar names according quantity
     of similar words given in parameter and save result*/
    private void getSimilarities(){
        for (FileInfo startFileInfo : startDirectory)
        {
            for (FileInfo endFileInfo : endDirectory){
            /*first compare condition*/
                if (startFileInfo.equals(endFileInfo)) continue;
            /*second compare condition*/
                else if (startFileInfo.getName().equals(endFileInfo.getName())) continue;
            /*third compare condition*/
                else if (startFileInfo.getSize()==endFileInfo.getSize()) continue;
                int foundWords = 0;
                for (String startWord : startFileInfo.getWords()){
                    for (String endWord: endFileInfo.getWords()){
                        int difference = startWord.compareTo(endWord);
                        if (difference == 0){
                            foundWords++;
                    /*words in lists were sort, so if endWord > startWord that means there cant be equals words left*/
                        }else if(difference<0){
                            break;
                        }
                    }
                }
                insertSimilarity(startFileInfo, endFileInfo, foundWords);
            }
        }
    }

    private void insertSimilarity (FileInfo startFileInfo, FileInfo endFileInfo, int foundWords){
        if (foundWords == 0) return;
        int startLength = startFileInfo.getWords().size();
        int endLength = endFileInfo.getWords().size();

        if ((startLength == endLength) && (startLength == foundWords)){
            addSimilarity(this.nameSimilarityHigh, startFileInfo, endFileInfo);
        }else if (startLength == foundWords) {
            addSimilarity(this.nameSimilarityLow, startFileInfo, endFileInfo);
        }else if (((startLength - foundWords) == 1) && startLength >= 2) {
            addSimilarity(this.nameSimilarityLow, startFileInfo, endFileInfo);
        }else if (startLength <= 2) {
            addSimilarity(this.nameSimilarityLow, startFileInfo, endFileInfo);
        }
        else {
//            addSimilarity(this.nameSimilarityLow, startFileInfo, endFileInfo);
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
        fileInfo.setAccepted(true);
    }

    /*delete duplications of similar fileInfo in reports*/
    private void deleteDuplications(List<FileInfo> list)
    {
        for (FileInfo fileInfo : list){
            for (FileInfo similar : fileInfo.getSimilarFiles()){
                if (list.contains(similar)) {
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
    private List<FileInfo> removeEmpties(List<FileInfo> list)
    {
        Iterator<FileInfo> iterator = list.iterator();
        while (iterator.hasNext()){
            FileInfo holder = iterator.next();
            if (holder.getSimilarFiles().isEmpty()){
                iterator.remove();
            }
        }
        return list;
    }



    /*first iteration of compare. Find files with 100% matching of names and size
    * in different directories (including subdirectories)**/
    private List<FileInfo> getFullEqualitiesInDifferentDirectories(){
        List<FileInfo> result = new ArrayList<>();
        for (FileInfo fileInfo : startDirectory){
            if (endDirectory.contains(fileInfo)){
                FileInfo copy = FileInfo.copy(fileInfo,fileInfo);
                result.add(copy);
                fileInfo.setAccepted(true);
            }
        }
        return result;
    }

    /*first iteration of compare. Find files with 100% matching of names and size
    in single directory (including subdirectories)*/
    private List<FileInfo> getFullEqualitiesInSingleDirectory(){
        List<FileInfo> result = new ArrayList<>();
        for (FileInfo startFileInfo : startDirectory){
            for (FileInfo endFileInfo : endDirectory){
                if (startFileInfo.equals(endFileInfo)){
                    if (!Objects.equals(startFileInfo.getAbsolutePath(), endFileInfo.getAbsolutePath())){
                        FileInfo copy = FileInfo.copy(startFileInfo,endFileInfo);
                        result.add(copy);
                        startFileInfo.setAccepted(true);
                    }
                }
            }
        }
        return result;
    }


    /*second iteration of compare. Find files with 100% matching of names*/
    private List<FileInfo> getNamesEqualities(){
        List<FileInfo> result = new ArrayList<>();
        for (FileInfo fileInfo : startDirectory){
            for (FileInfo endFileInfo: endDirectory){
                if ((fileInfo.getName().equals(endFileInfo.getName()))
                        &&(fileInfo.getSize() != endFileInfo.getSize())){
                    FileInfo copy = FileInfo.copy(fileInfo,endFileInfo);
                    result.add(copy);
                    fileInfo.setAccepted(true);
                }
            }
        }
        return result;
    }

       /*third iteration of compare. Find files with 100% matching of sizes*/
    private List<FileInfo> getSizeEqualities() {
        List<FileInfo> result = new ArrayList<>();
        for (FileInfo fileInfo : startDirectory){
            for (FileInfo endFileInfo: endDirectory){
                if ((fileInfo.getSize() == endFileInfo.getSize())
                        &&(!fileInfo.getName().equals(endFileInfo.getName()))){
                    FileInfo copy = FileInfo.copy(fileInfo,endFileInfo);
                    result.add(copy);
                    fileInfo.setAccepted(true);
                }
            }
        }
        return result;
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
    public void cleanFields(){
        startDirectoryName = null;
        endDirectoryName = null;
        reportName = null;
        startDirectory.clear();
        endDirectory.clear();
        fullEquality.clear();
        nameEquality.clear();
        nameSimilarityHigh.clear();
        nameSimilarityLow.clear();
    }


}
