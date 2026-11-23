package me.mudkipx.oldmc.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.mudkipx.oldmc.version.VersionLabels;
import net.minecraft.client.gui.screens.worldselection.WorldOpenFlows;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WorldOpenFlows.class)
abstract class WorldWarningVersionMixin {
    @ModifyExpressionValue(method = "openWorldCheckVersionCompatibility", at = @At(value = "INVOKE", target = VersionLabels.NAME))
    private String oldmc$versionLabel(String value) {
        return VersionLabels.display(value);
    }
}
