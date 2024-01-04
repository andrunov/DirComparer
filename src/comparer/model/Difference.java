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
    public int getСoincidence(boolean analyzeByLetters) {
        double result = 0;
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

        int coincidence = 0;

        for (WordInfo first : shortList) {
            if (first.isIgnorance()) continue;
            for (WordInfo second : longList) {
                if (second.isIgnorance()) continue;
                if (first.getID() == second.getID()) {
                    coincidence = 100;

                } else if (analyzeByLetters
                  && first.getSimilarWords() != null
                  && first.getSimilarWords().containsKey(second)) {
                            coincidence = first.getSimilarWords().get(second);
                } else if (analyzeByLetters) {
                    coincidence = compareWords(first.getWord(), second.getWord());
                    first.setSimilarWords(new HashMap<>());
                    first.getSimilarWords().put(second, coincidence);
                }
                result = result + coincidence;
            }
        }

        result = result / longList.size();
        if (result > 100) result = 100;

        return (int) (result);
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
        result = (int) (Math.round(result  * 100.00 / length));
        return result;
    }
}
