package comparer.model;

import comparer.util.AppPreferences;

public class Settings {

    private String[] allowedExtensions;

    private boolean analyzeByLetters;

    public void loadFields() {
        this.allowedExtensions = AppPreferences.getFilterExtensions();
        this.analyzeByLetters = AppPreferences.getAnalyseByLetters();
    }

    public void saveFields() {
        AppPreferences.setFilterExtensions(this.allowedExtensions);
        AppPreferences.setAnalyseByLetters(this.analyzeByLetters);
    }


    public String[] getAllowedExtensions() {
        return allowedExtensions;
    }

    public void setAllowedExtensions(String[] allowedExtensions) {
        this.allowedExtensions = allowedExtensions;
    }

    public boolean isAnalyzeByLetters() {
        return analyzeByLetters;
    }

    public void setAnalyzeByLetters(boolean analyzeByLetters) {
        this.analyzeByLetters = analyzeByLetters;
    }


}
