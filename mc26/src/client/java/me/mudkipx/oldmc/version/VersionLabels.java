package me.mudkipx.oldmc.version;

import me.mudkipx.oldmc.config.OldMCOptions;
import me.mudkipx.oldmc.config.VersionFormat;

import java.util.regex.Pattern;

public final class VersionLabels {
    private static final Pattern YEAR_26_RELEASE = Pattern.compile("26\\.([1-9][0-9]{0,8})(\\.[1-9][0-9]*)?");
    private static final Pattern MOD_GAME_VERSION = Pattern.compile("\\+mc(26\\.[0-9]+(?:\\.[0-9]+)?)(?:-build\\.[0-9]+|-local)?$");
    private static final String IRIS_DEV_LABEL = " (Development Environment)";
    public static final String NAME = "Lnet/minecraft/WorldVersion;name()Ljava/lang/String;";

    public static String display(String version) {
        if (!OldMCOptions.VERSION.enabled()) return version;
        var format = OldMCOptions.versionFormat();
        if (format == VersionFormat.MAJOR) {
            var release = YEAR_26_RELEASE.matcher(version);
            if (!release.matches()) return version;
            int major = 21 + Integer.parseInt(release.group(1));
            String patch = release.group(2);
            return "1." + major + (patch == null ? "" : patch);
        }
        // Each released update, including a hotfix, advances the displayed patch by one.
        int update = switch (version) {
            case "26.1" -> 0;
            case "26.1.1" -> 1;
            case "26.1.2" -> 2;
            case "26.2" -> 3;
            case "26.3" -> 4;
            default -> -1;
        };
        if (update < 0) return version;
        if (format == VersionFormat.PATCH_1_21) return "1.21." + (12 + update);
        return update == 0 ? "1.26" : "1.26." + update;
    }

    public static String sodiumDebugVersion(String sodiumVersion) {
        if (!OldMCOptions.SODIUM_F3_VERSION.enabled() || !OldMCOptions.VERSION.enabled()) return sodiumVersion;
        return formatModGameVersion(sodiumVersion, sodiumVersion.length());
    }

    public static String irisDebugVersion(String formattedVersion) {
        if (!OldMCOptions.IRIS_F3_VERSION.enabled() || !OldMCOptions.VERSION.enabled()) return formattedVersion;
        int end = formattedVersion.length();
        if (formattedVersion.endsWith(IRIS_DEV_LABEL)) end -= IRIS_DEV_LABEL.length();
        return formatModGameVersion(formattedVersion, end);
    }

    private static String formatModGameVersion(String modVersion, int versionEnd) {
        var match = MOD_GAME_VERSION.matcher(modVersion).region(0, versionEnd);
        if (!match.find()) return modVersion;
        String version = match.group(1);
        String label = display(version);
        if (label.equals(version)) return modVersion;
        return modVersion.substring(0, match.start(1)) + label + modVersion.substring(match.end(1));
    }

    public static String immediatelyFastDebugVersion(String modVersion) {
        if (!OldMCOptions.IMMEDIATELY_FAST_F3_VERSION.enabled() || !OldMCOptions.VERSION.enabled()) return modVersion;
        int separator = modVersion.lastIndexOf('+');
        if (separator < 0) return modVersion;
        String gameVersion = modVersion.substring(separator + 1);
        String label = display(gameVersion);
        return gameVersion.equals(label) ? modVersion : modVersion.substring(0, separator + 1) + label;
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
