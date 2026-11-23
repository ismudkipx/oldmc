package me.mudkipx.oldmc.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.mudkipx.oldmc.version.VersionLabels;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Minecraft.class)
abstract class WindowVersionMixin {
    @ModifyExpressionValue(method = "createTitle", at = @At(value = "INVOKE", target = VersionLabels.NAME))
    private String oldmc$versionLabel(String value) {
        return VersionLabels.display(value);
    }
}
