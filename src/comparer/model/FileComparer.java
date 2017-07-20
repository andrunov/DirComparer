package comparer.model;

import comparer.util.AppPreferences;
import comparer.util.Message;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
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
    * means maximum quantity of equals words - 100*/
    private static final int HIGH_SIMILARITY_LOWER_LIMIT = 3;

    /*use for comparing sequences, upper level limit for high similarity
    * means maximum quantity of equals words - 100*/
    private static final int LOW_SIMILARITY_UPPER_LIMIT = 2;

    /*use for comparing sequences, upper level limit for high similarity
    * means maximum quantity of equals words - 100*/
    private static final int LOW_SIMILARITY_LOWER_LIMIT = 1;

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
    private Filter filter;

    /*constructor. if extensions undefined filter no use*/
    public FileComparer() {
        String[] extensions = AppPreferences.getFilterExtensions();
        if (extensions.length!=0) {
            this.filter = new Filter(extensions);
        }
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

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    /*this method contains main logic of comparing
        calls out */
    public boolean execute(){
        boolean result = startPreparations();
        if (result) {
            try {
                result = startPreparations();
                result = compareDirectories();
                if (result) {
                    result = finishPreparations();
                    printResult();
                }
            } catch (Exception e) {
                Message.errorAlert(this.resourceBundle, e.getMessage());
            }
        }
        return result;
    }

    /*preparations before compare directories
    * check directories and fill collections*/
    private boolean startPreparations() {
        if ((this.startDirectoryName==null)&(this.endDirectoryName==null)){
            Message.warningAlert(this.resourceBundle,"SelectDirAlertContentTex");
            return false;
            /*condition for single directory comparing*/
        }else if (this.endDirectoryName==null){
            this.endDirectoryName = this.startDirectoryName;
            /*condition for single directory comparing*/
        }else if (this.startDirectory==null){
            this.startDirectoryName = this.endDirectoryName;
        }
        this.startDirectory = fillDirectory(this.startDirectoryName);
        this.endDirectory = fillDirectory(this.endDirectoryName);
        return true;
    }

    /*comparing files in directories*/
    private boolean compareDirectories(){
        /*if directory is single comparing of equals files and names not to do*/
        if (!this.startDirectoryName.equals(this.endDirectoryName)){
            this.fullEquality = getFullEqualities();
            this.nameEquality = getNamesEqualities();
            this.sizeEquality = getSizeEqualities();
        }
        this.nameSimilarityHigh = getSimilarities(HIGH_SIMILARITY_LOWER_LIMIT, HIGH_SIMILARITY_UPPER_LIMIT);
        this.nameSimilarityLow = getSimilarities(LOW_SIMILARITY_LOWER_LIMIT, LOW_SIMILARITY_UPPER_LIMIT);
        this.noSimilarities = getNoSimilarities();
        return true;
    }

    /*fill list fith files that no have similarities*/
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
    private boolean finishPreparations(){
        deleteDuplications(this.nameSimilarityHigh);
        deleteDuplications(this.nameSimilarityLow);
        removeEmpties(this.nameSimilarityHigh);
        removeEmpties(this.nameSimilarityLow);
        Collections.sort(this.fullEquality);
        Collections.sort(this.nameEquality);
        Collections.sort(this.sizeEquality);
        Collections.sort(this.nameSimilarityHigh);
        Collections.sort(this.nameSimilarityLow);
        Collections.sort(this.noSimilarities);
        return true;
    }

    /*second comparing
     find files with similar names according quantity
     of similar words given in parameter and save result*/
    private List<FileInfo> getSimilarities(int lowerLimit, int upperLimit){
        List<FileInfo> result = new ArrayList<>();
        for (FileInfo fileInfo : startDirectory)
        {
            List<FileInfo> similarities = findSimilarities(fileInfo,lowerLimit,upperLimit);
            Collections.sort(similarities);
            if (!similarities.isEmpty()){
                FileInfo copy = FileInfo.copy(fileInfo, similarities);
                result.add(copy);
                fileInfo.setAccepted(true);
            }
        }
        return result;
    }

    /*find files with similar names according 1-st given parameter (file name)
    * and quantity of similar words (2-st parameter)*/
    private List<FileInfo> findSimilarities(FileInfo startFileInfo,int lowerLimit, int upperLimit){
        List<FileInfo> result = new ArrayList<>();
        List<String> startWords = startFileInfo.getWords();
        for (FileInfo endFileInfo : endDirectory){
            /*first compare condition*/
            if (startFileInfo.equals(endFileInfo)) continue;
            /*second compare condition*/
            else if (startFileInfo.getName().equals(endFileInfo.getName())) continue;
            /*third compare condition*/
            else if (startFileInfo.getSize()==endFileInfo.getSize()) continue;
            int counter = 0;
//            System.out.println();
            for (String startWord : startWords){
                for (String endWord: endFileInfo.getWords()){
                        int difference = startWord.compareTo(endWord);
//                    System.out.printf("первое слово: %s второе слово: %s diff: %d\n",startWord,endWord,difference);
                    if (difference == 0){
                        counter++;
//                        System.out.printf("счетчик: %d\n",counter);
                    /*words in lists were sort, so if endWord > startWord that means there cant be equals words left*/
                    }else if(difference<0){
//                        System.out.println("break");
                        break;
                    }
                }
            }
//            System.out.printf("счетчик: %d ниж гр.: %d верх гр.: %d\n",counter,lowerLimit,upperLimit);
            if ((counter >= lowerLimit)&(counter <= upperLimit)){
                 result.add(FileInfo.copy(endFileInfo));
//                System.out.println("add");

            }
        }
        return result;
    }

    /*delete duplications in reports*/
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

    /*print result of comparing*/
    private void printResult(){
        try{
            PrintWriter writer = new PrintWriter(this.reportName, "UTF-8");
            /*condition for single directory comparing*/
            if (!startDirectoryName.equals(endDirectoryName))
            {
                printHeadTwoDirectory(writer);
                /*1-st level - 100 equality*/
                printTitle(writer,resourceBundle.getString("1stLevelEquality"));
                printFileList(writer,this.fullEquality);

                /*2 level - 100% names equality*/
                printTitle(writer,resourceBundle.getString("2ndLevelEquality"));
                printFileList(writer,this.nameEquality);

                 /*3 level - 100% sizes equality*/
                printTitle(writer,resourceBundle.getString("3thLevelEquality"));
                printFileList(writer,this.sizeEquality);
            }else {
                printHeadSingleDirectory(writer);
                printSchemeSingleDirectory(writer);
            }

            /*3 level - equality of 3 words and more*/
            printTitle(writer,resourceBundle.getString("4thLevelEquality"));
            printFileList(writer,nameSimilarityHigh);

            /*4 level - equality from 1 to 2 words*/
            printTitle(writer,resourceBundle.getString("5thLevelEquality"));
            printFileList(writer,nameSimilarityLow);

            /*5 level - no equalities
            * in this point in this.startDirectory is only filesInfo that no has similarities */
            printTitle(writer,resourceBundle.getString("6thLevelEquality"));
            printFileList(writer,this.noSimilarities);
            writer.close();
        } catch (IOException e) {
            Message.errorAlert(this.resourceBundle,e.getMessage());
        }
    }

    private void printHeadSingleDirectory(PrintWriter writer) {
        String title = this.resourceBundle.getString("Analyzed")
                + " " + this.startDirectory.size()
                + " " + this.resourceBundle.getString("Files")
                + " " + this.resourceBundle.getString("InDirectory")
                + " " + this.startDirectoryName;
        writer.println("***********************************************************************************************************");
        writer.printf("%-5s%-100.100s%2s","*",title,"*");
        writer.printf("\r\n%-2s%-100.100s%5s","*","","*");
        writer.println("\r\n***********************************************************************************************************");
    }

    private void printSchemeSingleDirectory(PrintWriter writer){
        writer.print(" ---------------------------------------------------------------------------------------------------------");
        writer.printf("\r\n%-2s%-103s%2s", "|", this.resourceBundle.getString("Schema"),"|");
        writer.printf("\r\n%-5s%102s", "|", "|");
        writer.printf("\r\n%-2s%-87.87s%9.9s%1s%3s%5s","|",this.resourceBundle.getString("ComparingFileSingle"),this.resourceBundle.getString("FileSize"),",", "mb","|");
        writer.printf("\r\n%-5s%102s", "|", "|");
        writer.printf("\r\n%-5s%-87.87s%9.9s%1s%3s%2s","|",this.resourceBundle.getString("SimilarFileSingle") + " №1",this.resourceBundle.getString("FileSize"),",", "mb","|");
        writer.printf("\r\n%-5s%-87.87s%9.9s%1s%3s%2s","|",this.resourceBundle.getString("SimilarFileSingle") + " №2",this.resourceBundle.getString("FileSize"),",", "mb","|");
        writer.printf("\r\n%-5s%-87.87s%9.9s%1s%3s%2s","|",this.resourceBundle.getString("SimilarFileSingle") + " №3",this.resourceBundle.getString("FileSize"),",", "mb","|");
        writer.print("\r\n ---------------------------------------------------------------------------------------------------------");


    }

    private void printHeadTwoDirectory(PrintWriter writer) {
        String title1 = this.resourceBundle.getString("Analyzed")
                        + " " + this.startDirectory.size()
                        + " " + this.resourceBundle.getString("Files")
                        + " " + this.resourceBundle.getString("InDirectory")
                        + " " + this.startDirectoryName;
        String title2 = this.resourceBundle.getString("Analyzed")
                + " " + this.endDirectory.size()
                + " " + this.resourceBundle.getString("Files")
                + " " + this.resourceBundle.getString("InDirectory")
                + " " + this.endDirectoryName;

        writer.println("***********************************************************************************************************");
        writer.printf("%-5s%-100.100s%2s","*",title1,"*");
        writer.printf("\r\n%-2s%-100.100s%5s","*","","*");
        writer.printf("\r\n%-5s%-100.100s%2s","*",title2,"*");
        writer.printf("\r\n%-2s%-100.100s%5s","*","","*");
        writer.println("\r\n***********************************************************************************************************");
    }

    private void printTitle(PrintWriter writer,String title){
        writer.println();
        writer.println();
        writer.println("***********************************************************************************************************");
        writer.printf("%-5s%-100.100s%2s","*",title,"*");
        writer.printf("\r\n%-2s%-100.100s%5s","*","","*");
        writer.println("\r\n* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");
    }

    private void printFound(PrintWriter writer, int quantity){
        if (quantity==0){
            writer.printf("%-5s%-100.100s%2s","*",this.resourceBundle.getString("NotFound"),"*");
        }else {
            writer.printf("%-5s%-8s%-1s%4d%-1s%-86.86s%2s", "*", this.resourceBundle.getString("Found"), " ",quantity, " ", this.resourceBundle.getString("Files"), "*");
        }
        writer.println("\r\n***********************************************************************************************************");
    }

    private void printFileList(PrintWriter writer, List<? extends Comparable> fileNameList){
         printFound(writer,fileNameList.size());
         for (Comparable fileName : fileNameList)
         {
             writer.println(fileName.toString());
         }
    }

    /*first iteration of compare. Find files with 100% matching of names and size*/
    private List<FileInfo> getFullEqualities(){
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
    private List<FileInfo> fillDirectory(String path){
        List<FileInfo> result = new ArrayList<>();
        File directory = new File(path);
        if (directory.isDirectory()){
            String[] filePaths;
            if (this.filter == null){
                filePaths = directory.list();
            }else {
                filePaths = directory.list(this.filter);
            }
            for (String filePath: filePaths){
                File file = new File(path + "/" + filePath);
                result.add(new FileInfo(filePath,file.length()));
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
