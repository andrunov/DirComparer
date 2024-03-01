package comparer.model;

import comparer.style.Skin;
import comparer.util.AppPreferences;

public class Settings {

    private String[] allowedExtensions;

    private boolean exactWordMatch;

    private boolean saveHtmlReport;

    private Skin skin;

    public void loadFields() {
        this.allowedExtensions = AppPreferences.getFilterExtensions();
        this.exactWordMatch = AppPreferences.getExactWordMatch();
        this.skin = AppPreferences.getSkin();
    }

    public void saveFields() {
        AppPreferences.setFilterExtensions(this.allowedExtensions);
        AppPreferences.setExactWordMatch(this.exactWordMatch);
        AppPreferences.saveSkin(this.skin);
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

    public boolean isSaveHtmlReport() {
        return saveHtmlReport;
    }

    public void setSaveHtmlReport(boolean saveHtmlReport) {
        this.saveHtmlReport = saveHtmlReport;
    }

    public Skin getSkin() {
        return skin;
    }

    public void setSkin(Skin skin) {
        this.skin = skin;
    }
}
