package me.mudkipx.oldmc.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.mudkipx.oldmc.version.VersionLabels;
import com.mojang.realmsclient.RealmsMainScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RealmsMainScreen.class)
abstract class RealmsVersionMixin {
    @ModifyExpressionValue(method = "extractRenderState", at = @At(value = "INVOKE", target = VersionLabels.NAME))
    private String oldmc$versionLabel(String value) {
        return VersionLabels.display(value);
    }

    @ModifyExpressionValue(method = {"play", "upgradeRealmAndPlay"}, at = @At(value = "INVOKE", target = VersionLabels.NAME))
    private static String oldmc$dialogVersion(String value) {
        return VersionLabels.display(value);
    }
}
