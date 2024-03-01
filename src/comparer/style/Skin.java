package comparer.style;

import java.util.ResourceBundle;

public enum Skin {
    CIAN,
    GRAY;


    public static String[] getLocaleValues(ResourceBundle resourceBundle) {
        Skin[] values = Skin.values();
        String[] result = new String[values.length];
        for (int i = 0; i < values.length; i++ ) {
            result[i] = resourceBundle.getString(values[i].toString());
        }
        return result;
    }

    public static Skin getByValue(ResourceBundle resourceBundle, String value) {
        Skin[] values = Skin.values();
        Skin result = null;
        for (int i = 0; i < values.length; i++ ) {
            String representation = resourceBundle.getString(values[i].toString());
            if (representation.equals(value)) result = values[i];
        }
        return result;
    }


    public String getLocale(ResourceBundle resourceBundle) {
        return resourceBundle.getString(this.toString());
    }

}
