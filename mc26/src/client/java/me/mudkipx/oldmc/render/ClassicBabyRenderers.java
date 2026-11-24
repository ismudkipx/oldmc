package me.mudkipx.oldmc.render;

import me.mudkipx.oldmc.config.OldMCOptions;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import org.slf4j.LoggerFactory;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

public final class ClassicBabyRenderers {
    private static final Set<String> TYPES = Set.of("armadillo", "axolotl", "bee", "camel", "camel_husk",
            "cat", "chicken", "cow", "dolphin", "donkey", "drowned", "fox", "glow_squid", "goat",
            "hoglin", "horse", "husk", "llama", "mooshroom", "mule", "ocelot", "panda", "pig", "piglin",
            "polar_bear", "rabbit", "sheep", "skeleton_horse", "squid", "strider", "trader_llama",
            "turtle", "villager", "wolf", "zoglin", "zombie", "zombie_horse", "zombie_villager", "zombified_piglin");
    private static final Map<Identifier, Identifier> RABBIT_TEXTURES = Map.ofEntries(
            rabbitTexture("black"), rabbitTexture("brown"), rabbitTexture("caerbannog"), rabbitTexture("gold"),
            rabbitTexture("salt"), rabbitTexture("toast"), rabbitTexture("white"), rabbitTexture("white_splotched"));
    private static final ThreadLocal<Boolean> BAKING = new ThreadLocal<>();
    private static volatile Map<EntityRenderer<?, ?>, Entry> renderers = Map.of();

    private static final class Entry {
        final EntityRendererProvider<?> provider;
        final EntityRendererProvider.Context context;
        EntityRenderer<?, ?> renderer;

        Entry(EntityRendererProvider<?> provider, EntityRendererProvider.Context context) {
            this.provider = provider;
            this.context = context;
        }
    }

    public static void reload(EntityRendererProvider.Context context, Map<EntityType<?>, EntityRendererProvider<?>> providers,
                              Map<EntityType<?>, EntityRenderer<?, ?>> originals) {
        var entries = new IdentityHashMap<EntityRenderer<?, ?>, Entry>();
        originals.forEach((type, original) -> {
            Identifier id = BuiltInRegistries.ENTITY_TYPE.getKey(type);
            var provider = providers.get(type);
            if (id != null && id.getNamespace().equals("minecraft") && TYPES.contains(id.getPath())
                    && provider != null && original.getClass().getPackageName().equals("net.minecraft.client.renderer.entity")) {
                entries.put(original, new Entry(provider, context));
            }
        });
        renderers = entries;
    }

    public static boolean baking() {
        return BAKING.get() != null;
    }

    public static EntityRenderer<?, ?> select(EntityRenderer<?, ?> original, LivingEntityRenderState state) {
        if (!state.isBaby || !OldMCOptions.CLASSIC_BABY_MOBS.enabled()) return original;
        Entry entry = renderers.get(original);
        if (entry == null) return original;
        if (entry.renderer == null) {
            BAKING.set(true);
            try {
                entry.renderer = entry.provider.create(entry.context);
            } catch (RuntimeException e) {
                LoggerFactory.getLogger("OldMC").warn("Could not prepare classic baby appearance for {}", state.entityType, e);
                entry.renderer = original;
            } finally {
                BAKING.remove();
            }
        }
        return entry.renderer;
    }

    private static Map.Entry<Identifier, Identifier> rabbitTexture(String coat) {
        String file = coat + ".png";
        return Map.entry(Identifier.withDefaultNamespace("textures/entity/rabbit/rabbit_" + file),
                Identifier.fromNamespaceAndPath("oldmc", "textures/entity/rabbit/" + file));
    }

    public static Identifier texture(Identifier texture) {
        return RABBIT_TEXTURES.getOrDefault(texture, texture);
    }

    private ClassicBabyRenderers() {}
}
