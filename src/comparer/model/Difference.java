package comparer.model;

import java.util.List;

public class Difference {

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
    public int getDifference() {
        int result = 0;
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

        boolean[] congruence = new boolean[shortList.size()];
        int[] order = new int[shortList.size()];

        outer: for (int i = 0; i < shortList.size(); i++) {
            WordInfo first = shortList.get(i);
            for (int j = 0; j < longList.size(); j++) {
                WordInfo second = longList.get(j);
                if (first.getID() == second.getID()) {
                    congruence[i] = true;
                    int exactOrder = 100 - 100 * (i - j)/longList.size();
                    if (exactOrder < 0) exactOrder = exactOrder * -1;
                    order[i] = exactOrder;
                    continue outer;
                }  //TODO insert analyze by letters here
            }
        }

        for (int i = 0; i < congruence.length; i++) {
            if (congruence[i]) {
                result = result + order[i];
            }
            result = result/congruence.length;
            result = result * shortList.size() / longList.size();
        }

        return result;
    }
}
