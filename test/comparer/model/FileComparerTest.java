package comparer.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FileComparerTest {

    @Test
    public void case_01() {
        FileComparer fileComparer = new FileComparer();
        fileComparer.setShowSimilarityMiddle(true);
        fileComparer.setShowSimilarityLow(true);
        String dir = "D://Some dir";
        FileInfo fileInfo1 = new FileInfo(dir,"03. Пламя - Карточный Домик.mp3", 10000, false);
        FileInfo fileInfo2 = new FileInfo(dir," 58 Пламя -Строим домик (1987).mp3", 10001, false);
        fileComparer.setStartDirectoryName(dir);
        fileComparer.setEndDirectoryName(dir);
        fileComparer.startDirectory.add(fileInfo1);
        fileComparer.endDirectory.add(fileInfo2);
        fileComparer.updateDictionaries();
        fileComparer.compareDirectories();
        assertEquals(0, fileComparer.getFullEquality().size());
        assertEquals(0, fileComparer.getSizeEquality().size());
        assertEquals(0, fileComparer.getNameEquality().size());
        assertEquals(0, fileComparer.getNameSimilarityHighest().size());
        assertEquals(1, fileComparer.getNameSimilarityHigh().size());
        assertEquals(0, fileComparer.getNameSimilarityMiddle().size());
        assertEquals(0, fileComparer.getNameSimilarityLow().size());
    }

    @Test
    public void case_02() {
        FileComparer fileComparer = new FileComparer();
        fileComparer.setShowSimilarityMiddle(true);
        fileComparer.setShowSimilarityLow(true);
        String dir = "D://Some dir";
        FileInfo fileInfo1 = new FileInfo(dir,"06. Опус - Ах, Молодежь.mp3", 10000, false);
        FileInfo fileInfo2 = new FileInfo(dir,"52 Опус - На кого ты похож (1987).mp3", 10001, false);
        fileComparer.setStartDirectoryName(dir);
        fileComparer.setEndDirectoryName(dir);
        fileComparer.startDirectory.add(fileInfo1);
        fileComparer.endDirectory.add(fileInfo2);
        fileComparer.updateDictionaries();
        fileComparer.compareDirectories();
        assertEquals(0, fileComparer.getFullEquality().size());
        assertEquals(0, fileComparer.getSizeEquality().size());
        assertEquals(0, fileComparer.getNameEquality().size());
        assertEquals(0, fileComparer.getNameSimilarityHighest().size());
        assertEquals(0, fileComparer.getNameSimilarityHigh().size());
        assertEquals(0, fileComparer.getNameSimilarityMiddle().size());
        assertEquals(0, fileComparer.getNameSimilarityLow().size());
    }
}