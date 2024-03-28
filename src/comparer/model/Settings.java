package comparer.model;

import comparer.style.Skin;
import comparer.util.AppPreferences;

public class Settings {

    private String[] allowedExtensions;

    private boolean exactWordMatch;

    private boolean writeHtmlReport;

    private Skin skin;

    public void loadFields() {
        this.allowedExtensions = AppPreferences.getFilterExtensions();
        this.exactWordMatch = AppPreferences.getExactWordMatch();
        this.skin = AppPreferences.getSkin();
        this.writeHtmlReport = AppPreferences.getWriteHtmlReport();
    }

    public void saveFields() {
        AppPreferences.setFilterExtensions(this.allowedExtensions);
        AppPreferences.setExactWordMatch(this.exactWordMatch);
        AppPreferences.saveSkin(this.skin);
        AppPreferences.saveWriteHtmlReport(this.writeHtmlReport);
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

    public boolean isWriteHtmlReport() {
        return writeHtmlReport;
    }

    public void setWriteHtmlReport(boolean writeHtmlReport) {
        this.writeHtmlReport = writeHtmlReport;
    }

    public Skin getSkin() {
        return skin;
    }

    public void setSkin(Skin skin) {
        this.skin = skin;
    }
}
