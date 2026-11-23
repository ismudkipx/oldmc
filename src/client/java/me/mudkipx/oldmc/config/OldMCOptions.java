package me.mudkipx.oldmc.config;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public enum OldMCOptions {
    PERCENTAGE("percentage"), TERRAIN("terrain"), GRID("grid"), DIRT_BACKGROUND("dirtBackground"), DEBUG("debug"), VERSION("version");

    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("oldmc.json");
    private static boolean majorVersionFormat;
    public final String key;
    private boolean enabled = true;

    OldMCOptions(String key) {
        this.key = key;
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
