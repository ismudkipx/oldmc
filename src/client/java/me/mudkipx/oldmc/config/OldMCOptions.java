package me.mudkipx.oldmc.config;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.SemanticVersion;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public enum OldMCOptions {
    PERCENTAGE("percentage"), GRID("grid"), DIRT_BACKGROUND("dirtBackground"), DEBUG("debug"),
    TITLE_SIDE_BUTTONS("titleSideButtons"), PAUSE_MENU_TEXT("pauseMenuText"), REMOVE_FEEDBACK("removeFeedback"),
    REMOVE_PLAYER_REPORTING("removePlayerReporting"), VERSION("version");

    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("oldmc.json");
    static final boolean DEFAULT_MAJOR_VERSION_FORMAT = true;
    private static boolean majorVersionFormat = DEFAULT_MAJOR_VERSION_FORMAT;
    public static final boolean HAS_PAUSE_MENU_ICONS = FabricLoader.getInstance().getModContainer("minecraft")
            .map(mod -> mod.getMetadata().getVersion())
            .filter(SemanticVersion.class::isInstance).map(SemanticVersion.class::cast)
            .map(v -> v.getVersionComponent(0) > 26 || v.getVersionComponent(0) == 26 && v.getVersionComponent(1) >= 2)
            .orElse(false);
    private static FriendButtonPlacement friendPlacement = FriendButtonPlacement.DEFAULT;
    private static TitleFriendPlacement titleFriendPlacement = TitleFriendPlacement.DEFAULT;
    public final String key;
    public final boolean defaultValue;
    private boolean enabled;

    OldMCOptions(String key) {
        this(key, true);
    }

    OldMCOptions(String key, boolean defaultValue) {
        this.key = key;
        this.enabled = this.defaultValue = defaultValue;
    }

    public static FriendButtonPlacement friendPlacement() {
        return friendPlacement;
    }

    static void setFriendPlacement(FriendButtonPlacement placement) {
        friendPlacement = placement;
    }

    public static TitleFriendPlacement titleFriendPlacement() {
        return titleFriendPlacement;
    }

    static void setTitleFriendPlacement(TitleFriendPlacement placement) {
        titleFriendPlacement = placement;
    }

    public static boolean usesMajorVersionFormat() {
        return majorVersionFormat;
    }

    static void setMajorVersionFormat(boolean value) {
        majorVersionFormat = value;
    }

    public boolean enabled() {
        return enabled;
    }

    void set(boolean value) {
        enabled = value;
    }

    static {
        if (Files.isRegularFile(FILE)) {
            try (var reader = Files.newBufferedReader(FILE)) {
                var data = JsonParser.parseReader(reader).getAsJsonObject();
                var placement = data.get("friendButtonPlacement");
                if (placement != null && placement.isJsonPrimitive() && placement.getAsJsonPrimitive().isString()) {
                    friendPlacement = FriendButtonPlacement.fromKey(placement.getAsString());
                }
                var titlePlacement = data.get("titleFriendButtonPlacement");
                if (titlePlacement != null && titlePlacement.isJsonPrimitive() && titlePlacement.getAsJsonPrimitive().isString()) {
                    titleFriendPlacement = TitleFriendPlacement.fromKey(titlePlacement.getAsString());
                }
                var format = data.get("majorVersionFormat");
                if (format != null && format.isJsonPrimitive() && format.getAsJsonPrimitive().isBoolean()) {
                    majorVersionFormat = format.getAsBoolean();
                }
                for (var option : values()) {
                    var value = data.get(option.key);
                    if (value != null && value.isJsonPrimitive() && value.getAsJsonPrimitive().isBoolean()) {
                        option.enabled = value.getAsBoolean();
                    }
                }
            } catch (RuntimeException | IOException e) {
                LoggerFactory.getLogger("OldMC").warn("Could not read {}", FILE, e);
            }
        }
    }

    static void save() {
        var data = new JsonObject();
        data.addProperty("friendButtonPlacement", friendPlacement.key());
        data.addProperty("titleFriendButtonPlacement", titleFriendPlacement.key());
        data.addProperty("majorVersionFormat", majorVersionFormat);
        for (var option : values()) data.addProperty(option.key, option.enabled);
        try {
            Files.createDirectories(FILE.getParent());
            Path tmp = Files.createTempFile(FILE.getParent(), "oldmc-", ".tmp");
            try {
                Files.writeString(tmp, new GsonBuilder().setPrettyPrinting().create().toJson(data) + "\n");
                try {
                    Files.move(tmp, FILE, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
                } catch (java.nio.file.AtomicMoveNotSupportedException e) {
                    Files.move(tmp, FILE, StandardCopyOption.REPLACE_EXISTING);
                }
            } finally {
                Files.deleteIfExists(tmp);
            }
        } catch (IOException e) {
            LoggerFactory.getLogger("OldMC").warn("Could not save {}", FILE, e);
        }
    }
}
