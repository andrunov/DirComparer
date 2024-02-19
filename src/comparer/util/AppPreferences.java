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


    public static void setMainWindowWidth(Double width){
        Preferences prefs = Preferences.userNodeForPackage(AppPreferences.class);
        prefs.put("mainWindowWidth", String.valueOf(width));
    }

    public static double getMainWindowWidth(){
        Preferences prefs = Preferences.userNodeForPackage(AppPreferences.class);
        return Double.parseDouble(prefs.get("mainWindowWidth", "650.00"));
         //600 for comparer
    }

    public static void setMainWindowHeight(Double height){
        Preferences prefs = Preferences.userNodeForPackage(AppPreferences.class);
        prefs.put("mainWindowHeight", String.valueOf(height));
    }

    public static double getMainWindowHeight(){
        Preferences prefs = Preferences.userNodeForPackage(AppPreferences.class);
        return Double.parseDouble(prefs.get("mainWindowHeight", "600.00"));
        //200 for comparer
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
        return Double.parseDouble(prefs.get("settingsWindowHeight", "150.00"));
    }

    public static void setExactWordMatch(boolean toShow){
        Preferences prefs = Preferences.userNodeForPackage(AppPreferences.class);
        prefs.put("exactWordMatch", String.valueOf(toShow));
    }

    public static boolean getExactWordMatch() {
        Preferences prefs = Preferences.userNodeForPackage(AppPreferences.class);
        return Boolean.parseBoolean(prefs.get("exactWordMatch", "FALSE"));
    }

    public static void setSplitPaneDividerPosition(Double position) {
        Preferences prefs = Preferences.userNodeForPackage(AppPreferences.class);
        prefs.put("splitPaneDividerPosition", String.valueOf(position));
    }

    public static double getSplitPaneDividerPosition() {
        Preferences prefs = Preferences.userNodeForPackage(AppPreferences.class);
        return Double.parseDouble(prefs.get("splitPaneDividerPosition", "0.18"));
    }

    public static double getTableColumnWidth(String columnId) {
        Preferences prefs = Preferences.userNodeForPackage(AppPreferences.class);
        String key = String.format("tableColumnWidth_%s", columnId);
        String def = null;
        if (columnId.equals("rowSimilar")) {
            def = "50";
        } else if (columnId.equals("rowFolderName")) {
            def = "215";
        } else if (columnId.equals("rowFileName")) {
            def = "275";
        } else if (columnId.equals("rowFileSize")) {
            def = "90";
        }
        return Double.parseDouble(prefs.get(key, def));
    }

    public static void setTableColumnWidth(String columnId, double width) {
        Preferences prefs = Preferences.userNodeForPackage(AppPreferences.class);
        String key = String.format("tableColumnWidth_%s", columnId);
        prefs.put(key, String.valueOf(width));
    }
}
