package me.mudkipx.oldmc.render;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import me.mudkipx.oldmc.mixin.BabyModelPartAccessor;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.core.Direction;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Mesh differences between the adult layer and the 1.21.11 baby layer, in model pixels. */
public final class ClassicBabyModels {
    private record PartEdit(float[] pose, List<JsonArray> boxes) {}
    private record Definitions(Map<String, Map<String, PartEdit>> models, Map<String, String> aliases) {}
    private static final Definitions DEFINITIONS;
    static {
        var stream = ClassicBabyModels.class.getResourceAsStream("/assets/oldmc/legacy/baby_models.json");
        try (var reader = new InputStreamReader(Objects.requireNonNull(stream), StandardCharsets.UTF_8)) {
            DEFINITIONS = new Gson().fromJson(reader, Definitions.class);
        } catch (java.io.IOException e) {
            throw new java.io.UncheckedIOException(e);
        }
    }

    public static ModelPart apply(ModelLayerLocation layer, ModelPart root) {
        String name = layer.toString();
        var edits = DEFINITIONS.models.get(DEFINITIONS.aliases.getOrDefault(name, name));
        if (edits == null) return root;
        // Parent paths precede children in the resource. Only new rabbit bones lack a native part.
        for (var entry : edits.entrySet()) {
            String path = entry.getKey();
            var edit = entry.getValue();
            var parent = root;
            String child = "";
            if (!path.isEmpty()) {
                String[] parts = path.split("/");
                for (int i = 0; i < parts.length - 1; i++) parent = parent.getChild(parts[i]);
                child = parts[parts.length - 1];
            }
            ModelPart part = child.isEmpty() ? root : (parent.hasChild(child) ? parent.getChild(child) : null);
            if (edit.boxes != null || part == null) {
                var children = part == null ? new HashMap<String, ModelPart>() : access(part).oldmc$children();
                var cubes = new ArrayList<ModelPart.Cube>();
                if (edit.boxes != null) {
                    for (var box : edit.boxes) cubes.add(cube(box));
                }
                var replacement = new ModelPart(cubes, children);
                if (part != null) replacement.setInitialPose(part.getInitialPose());
                part = replacement;
                if (child.isEmpty()) root = part;
                else access(parent).oldmc$children().put(child, part);
            }
            if (edit.pose != null) {
                float[] p = edit.pose;
                part.setInitialPose(new PartPose(p[0], p[1], p[2], p[3], p[4], p[5], p[6], p[7], p[8]));
            }
            part.resetPose();
        }
        return root;
    }

    private static BabyModelPartAccessor access(ModelPart part) {
        return (BabyModelPartAccessor)(Object) part;
    }

    private static ModelPart.Cube cube(JsonArray box) {
        // Texture origin, box origin, dimensions, inflation, mirror flag and texture size.
        return new ModelPart.Cube(box.get(0).getAsInt(), box.get(1).getAsInt(),
                box.get(2).getAsFloat(), box.get(3).getAsFloat(), box.get(4).getAsFloat(),
                box.get(5).getAsFloat(), box.get(6).getAsFloat(), box.get(7).getAsFloat(),
                box.get(8).getAsFloat(), box.get(9).getAsFloat(), box.get(10).getAsFloat(),
                box.get(11).getAsBoolean(), box.get(12).getAsFloat(), box.get(13).getAsFloat(),
                EnumSet.allOf(Direction.class));
    }

    private ClassicBabyModels() {}
}
