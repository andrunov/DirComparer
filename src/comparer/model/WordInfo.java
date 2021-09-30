package comparer.model;

import java.util.Map;
import java.util.Objects;

public class WordInfo {

    private int ID;

    private String word;

    private int quantity;

    private double weight;

    private Map<Integer, WordInfo> similarWords;

    public WordInfo(String word) {
        this.word = word;
        this.quantity = 1;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WordInfo wordInfo = (WordInfo) o;
        return word.equals(wordInfo.word);
    }

    @Override
    public int hashCode() {
        return Objects.hash(word);
    }



}
