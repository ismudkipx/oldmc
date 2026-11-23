package me.mudkipx.oldmc.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.mudkipx.oldmc.version.VersionLabels;
import net.minecraft.world.level.storage.LevelSummary;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LevelSummary.class)
abstract class WorldListVersionMixin {
    @ModifyExpressionValue(method = "getWorldVersionName", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/LevelVersion;minecraftVersionName()Ljava/lang/String;", ordinal = 1))
    private String oldmc$versionLabel(String value) {
        return VersionLabels.display(value);
    }
}
