package me.mudkipx.oldmc.version;

import me.mudkipx.oldmc.config.OldMCOptions;

import java.util.regex.Pattern;

public final class VersionLabels {
    private static final Pattern YEAR_26_RELEASE = Pattern.compile("26\\.([1-9][0-9]{0,8})(\\.[1-9][0-9]*)?");
    public static final String NAME = "Lnet/minecraft/WorldVersion;name()Ljava/lang/String;";

    public static String display(String version) {
        if (!OldMCOptions.VERSION.enabled()) return version;
        if (OldMCOptions.usesMajorVersionFormat()) {
            var release = YEAR_26_RELEASE.matcher(version);
            if (!release.matches()) return version;
            int major = 21 + Integer.parseInt(release.group(1));
            String patch = release.group(2);
            return "1." + major + (patch == null ? "" : patch);
        }
        return switch (version) {
            case "26.1" -> "1.26";
            case "26.1.1" -> "1.26.1";
            case "26.1.2" -> "1.26.2";
            case "26.2" -> "1.26.3";
            case "26.3" -> "1.26.4";
            default -> version;
        };
    }

    public static String launchProfile(String profile) {
        if (!profile.startsWith("fabric-loader-")) return display(profile);
        int suffix = profile.lastIndexOf('-') + 1;
        String version = profile.substring(suffix);
        String label = display(version);
        return version.equals(label) ? profile : profile.substring(0, suffix) + label;
    }

    private VersionLabels() {}
}
