package comparer.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Class for custom formatters and other useful
 * methods for transfer primitive types
 */
public class Formatter {

    public static String doubleFormat(String pattern, double value ) {
        DecimalFormat myFormatter = new DecimalFormat(pattern);
        return myFormatter.format(value);
    }

    /*split 1-st par string into words according length of word (2-nd parameter)
   * and sort that list*/
    public static List<String> splitStringHard(String sentence, int wordLength){
        String[] arr = sentence.split("([0-9])|([\\s])|(\\.)|(,)|(\\()|(\\))|(-)|(_)|(\\?)|(!)|(:)|(;)|(&)");
        List<String> list = new ArrayList<>();
        for (String word : arr){
            if (word.length()>=wordLength){
                list.add(word.toLowerCase().replace('ё','е'));
            }
        }
        return list;
    }

    /*split 1-st par string into words according length of word (2-nd parameter)
     * and sort that list*/
    public static List<String> splitStringLight(String sentence, int wordLength){
        String[] arr = sentence.split("([\\s])|(\\()|(\\))|(\\?)|(!)|(:)|(;)|(&)");
        List<String> list = new ArrayList<>();
        for (String word : arr){
            if (word.length()>=wordLength){
                list.add(word.toLowerCase().replace('ё','е'));
            }
        }
        Collections.sort(list);
        return removeDuplications(list);
    }

    /*removes duplications of elements in List<String>*/
    private static List<String> removeDuplications(List<String> list){
        Iterator<String> iterator = list.iterator();
        String last = null;
        while (iterator.hasNext()){
           String current = iterator.next();
           if (current.equals(last)){
               iterator.remove();
           }else {
               last = current;
           }
        }
        return list;
    }

    /*transform String[] to String*/
    public static String getArrayAsString(String[] strings){
        StringBuilder sb = new StringBuilder();
        for (String s : strings){
            sb.append(s);
            sb.append(" ");
        }
        return sb.toString();
    }

    /*return true if string parameter is null or empty or spase*/
    public static boolean stringIsEmpty(String string){
        if (string == null) return true;
        if (string.isEmpty()) return true;
        String stringTrimmed = string.replaceAll("([\\s])","");
        return stringTrimmed.equals("");
    }

    /*split string in rows according roeLength in parameter*/
    public static List<String> splitStringInRows(String string, int rowLength){
        List<String> result = new ArrayList<>();
        int counter = string.length()/rowLength;
        for (int i = 0; i <counter; i++){
            result.add(string.substring(i*rowLength,(i+1)*rowLength));
        }
        result.add(string.substring(counter*rowLength));
        return result;
    }

    /*transform full filePath to filename only*/
    public static String getShortFilePath(String filePath){
        return filePath.substring(0,3) + "..." + filePath.substring(filePath.lastIndexOf('\\'));
    }

    /*adjust control's text size according app's height*/
    public static double getTextSize(Double height){
        double defaultSize = 8.0; //default size
        double sizeByHeight = 0.06 * height;
        return sizeByHeight > defaultSize ? sizeByHeight : defaultSize ;
    }

}
