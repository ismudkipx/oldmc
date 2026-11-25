package me.mudkipx.oldmc.config;

public enum VersionFormat {
    YEAR("year"), MAJOR("major"), PATCH_1_21("1.21.x");

    public static final VersionFormat DEFAULT = MAJOR;
    final String key;

    VersionFormat(String key) {
        this.key = key;
    }

    String labelKey() {
        return "oldmc.format." + key;
    }

    static VersionFormat fromKey(String key) {
        for (var format : values()) {
            if (format.key.equals(key)) return format;
        }
        return null;
    }
}
