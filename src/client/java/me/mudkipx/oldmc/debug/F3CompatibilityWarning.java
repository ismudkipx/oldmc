package me.mudkipx.oldmc.debug;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.transformer.meta.MixinMerged;

public final class F3CompatibilityWarning {
    private static final Logger LOGGER = LoggerFactory.getLogger("OldMC");
    private static boolean checkedMixins;
    private static boolean warned;

    private F3CompatibilityWarning() {}

    public static void check(Class<?> actualOverlay, Class<?> vanillaOverlay) {
        if (warned) return;
        if (actualOverlay == vanillaOverlay) {
            if (checkedMixins) return;
            checkedMixins = true;
            if (!hasExternalMixin(vanillaOverlay)) return;
        }
        warned = true;
        LOGGER.warn("[OldMC] Another mod has modified the F3 debug screen. OldMC’s “Classic F3 layout” option may not take effect. "
                + "If the F3 debug screen looks wrong or has been replaced by another mod, try disabling that mod.");
    }

    private static boolean hasExternalMixin(Class<?> overlay) {
        try {
            for (var method : overlay.getDeclaredMethods()) {
                var merged = method.getDeclaredAnnotation(MixinMerged.class);
                if (merged == null || method.isAnnotationPresent(Accessor.class) || method.isAnnotationPresent(Invoker.class)) continue;
                String origin = merged.mixin();
                if (!origin.startsWith("me.mudkipx.oldmc.") && !origin.startsWith("net.caffeinemc.mods.sodium.")
                        && !origin.startsWith("net.irisshaders.iris.") && !origin.startsWith("net.raphimc.immediatelyfast.")) {
                    return true;
                }
            }
        } catch (LinkageError e) {
            // An optional type in another mod's method signature may be unavailable.
            LOGGER.debug("Could not inspect F3 debug screen methods", e);
        }
        return false;
    }
}
