package me.mudkipx.oldmc.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import me.mudkipx.oldmc.mixin.DebugProfileAccessor;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.SemanticVersion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.function.Function;

public enum OldMCOptions implements OptionCatalog.Setting {
    PERCENTAGE("percentage"), GRID("grid"), DIRT_BACKGROUND("dirtBackground"), DEBUG("debug"),
    SODIUM_F3_RIGHT("sodiumF3Right"), SODIUM_F3_VERSION("sodiumF3Version"),
    IMMEDIATELY_FAST_F3_RIGHT("immediatelyFastF3Right"), IMMEDIATELY_FAST_F3_VERSION("immediatelyFastF3Version"),
    IRIS_F3_RIGHT("irisF3Right"), IRIS_F3_VERSION("irisF3Version"),
    CLASSIC_BABY_MOBS("classicBabyMobs"), CLASSIC_BABY_SOUNDS("classicBabySounds"),
    CLASSIC_CREATE_WORLD("classicCreateWorld"),
    TITLE_SIDE_BUTTONS("titleSideButtons"), PAUSE_MENU_TEXT("pauseMenuText"), REMOVE_FEEDBACK("removeFeedback"),
    REMOVE_PLAYER_REPORTING("removePlayerReporting"), VERSION("version");

    private static final Logger LOGGER = LoggerFactory.getLogger("OldMC");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("oldmc.json");
    private static VersionFormat versionFormat = VersionFormat.DEFAULT;
    private static boolean dirty;
    private static boolean preserveUnreadConfig;
    private static final boolean HAS_SODIUM = FabricLoader.getInstance().isModLoaded("sodium");
    private static final boolean HAS_IMMEDIATELY_FAST = FabricLoader.getInstance().isModLoaded("immediatelyfast");
    private static final boolean HAS_IRIS = FabricLoader.getInstance().isModLoaded("iris");
    static final String GAME_VERSION;
    private static final boolean HAS_YEAR_VERSION;
    public static final boolean IS_26_2_OR_LATER;
    static {
        var version = FabricLoader.getInstance().getModContainer("minecraft").orElseThrow().getMetadata().getVersion();
        GAME_VERSION = version.getFriendlyString();
        HAS_YEAR_VERSION = GAME_VERSION.startsWith("26.");
        IS_26_2_OR_LATER = version instanceof SemanticVersion && isAtLeast26_2((SemanticVersion) version);
    }

    private static boolean isAtLeast26_2(SemanticVersion version) {
        int major = version.getVersionComponent(0);
        return major > 26 || major == 26 && version.getVersionComponent(1) >= 2;
    }

    private static FriendButtonPlacement friendPlacement = FriendButtonPlacement.DEFAULT;
    private static TitleFriendPlacement titleFriendPlacement = TitleFriendPlacement.DEFAULT;
    public final String key;
    public final boolean defaultValue = true;
    private boolean enabled = defaultValue;

