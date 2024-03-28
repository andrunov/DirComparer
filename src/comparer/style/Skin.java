package comparer.style;

import java.util.ResourceBundle;

public enum Skin {
    CIAN("Cian"),
    GRAY("Gray"),
    GOLD("Gold"),
    NAVY("Navy"),
    PURPLE("Purple");

    private final String repr;

    Skin(String repr) {
        this.repr = repr;
    }

    public String getRepr() {
        return repr;
    }

    public static String[] getLocaleValues(ResourceBundle resourceBundle) {
        Skin[] values = Skin.values();
        String[] result = new String[values.length];
        for (int i = 0; i < values.length; i++ ) {
            result[i] = resourceBundle.getString(values[i].toString());
        }
        return result;
    }

    public static Skin getByLocalValue(ResourceBundle resourceBundle, String value) {
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

    public static String getTableBorderColor(Skin skin) {
        if (skin == CIAN) return "navy";
        else if (skin == GRAY) return "#5b5b5b";
        else if (skin == GOLD) return "#8f4407";
        else if (skin == NAVY) return "#000435";
        else if (skin == PURPLE) return "#3c3252";
        else return "navy";
    }

    public static String getBgColor(Skin skin) {
        if (skin == CIAN) return "rgba(148, 182, 222, 0.25)";
        else if (skin == GRAY) return "rgba(192, 192, 192, 0.45)";
        else if (skin == GOLD) return "rgba(196, 156, 72, 0.2)";
        else if (skin == NAVY) return "rgba(7, 114, 130, 0.2)";
        else if (skin == PURPLE) return "rgba(119, 91, 171, 0.2)";
        else return "navy";
    }

    public static String getTextColor(Skin skin) {
        if (skin == CIAN) return "black";
        else if (skin == GRAY) return "black";
        else if (skin == GOLD) return "#4c3a15";
        else if (skin == NAVY) return "#071e01";
        else if (skin == PURPLE) return "#2a2338";
        else return "navy";
    }

}
