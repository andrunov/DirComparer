package comparer.model;

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
    public int getDifference( boolean analyzeByLetters) {
        double result = 0;
        List<WordInfo> shortList = null;
        List<WordInfo> longList = null;

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

        double[] congruence = new double[shortList.size()];

        for (int i = 0; i < shortList.size(); i++) {
            WordInfo first = shortList.get(i);
            for (int j = 0; j < longList.size(); j++) {
                WordInfo second = longList.get(j);

                if (first.getID() == second.getID()) {
                   // congruence[i] = first.getWeight();
                    congruence[i] = 1;

                } else if (analyzeByLetters && first.getSimilarWords() != null) {
                    if (first.getSimilarWords().containsKey(second)) {
                        int difference = first.getSimilarWords().get(second);
                        congruence[i] = difference * (first.getWeight());
                    }
                }

                double order = 1 - (double) (i - j) / longList.size();
                if (order < 0) order = order * -1;
                //congruence[i] = congruence[i] * order;
                congruence[i] = congruence[i];
            }
        }

        for (double value : congruence) {
            result = result + value;
        }

        result = result / longList.size();

        return (int) (result * 100);
    }
}
