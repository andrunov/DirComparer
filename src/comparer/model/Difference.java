package comparer.model;

import java.util.HashMap;
import java.util.List;

public class Difference {

    /*min coeff of difference to mark word as a similar*/
    private static final int MIN_DIFF = 75;

    /*min weight of word to be considered*/
    private static final int MIN_WEIGHT = 5;

    private List<WordInfo> firstList;

    private List<WordInfo> secondList;

    public Difference(List<WordInfo> firstList, List<WordInfo> secondList) {
        this.firstList = firstList;
        this.secondList = secondList;
    }

    /*
     * find quantity of similar words in two phrases,
     * return 100 NOT means phrases equality (phrases contains equal words,
     * however the order of words may be different)
     * return 0 means that phrases are definitely indifferent
     * return value in range from 1 nj 99 means that phrases are similar in that degree */
    public int getСoincidence(boolean exactWordMatch) {
        List<WordInfo> shortList;
        List<WordInfo> longList;

        if (this.firstList == null) {
            if (this.secondList == null) return 100;
            else return 0;
        } else if (this.secondList == null) return 0;

        if (firstList.size() <= secondList.size()) {
            shortList = firstList;
            longList = secondList;
        } else {
            shortList = secondList;
            longList = firstList;
        }

        if (shortList.size() == 0) {
            if (longList.size() == 0) return 100;
            else return 0;
        }

        int[] coincidences = new int[longList.size()];
        int i = 0;
        outerCycle: for (WordInfo first : longList) {
            for (WordInfo second : shortList) {
                if (first.getID() == second.getID()) {
                    coincidences[i] = 100;
                    i++;
                    continue outerCycle;

                } else {
                    if (!exactWordMatch) {
                        int coincidence = 0;
                        if (first.getSimilarWords() == null) {
                            coincidence = getDiff(first.getWord(), second.getWord());
                            first.setSimilarWords(new HashMap<>());
                            first.getSimilarWords().put(second, coincidence);
                            //TODO отладочный код
                            //System.out.println(first.getWord() +"\t" + second.getWord() + "\t" + coincidence);

                        } else if (!first.getSimilarWords().containsKey(second)) {
                            coincidence = getDiff(first.getWord(), second.getWord());
                            first.getSimilarWords().put(second, coincidence);
                            //TODO отладочный код
                            //System.out.println(first.getWord() +"\t" + second.getWord() + "\t" + coincidence);

                        }
                        else {
                            coincidence = first.getSimilarWords().get(second);
                        }
                        if (coincidence > coincidences[i]) {
                            coincidences[i] = coincidence;
                        }
                    }
                }
            }
            i++;
        }
        return this.getNormalizeResult(coincidences);
    }

    int getDiff(String str1, String str2) {
        int distance = getLevensteinDistance(str1, str2);
        int maxLength = Math.max(str1.length(), str2.length());
        return  100 * (maxLength - distance) / maxLength ;
    }


    private int getLevensteinDistance(String str1, String str2) {
        int[][] matrix = new int[str1.length() + 1][str2.length() + 1];

        // Инициализация первой строки и первого столбца
        for (int i = 0; i <= str1.length(); i++) {
            matrix[i][0] = i;
        }
        for (int j = 0; j <= str2.length(); j++) {
            matrix[0][j] = j;
        }

        // Заполнение матрицы
        for (int i = 1; i <= str1.length(); i++) {
            for (int j = 1; j <= str2.length(); j++) {
                int cost = (str1.charAt(i - 1) == str2.charAt(j - 1)) ? 0 : 1;

                matrix[i][j] = Math.min(
                        matrix[i - 1][j] + 1, // Удаление
                        Math.min(
                                matrix[i][j - 1] + 1, // Вставка
                                matrix[i - 1][j - 1] + cost // Замена
                        )
                );

                // Проверка на перестановку
                if (i > 1 && j > 1 && str1.charAt(i - 1) == str2.charAt(j - 2) && str1.charAt(i - 2) == str2.charAt(j - 1)) {
                    matrix[i][j] = Math.min(matrix[i][j], matrix[i - 2][j - 2] + cost);
                }
            }
        }

        return matrix[str1.length()][str2.length()];
    }

    private int getNormalizeResult(int[] coincidence) {
        int sum = 0;
        int max = 0;
        for (int i : coincidence) {
            sum = sum + i;
            if (i > max) {
                max = i;
            }
        }

        int avg = sum/coincidence.length;
        return (avg + max) / 2;
    }

    private void printArr(int[] array) {
        System.out.print("[");
        for (int element : array) {
            System.out.print(element + ", ");
        }
        System.out.print("]");
    }
}
