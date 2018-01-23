package comparer.util;

import comparer.model.FileInfo;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Class for custom sorters
 */
public class Sorter {

    public static void sort(List<FileInfo> fileInfoList){
        Collections.sort(fileInfoList);
        for (FileInfo fileInfo : fileInfoList){
            Collections.sort(fileInfo.getSimilarFiles(), new Comparator<FileInfo>() {
                @Override
                public int compare(FileInfo o1, FileInfo o2) {
                    int result = o1.getAbsolutePath().compareTo(o2.getAbsolutePath());
                    if (result==0){
                        result = (o1.getSize() < o2.getSize())? -1:(o1.getSize() > o2.getSize())? 1:0;
                    }
                    return result;
                }
            });

        }
    }
}
