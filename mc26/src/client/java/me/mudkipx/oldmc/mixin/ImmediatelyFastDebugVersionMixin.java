package me.mudkipx.oldmc.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.mudkipx.oldmc.version.VersionLabels;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "net.raphimc.immediatelyfast.feature.core.ImmediatelyFastDebugScreenEntry", remap = false)
abstract class ImmediatelyFastDebugVersionMixin {
    @ModifyExpressionValue(method = "display", at = @At(value = "FIELD",
            target = "Lnet/raphimc/immediatelyfast/ImmediatelyFast;VERSION:Ljava/lang/String;"), require = 0)
    private String oldmc$displayGameVersion(String modVersion) {
        return VersionLabels.immediatelyFastDebugVersion(modVersion);
    }
}
