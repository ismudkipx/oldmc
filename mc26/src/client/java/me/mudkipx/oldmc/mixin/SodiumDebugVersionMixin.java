package me.mudkipx.oldmc.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.mudkipx.oldmc.version.VersionLabels;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "net.caffeinemc.mods.sodium.client.gui.SodiumDebugEntry", remap = false)
abstract class SodiumDebugVersionMixin {
    @ModifyExpressionValue(method = "display", at = @At(value = "INVOKE",
            target = "Lnet/caffeinemc/mods/sodium/client/SodiumClientMod;getVersion()Ljava/lang/String;"), require = 0)
    private String oldmc$displayGameVersion(String sodiumVersion) {
        return VersionLabels.sodiumDebugVersion(sodiumVersion);
    }
}
