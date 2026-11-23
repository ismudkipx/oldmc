package me.mudkipx.oldmc.mixin;

import me.mudkipx.oldmc.config.OldMCOptions;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Minecraft.class)
abstract class LoadingGridMixin {
    @ModifyArg(method = "doWorldLoad", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/server/IntegratedServer;createChunkLoadStatusView(I)Lnet/minecraft/server/level/progress/ChunkLoadStatusView;"), index = 0)
    private int oldmc$gridRadius(int radius) {
        // 1.21.8 default: spawnChunkRadius (2) + 1 + full-chunk dependency radius (11).
        return OldMCOptions.GRID.enabled() ? 14 : radius;
    }
}
