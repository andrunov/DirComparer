package comparer.model;

import java.util.Map;
import java.util.Objects;

public class WordInfo {

    /**
     * unique identifier
     * */
    private int ID;

    /**
     * contains word itself
     * */
    private final String word;

    /**
     * quantity ow this word in representation
     * */
    private int quantity;

    /**
     * weight of this word in representation.
     * Than bigger weight, than more unique word.
     * Small value means that word is very spread in representation.
     * */
    private double weight;


    /**
     * words similar to this one. Key - similar word itself,
     * value - similarity coefficient (100% means that word is equal)
     * */
    private Map<WordInfo, Integer> similarWords;

    public WordInfo(String word) {
        this.word = word;
        this.quantity = 1;
    }

    public String getWord() {
        return word;
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

    public int getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public Map<WordInfo, Integer> getSimilarWords() {
        return similarWords;
    }

    public void setSimilarWords(Map<WordInfo, Integer> similarWords) {
        this.similarWords = similarWords;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WordInfo wordInfo = (WordInfo) o;
        return ID == wordInfo.ID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ID);
        sb.append('\t');
        sb.append(word);
        sb.append('\t');
        sb.append(quantity);
        sb.append('\t');
        sb.append(weight);
        return sb.toString();
    }
}
