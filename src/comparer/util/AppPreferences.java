package comparer.util;

import java.io.File;
import java.util.prefs.Preferences;

/**
 * Class for save app's settings outside
 */
public class AppPreferences {

    /*get last selected directory*/
    public static File getDirectory(String key) {
        Preferences prefs = Preferences.userNodeForPackage(AppPreferences.class);
        String filePath = prefs.get(key, null);
        if (filePath != null) {
            return new File(filePath);
        } else {
            return null;
        }
    }

    /*set last selected directory*/
    public static void setDirectory(File file, String key) {
        Preferences prefs = Preferences.userNodeForPackage(AppPreferences.class);
        if (file != null) {
            prefs.put(key, file.getPath());
        }
    }

    /*get files extensions for FileFilter*/
    public static String[] getFilterExtensions() {
        Preferences prefs = Preferences.userNodeForPackage(AppPreferences.class);
        String extensions = prefs.get("filterExtensions", "mp3 wma wmw mp4 avi mkv");
        if (!Formatter.stringIsEmpty(extensions)) {
            return extensions.split(" ");
        } else {
            return new String[]{};
        }
    }

    /*set files extensions for FileFilter*/
    public static void setFilterExtensions(String[] extensions) {
        Preferences prefs = Preferences.userNodeForPackage(AppPreferences.class);
        prefs.put("filterExtensions", Formatter.getArrayAsString(extensions));
    }

    /*get minimum word length*/
    public static int getMinStringLength() {
        Preferences prefs = Preferences.userNodeForPackage(AppPreferences.class);
        int minLength = 0;
        try {
            minLength = Integer.parseInt(prefs.get("minStringLength", "2"));
        }catch (NumberFormatException e){
            e.printStackTrace();
        }
        if (minLength < 1){
            minLength = 1;
        }
        return minLength;
    }

    /*set minimum word length*/
    public static void setMinStringLength(String minStringLength) {
        Preferences prefs = Preferences.userNodeForPackage(AppPreferences.class);
        prefs.put("minStringLength", minStringLength);
    }

    public static void setMainWindowWidth(Double width){
        Preferences prefs = Preferences.userNodeForPackage(AppPreferences.class);
        prefs.put("mainWindowWidth", String.valueOf(width));
    }

    public static double getMainWindowWidth(){
        Preferences prefs = Preferences.userNodeForPackage(AppPreferences.class);
        return Double.parseDouble(prefs.get("mainWindowWidth", "600.00"));
    }

    public static void setMainWindowHeight(Double height){
        Preferences prefs = Preferences.userNodeForPackage(AppPreferences.class);
        prefs.put("mainWindowHeight", String.valueOf(height));
    }

    public static double getMainWindowHeight(){
        Preferences prefs = Preferences.userNodeForPackage(AppPreferences.class);
        return Double.parseDouble(prefs.get("mainWindowHeight", "200.00"));
    }

    public static void setSettingsWindowHeight(double height) {
        Preferences prefs = Preferences.userNodeForPackage(AppPreferences.class);
        prefs.put("settingsWindowHeight", String.valueOf(height));
    }

    public static void setSettingsWindowWidth(double width) {
        Preferences prefs = Preferences.userNodeForPackage(AppPreferences.class);
        prefs.put("settingsWindowWidth", String.valueOf(width));
    }

    public static double getSettingsWindowWidth() {
        Preferences prefs = Preferences.userNodeForPackage(AppPreferences.class);
        return Double.parseDouble(prefs.get("settingsWindowWidth", "600.00"));
    }

    public static double getSettingsWindowHeight() {
        Preferences prefs = Preferences.userNodeForPackage(AppPreferences.class);
        return Double.parseDouble(prefs.get("settingsWindowHeight", "160.00"));
    }

    public static void setShowSimilarityMiddle(boolean toShow){
        Preferences prefs = Preferences.userNodeForPackage(AppPreferences.class);
        prefs.put("showSimilarityMiddle", String.valueOf(toShow));
    }

    public static void setShowSimilarityLow(boolean toShow){
        Preferences prefs = Preferences.userNodeForPackage(AppPreferences.class);
        prefs.put("showSimilarityLow", String.valueOf(toShow));
    }

    public static boolean getShowSimilarityMiddle() {
        Preferences prefs = Preferences.userNodeForPackage(AppPreferences.class);
        return Boolean.parseBoolean(prefs.get("showSimilarityMiddle", "FALSE"));
    }

    public static boolean getShowSimilarityLow() {
        Preferences prefs = Preferences.userNodeForPackage(AppPreferences.class);
        return Boolean.parseBoolean(prefs.get("showSimilarityLow", "FALSE"));
    }

    public static void setAnalyseByLetters(boolean toShow){
        Preferences prefs = Preferences.userNodeForPackage(AppPreferences.class);
        prefs.put("analyseByLetters", String.valueOf(toShow));
    }

    public static boolean getAnalyseByLetters() {
        Preferences prefs = Preferences.userNodeForPackage(AppPreferences.class);
        return Boolean.parseBoolean(prefs.get("analyseByLetters", "FALSE"));
    }
}
