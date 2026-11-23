package me.mudkipx.oldmc.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.mudkipx.oldmc.config.DefaultDebugProfile;
import net.minecraft.client.gui.components.debug.DebugScreenEntryList;
import net.minecraft.client.gui.components.debug.DebugScreenEntryStatus;
import net.minecraft.client.gui.components.debug.DebugScreenProfile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;

@Mixin(DebugScreenEntryList.class)
abstract class DebugEntryListMixin {
    // Resolve the preset at vanilla's lookup, including loads after the game has started.
    @ModifyExpressionValue(method = {"loadProfile", RenderTargets.RESET_PROFILE}, at = @At(value = "FIELD",
            target = "Lnet/minecraft/client/gui/components/debug/DebugScreenEntries;PROFILES:Ljava/util/Map;"),
            require = 1, allow = 2)
    private Map<DebugScreenProfile, Map<?, DebugScreenEntryStatus>> oldmc$resolveProfiles(
            Map<DebugScreenProfile, Map<?, DebugScreenEntryStatus>> profiles) {
        return DefaultDebugProfile.resolve(profiles);
    }
}
