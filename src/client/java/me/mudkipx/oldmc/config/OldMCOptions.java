package me.mudkipx.oldmc.config;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.mudkipx.oldmc.mixin.DebugProfileAccessor;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.SemanticVersion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenProfile;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public enum OldMCOptions {
    PERCENTAGE("percentage"), GRID("grid"), DIRT_BACKGROUND("dirtBackground"), DEBUG("debug"),
    SODIUM_F3_RIGHT("sodiumF3Right"), SODIUM_F3_VERSION("sodiumF3Version"),
    IMMEDIATELY_FAST_F3_RIGHT("immediatelyFastF3Right"), IMMEDIATELY_FAST_F3_VERSION("immediatelyFastF3Version"),
    IRIS_F3_RIGHT("irisF3Right"), IRIS_F3_VERSION("irisF3Version"),
    CLASSIC_BABY_MOBS("classicBabyMobs"),
    CLASSIC_CREATE_WORLD("classicCreateWorld"),
    TITLE_SIDE_BUTTONS("titleSideButtons"), PAUSE_MENU_TEXT("pauseMenuText"), REMOVE_FEEDBACK("removeFeedback"),
    REMOVE_PLAYER_REPORTING("removePlayerReporting"), VERSION("version");

    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("oldmc.json");
    static final boolean DEFAULT_MAJOR_VERSION_FORMAT = true;
    private static boolean majorVersionFormat = DEFAULT_MAJOR_VERSION_FORMAT;
    private static boolean dirty;
    private static final boolean HAS_SODIUM = FabricLoader.getInstance().isModLoaded("sodium");
    private static final boolean HAS_IMMEDIATELY_FAST = FabricLoader.getInstance().isModLoaded("immediatelyfast");
    private static final boolean HAS_IRIS = FabricLoader.getInstance().isModLoaded("iris");
    private static final boolean HAS_YEAR_VERSION = FabricLoader.getInstance().getModContainer("minecraft")
            .orElseThrow().getMetadata().getVersion().getFriendlyString().startsWith("26.");
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
        if (friendPlacement != placement) {
            friendPlacement = placement;
            dirty = true;
        }
    }

    public static TitleFriendPlacement titleFriendPlacement() {
        return titleFriendPlacement;
    }

    static void setTitleFriendPlacement(TitleFriendPlacement placement) {
        if (titleFriendPlacement != placement) {
            titleFriendPlacement = placement;
            dirty = true;
        }
    }

    public static boolean usesMajorVersionFormat() {
        return majorVersionFormat;
    }

    static void setMajorVersionFormat(boolean value) {
        if (majorVersionFormat != value) {
            majorVersionFormat = value;
            dirty = true;
        }
    }

    boolean available() {
        return switch (this) {
            case SODIUM_F3_RIGHT -> HAS_SODIUM;
            case VERSION, CLASSIC_BABY_MOBS -> HAS_YEAR_VERSION;
            case SODIUM_F3_VERSION -> HAS_SODIUM && HAS_YEAR_VERSION;
            case IMMEDIATELY_FAST_F3_RIGHT -> HAS_IMMEDIATELY_FAST;
            case IRIS_F3_RIGHT -> HAS_IRIS;
            case IMMEDIATELY_FAST_F3_VERSION -> HAS_IMMEDIATELY_FAST && HAS_YEAR_VERSION;
            case IRIS_F3_VERSION -> HAS_IRIS && HAS_YEAR_VERSION;
            case PAUSE_MENU_TEXT, TITLE_SIDE_BUTTONS -> HAS_PAUSE_MENU_ICONS;
            default -> true;
        };
    }

    public boolean enabled() {
        return enabled;
    }

    void set(boolean value) {
        if (enabled != value) {
            enabled = value;
            dirty = true;
        }
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

    static synchronized void applyChanges(Runnable edits) {
        boolean debugBefore = DEBUG.enabled();
        boolean versionBefore = VERSION.enabled();
        boolean formatBefore = usesMajorVersionFormat();
        edits.run();
        if (!dirty) return;
        saveIfDirty();
        var client = Minecraft.getInstance();
        if (debugBefore != DEBUG.enabled()) {
            var entries = client.debugEntries;
            if (entries.isUsingProfile(DebugScreenProfile.DEFAULT) && !DefaultDebugProfile.matches(entries)) {
                // Vanilla saves named profiles by name only. Preserve these exact entries on restart.
                ((DebugProfileAccessor) entries).oldmc$setProfile(null);
                entries.save();
            }
        }
        if (versionBefore != VERSION.enabled() || (formatBefore != usesMajorVersionFormat() && versionBefore)) client.updateTitle();
    }

    static synchronized void saveIfDirty() {
        if (!dirty) return;
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
                dirty = false;
            } finally {
                Files.deleteIfExists(tmp);
            }
        } catch (IOException e) {
            LoggerFactory.getLogger("OldMC").warn("Could not save {}", FILE, e);
        }
    }
}
