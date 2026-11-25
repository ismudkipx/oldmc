package me.mudkipx.oldmc.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.mudkipx.oldmc.config.LayoutDebugProfile;
import net.minecraft.client.gui.components.debug.DebugScreenEntryList;
import net.minecraft.client.gui.components.debug.DebugScreenProfile;
import net.minecraft.client.gui.screens.debug.DebugOptionsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DebugOptionsScreen.class)
abstract class DebugOptionsScreenMixin {
    @WrapOperation(method = "addProfileButton", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/debug/DebugScreenEntryList;isUsingProfile(Lnet/minecraft/client/gui/components/debug/DebugScreenProfile;)Z"))
    private boolean oldmc$isApplied(DebugScreenEntryList entries, DebugScreenProfile profile, Operation<Boolean> original) {
        // Vanilla records the selected profile when the button is pressed.
        return original.call(entries, profile)
                || (profile == DebugScreenProfile.DEFAULT && ((LayoutDebugProfile) entries).oldmc$matchesLayoutDefault());
    }
}
