package comparer.model;

public class Test {

    public static void main(String[] args) {
        Difference difference = new Difference(null, null);
        String string1 = "любовь";
        String string2 = "драгуны";
        System.out.println(string1 + " & " + string2);
        int diff = difference.compareWords2(string1, string2);
        System.out.println();
        System.out.println("diff = " + diff);
    }



}
