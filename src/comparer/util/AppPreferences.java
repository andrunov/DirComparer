package comparer.util;

import java.io.File;
import java.util.prefs.Preferences;

/**
 * Class for save app's settings outside
 */
public class AppPreferences {

    /*get last selected directory*/
    public static File getDirectory() {
        Preferences prefs = Preferences.userNodeForPackage(AppPreferences.class);
        String filePath = prefs.get("directory", null);
        if (filePath != null) {
            return new File(filePath);
        } else {
            return null;
        }
    }

    /*set last selected directory*/
    public static void setDirectory(File file) {
        Preferences prefs = Preferences.userNodeForPackage(AppPreferences.class);
        if (file != null) {
            prefs.put("directory", file.getPath());
        }
    }

    /*get files extensions for Filter*/
    public static String[] getFilterExtensions() {
        Preferences prefs = Preferences.userNodeForPackage(AppPreferences.class);
        String extensions = prefs.get("filterExtensions", null);
        if (!Formatter.stringIsEmpty(extensions)) {
            return extensions.split(" ");
        } else {
            return new String[]{};
        }
    }

    /*set files extensions for Filter*/
    public static void setFilterExtensions(String[] extensions) {
        Preferences prefs = Preferences.userNodeForPackage(AppPreferences.class);
        prefs.put("filterExtensions", Formatter.getArrayAsString(extensions));
    }

    /*get minimum word length*/
    public static int getMinStringLength() {
        Preferences prefs = Preferences.userNodeForPackage(AppPreferences.class);
        int minLength = 0;
        try {
            minLength = Integer.parseInt(prefs.get("minStringLength", null));
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
}
