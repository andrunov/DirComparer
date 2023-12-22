package comparer.util;

import comparer.RowTableData;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Class for custom sorters
 */
public class Sorter {

    /*comparator use only for sort List<FileInfo>, not for compare FileInfo objects */
    private static Comparator<RowTableData> rowTableDataComparator;

    static {
        rowTableDataComparator = new Comparator<RowTableData>() {
            @Override
            public int compare(RowTableData o1, RowTableData o2) {
                int result = o2.getSimilarity() - o1.getSimilarity();
                if (result == 0) {
                    result = o1.getAbsolutePath().compareTo(o2.getAbsolutePath());
                }
                return result;
            }
        };
    }

    public static void sort(List<RowTableData> rowTableDataList) {
        Collections.sort(rowTableDataList, rowTableDataComparator);
    }
}
