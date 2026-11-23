package me.mudkipx.oldmc.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.mudkipx.oldmc.version.VersionLabels;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "net.irisshaders.iris.gui.debug.IrisDebugEntry", remap = false)
abstract class IrisDebugVersionMixin {
    @ModifyExpressionValue(method = "display", at = @At(value = "INVOKE",
            target = "Lnet/irisshaders/iris/Iris;getFormattedVersion()Ljava/lang/String;"), require = 0)
    private String oldmc$displayGameVersion(String modVersion) {
        return VersionLabels.irisDebugVersion(modVersion);
    }
}
