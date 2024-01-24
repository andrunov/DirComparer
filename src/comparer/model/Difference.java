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

        int[] coincidences = new int[longList.size()];
        int i = 0;
        outerCycle: for (WordInfo first : longList) {
            /*  temporary switch off
            if (first.isIgnorance()) {
                i++;
                continue;
            }
             */
            for (WordInfo second : shortList) {
               // if (second.isIgnorance()) continue; temporary switch off
                if (first.getID() == second.getID()) {
                    coincidences[i] = 100;
                    i++;
                    continue outerCycle;

                } else {
                    if (analyzeByLetters) {
                        int coincidence = 0;
                        if (first.getSimilarWords() != null && first.getSimilarWords().containsKey(second)) {
                            coincidence = first.getSimilarWords().get(second);
                        } else {
                            coincidence = compareWords(first.getWord(), second.getWord());
                            first.setSimilarWords(new HashMap<>());
                            first.getSimilarWords().put(second, coincidence);
                            //TODO отладочный код
                            System.out.println(first.getWord() +"\t" + second.getWord() + "\t" + coincidence);
                        }
                        if (coincidence > coincidences[i]) {
                            coincidences[i] = coincidence;
                        }
                    }
                }
            }
            i++;
        }

        return this.retrieve(coincidences);
    }


    /*
     * find quantity of similar letters in two words,
     * return 100 means words equality
     * return 0 means that words are definitely different
     * return value in range from 1 nj 99 means that words are similar in that degree */
    public int compareWords(String word1, String word2){

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
        int gap = 0;
        int shift = 0;
        int diffChangeCount = 0;
        int[] foundIndexes = new int[longWord.length()];
        for (int i = 0; i < shortWord.length(); i++){
            boolean found = false;
            //System.out.println(i + "-" + shortWord.charAt(i));
            for (int j = (i == 0 ? 0 : i - 1); (j <= i + 1) && (j < longWord.length()) ; j++){

                //System.out.print("  " + j + "-" + longWord.charAt(j));
                if (shortWord.charAt(i) == longWord.charAt(j)) {
                    found = true;

                    foundIndexes[j] = 1;
                    //System.out.print("  совп ");
                    //printArr(foundIndexes);
                    //System.out.print("i-j=" + (i-j));
                    if ((i - j) != shift) {
                        diffChangeCount++;
                        if (diffChangeCount >= 2) {
                            //System.out.print("  прер diffChangeCount > 2 ");
                            return 0;
                        }
                        shift = i - j;
                        //System.out.println("    diff: " + shift);
                    }
                    result = result + 1;
                    //System.out.print("  результат: " + result);
                    //System.out.println();
                    break;
                }
                //System.out.println();
            }
            if (!found) {
                gap++;
            }
            if (gap >= 2) {
                //System.out.print("  прер gap = " + gap );
                return 0;
            }

        }
        int length = word1.length();
        result = (int) (Math.round(result  * 100.00 / length));
   //     if (result < MIN_DIFF) result = 0;
        return result;
    }

    private int retrieve (int[] coincidence) {
        int result = 0;
        int foundResult = 0;
        int foundCounter = 0;
        int notFoundCounter = 0;
        for (int i : coincidence) {
            if (i > 0) {
                foundCounter++;
                foundResult = foundResult + i;
            }
            else {
                notFoundCounter++;
            }
            // if i == -1 do nothing, this is an ignorance word marker
        }

        if (foundCounter > 0) {
            foundResult = foundResult / foundCounter;
            result = foundResult - (foundResult / (foundCounter + notFoundCounter)) * notFoundCounter;
        }
        return result;
    }

    private void printArr(int[] array) {
        System.out.print("[");
        for (int element : array) {
            System.out.print(element + ", ");
        }
        System.out.print("]");
    }

}
