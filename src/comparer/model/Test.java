package comparer.model;

public class Test {

    public static void main(String[] args) {
        Difference difference = new Difference(null, null);
        String string2 = "любовь";
        String string1 = "любите";
        System.out.println(string1 + " & " + string2);
        int diff = difference.compareWords(string1, string2);
        System.out.println();
        System.out.println("diff = " + diff);
    }



}