    OldMCOptions(String key) {
        this.key = key;
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

    public static VersionFormat versionFormat() {
        return versionFormat;
    }

    static void setVersionFormat(VersionFormat value) {
        if (versionFormat != value) {
            versionFormat = value;
            dirty = true;
        }
    }

    String nameKey() {
        return "oldmc.option." + key;
    }

    @Override
    public boolean available() {
        return switch (this) {
            case SODIUM_F3_RIGHT -> HAS_SODIUM;
            case VERSION, CLASSIC_BABY_MOBS, CLASSIC_BABY_SOUNDS -> HAS_YEAR_VERSION;
            case SODIUM_F3_VERSION -> HAS_SODIUM && HAS_YEAR_VERSION;
            case IMMEDIATELY_FAST_F3_RIGHT -> HAS_IMMEDIATELY_FAST;
            case IRIS_F3_RIGHT -> HAS_IRIS;
            case IMMEDIATELY_FAST_F3_VERSION -> HAS_IMMEDIATELY_FAST && HAS_YEAR_VERSION;
            case IRIS_F3_VERSION -> HAS_IRIS && HAS_YEAR_VERSION;
            case PAUSE_MENU_TEXT, TITLE_SIDE_BUTTONS -> IS_26_2_OR_LATER;
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
        load();
    }

    private static void load() {
        try {
            JsonElement data = GSON.fromJson(Files.readString(FILE), JsonElement.class);
            if (data != null && !data.isJsonNull()) {
                if (!data.isJsonObject()) throw new JsonParseException("Expected a configuration object");
                var config = data.getAsJsonObject();
                applyConfig(config);
                if (dirty) save(config);
                return;
            }
            LOGGER.warn("[OldMC] Empty configuration file; reset to default values.");
        } catch (NoSuchFileException ignored) {
            // First launch: create the default file without a warning.
        } catch (CharacterCodingException | JsonParseException e) {
            LOGGER.warn("[OldMC] Failed to load configuration file; reset to default values. {}", e.getMessage());
        } catch (IOException e) {
            preserveUnreadConfig = true;
            LOGGER.warn("[OldMC] Could not read {}. Using defaults for this session; changes will not be saved.", FILE, e);
            return;
        }
        for (var option : values()) option.enabled = option.defaultValue;
        friendPlacement = FriendButtonPlacement.DEFAULT;
        titleFriendPlacement = TitleFriendPlacement.DEFAULT;
        versionFormat = VersionFormat.DEFAULT;
        dirty = true;
        saveIfDirty();
    }

    private static void applyConfig(JsonObject data) {
        friendPlacement = readChoice(data, "friendButtonPlacement",
                FriendButtonPlacement.DEFAULT.key(), FriendButtonPlacement::fromKey);
        titleFriendPlacement = readChoice(data, "titleFriendButtonPlacement",
                TitleFriendPlacement.DEFAULT.key(), TitleFriendPlacement::fromKey);
        if (data.has("versionFormat") && !data.get("versionFormat").isJsonNull()) {
            versionFormat = readChoice(data, "versionFormat", VersionFormat.DEFAULT.key, VersionFormat::fromKey);
        } else {
            boolean major = readBoolean(data, "majorVersionFormat", VersionFormat.DEFAULT == VersionFormat.MAJOR);
            versionFormat = major ? VersionFormat.MAJOR : VersionFormat.YEAR;
        }
        for (var option : values()) {
            option.enabled = readBoolean(data, option.key, option.defaultValue);
        }
    }

    private static boolean readBoolean(JsonObject data, String key, boolean fallback) {
        var value = data.get(key);
        if (value == null || value.isJsonNull()) return fallback;
        if (value.isJsonPrimitive()) {
            var primitive = value.getAsJsonPrimitive();
            if (primitive.isBoolean()) return primitive.getAsBoolean();
            // Previous versions also accepted the strings "true" and "false".
            if (primitive.isString()) {
                if ("true".equalsIgnoreCase(primitive.getAsString())) return true;
                if ("false".equalsIgnoreCase(primitive.getAsString())) return false;
            }
        }
        data.addProperty(key, fallback);
        markCorrected(key);
        return fallback;
    }

    private static <E extends Enum<E>> E readChoice(JsonObject data, String key, String defaultKey,
                                                    Function<String, E> fromKey) {
        var value = data.get(key);
        if (value != null && !value.isJsonNull()) {
            if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isString()) {
                E choice = fromKey.apply(value.getAsString());
                if (choice != null) return choice;
            }
            data.addProperty(key, defaultKey);
            markCorrected(key);
        }
        return fromKey.apply(defaultKey);
    }

    private static void markCorrected(String key) {
        LOGGER.warn("[OldMC] Invalid value for {}; using its default.", key);
        dirty = true;
    }

    static synchronized void applyChanges(Runnable edits) {
        boolean debugBefore = DEBUG.enabled();
        boolean versionBefore = VERSION.enabled();
        var formatBefore = versionFormat;
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
        if (versionBefore != VERSION.enabled() || (formatBefore != versionFormat && versionBefore)) client.updateTitle();
    }

    static synchronized void saveIfDirty() {
        if (!dirty || preserveUnreadConfig) return;
        var data = new JsonObject();
        data.addProperty("friendButtonPlacement", friendPlacement.key());
        data.addProperty("titleFriendButtonPlacement", titleFriendPlacement.key());
        data.addProperty("versionFormat", versionFormat.key);
        for (var option : values()) data.addProperty(option.key, option.enabled);
        save(data);
    }

    private static void save(JsonObject data) {
        try {
            Files.createDirectories(FILE.getParent());
            Path tmp = Files.createTempFile(FILE.getParent(), "oldmc-", ".tmp");
            try {
                Files.writeString(tmp, GSON.toJson(data) + "\n");
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
            LOGGER.warn("[OldMC] Failed to save configuration file: {}", FILE, e);
        }
    }
}
