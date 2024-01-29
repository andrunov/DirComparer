package comparer.model;

import comparer.util.AppPreferences;

public class Settings {

    private String[] allowedExtensions;

    private boolean exactWordMatch;

    public void loadFields() {
        this.allowedExtensions = AppPreferences.getFilterExtensions();
        this.exactWordMatch = AppPreferences.getExactWordMatch();
    }

    public void saveFields() {
        AppPreferences.setFilterExtensions(this.allowedExtensions);
        AppPreferences.setExactWordMatch(this.exactWordMatch);
    }


    public String[] getAllowedExtensions() {
        return allowedExtensions;
    }

    public void setAllowedExtensions(String[] allowedExtensions) {
        this.allowedExtensions = allowedExtensions;
    }

    public boolean isExactWordMatch() {
        return exactWordMatch;
    }

    public void setExactWordMatch(boolean exactWordMatch) {
        this.exactWordMatch = exactWordMatch;
    }


}
