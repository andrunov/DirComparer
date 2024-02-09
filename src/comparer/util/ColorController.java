package comparer.util;

public class ColorController {

    public static String getBgRGBA (int similarity) {

        int R = 0;
        int G = 0;
        int B = 0;

        if (similarity > 75 && similarity <=100) {
            R = 0;
            G = 255;
            B = (255/25) * (100 - similarity);
        } else if (similarity > 50 && similarity <= 75) {
            R = 0;
            G = (255/25) * (similarity - 50);
            B = 255;
        } else if (similarity > 25 && similarity <= 50) {
            R = (255/25) * (50 - similarity);
            G = 0;
            B = 255;
        } else if (similarity > 0 && similarity <= 25) {
            R = 255;
            G = 0;
            B = (255/25) * similarity;
        }
        return String.format("%s, %s, %s", R, G, B);
    }

    public static String getTextRGB(int similarity) {

        int R = 0;
        int G = 0;
        int B = 0;

        if (similarity > 75 && similarity <=100) {
            R = 0;
            G = 128;
            B = (128/25) * (100 - similarity);
        } else if (similarity > 50 && similarity <= 75) {
            R = 0;
            G = (128/25) * (similarity - 50);
            B = 128;
        } else if (similarity > 25 && similarity <= 50) {
            R = (128/25) * (50 - similarity);
            G = 0;
            B = 128;
        } else if (similarity > 0 && similarity <= 25) {
            R = 128;
            G = 0;
            B = (128/25) * similarity;
        }
        return String.format("%s, %s, %s", R, G, B);
    }

}
