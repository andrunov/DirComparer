package comparer.model;

import java.util.List;

public class Difference {

    private List<WordInfo> firstList;

    private List<WordInfo> secondList;

    private boolean[] congruence;

    private int[] order;

    public Difference(List<WordInfo> firstList, List<WordInfo> secondList) {
        this.firstList = firstList;
        this.secondList = secondList;
    }

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

        this.congruence = new boolean[shortList.size()];
        this.order = new int[shortList.size()];

        outer: for (int i = 0; i < shortList.size(); i++) {
            WordInfo first = shortList.get(i);
            for (int j = 0; j < longList.size(); j++) {
                WordInfo second = longList.get(j);
                if (first.getID() == second.getID()) {
                    this.congruence[i] = true;
                    int order = ((i - j) / this.congruence.length) * 100;
                    if (order == 0) order = 100;
                    else if (order < 0) order = order * -1;
                    this.order[i] = order;
                    continue outer;
                }
            }
        }

        for (int i = 0; i < this.congruence.length; i++) {
            if (this.congruence[i]) {
                result = result + this.order[i];
            }
            result = result/this.congruence.length;
        }

        return result;
    }
}
