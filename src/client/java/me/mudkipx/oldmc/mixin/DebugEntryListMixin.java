package me.mudkipx.oldmc.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.mudkipx.oldmc.config.DefaultDebugProfile;
import me.mudkipx.oldmc.config.LayoutDebugProfile;
import net.minecraft.client.gui.components.debug.DebugScreenEntryList;
import net.minecraft.client.gui.components.debug.DebugScreenEntryStatus;
import net.minecraft.client.gui.components.debug.DebugScreenProfile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(DebugScreenEntryList.class)
abstract class DebugEntryListMixin implements LayoutDebugProfile {
    @Shadow private DebugScreenProfile profile;
    @Unique private boolean oldmc$matchesLayoutDefault;

    @Override
    public void oldmc$onLayoutChanged() {
        var entries = (DebugScreenEntryList) (Object) this;
        oldmc$matchesLayoutDefault = profile != DebugScreenProfile.PERFORMANCE && DefaultDebugProfile.matches(entries);
        if (profile == DebugScreenProfile.DEFAULT && !oldmc$matchesLayoutDefault) {
            // Vanilla saves named profiles by name only. Preserve the current entries on restart.
            profile = null;
            entries.save();
        }
    }

    @Override
    public boolean oldmc$matchesLayoutDefault() {
        return oldmc$matchesLayoutDefault;
    }

    @Inject(method = {"setStatus", "loadProfile", "load"}, at = @At("HEAD"))
    private void oldmc$clearLayoutMatch(CallbackInfo ci) {
        oldmc$matchesLayoutDefault = false;
    }

    // Resolve the preset at vanilla's lookup, including loads after the game has started.
    @ModifyExpressionValue(method = {"loadProfile", RenderTargets.RESET_PROFILE}, at = @At(value = "FIELD",
            target = "Lnet/minecraft/client/gui/components/debug/DebugScreenEntries;PROFILES:Ljava/util/Map;"),
            require = 1, allow = 2)
    private Map<DebugScreenProfile, Map<?, DebugScreenEntryStatus>> oldmc$resolveProfiles(
            Map<DebugScreenProfile, Map<?, DebugScreenEntryStatus>> profiles) {
        return DefaultDebugProfile.resolve(profiles);
    }
}
