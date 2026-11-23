package me.mudkipx.oldmc.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.mudkipx.oldmc.version.VersionLabels;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.client.gui.components.debug.DebugEntryVersion")
abstract class DebugVersionMixin {
    @ModifyExpressionValue(method = "display", at = @At(value = "INVOKE", target = VersionLabels.NAME))
    private String oldmc$versionLabel(String value) {
        return VersionLabels.display(value);
    }

    @ModifyExpressionValue(method = "display", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/Minecraft;getLaunchedVersion()Ljava/lang/String;"))
    private String oldmc$launchLabel(String value) {
        return VersionLabels.launchProfile(value);
    }
}
