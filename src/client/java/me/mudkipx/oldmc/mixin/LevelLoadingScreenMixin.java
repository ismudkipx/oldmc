package me.mudkipx.oldmc.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.mudkipx.oldmc.config.OldMCOptions;
import me.mudkipx.oldmc.loading.LoadingFinish;
import org.spongepowered.asm.mixin.Unique;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.multiplayer.LevelLoadTracker;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.progress.ChunkLoadStatusView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(LevelLoadingScreen.class)
abstract class LevelLoadingScreenMixin {
    @Shadow private LevelLoadTracker loadTracker;

    @Unique private LoadingFinish oldmc$finish;
    @Unique private boolean oldmc$terrainFrame;
    @Unique private ChunkLoadStatusView oldmc$lastGrid;

    @Unique private static final long oldmc$clockOrigin = System.nanoTime();

    @Unique private LoadingFinish oldmc$finishState() {
        if (oldmc$finish == null) {
            oldmc$finish = new LoadingFinish();
        }
        return oldmc$finish;
    }

    @Unique private static long oldmc$now() {
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - oldmc$clockOrigin);
    }

    @ModifyExpressionValue(method = RenderTargets.SCREEN_RENDER, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/LevelLoadTracker;statusView()Lnet/minecraft/server/level/progress/ChunkLoadStatusView;"))
    private ChunkLoadStatusView oldmc$choosePhase(ChunkLoadStatusView chunks) {
        if (!OldMCOptions.TERRAIN.enabled()) {
            oldmc$terrainFrame = false;
            return chunks;
        }
        if (chunks != null) oldmc$lastGrid = chunks;
        boolean generated = chunks != null || loadTracker.hasProgress();
        oldmc$terrainFrame = oldmc$finishState().renderTerrain(generated,
                loadTracker.serverProgress() >= 1.0F || loadTracker.isLevelReady(), oldmc$now());
        return oldmc$terrainFrame ? null : oldmc$lastGrid;
    }

    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/LevelLoadTracker;isLevelReady()Z"))
    private boolean oldmc$finishPresentation(boolean ready) {
        return ready && (!OldMCOptions.TERRAIN.enabled() || oldmc$finishState().mayClose(oldmc$now()));
    }

    @ModifyArgs(
        method = RenderTargets.SCREEN_RENDER,
        at = @At(value = "INVOKE", target = RenderTargets.CENTERED_TEXT)
    )
    private void oldmc$showPercentage(Args draw) {
        if (oldmc$terrainFrame || !OldMCOptions.PERCENTAGE.enabled()) return;
        boolean terrainEnabled = OldMCOptions.TERRAIN.enabled();
        boolean full = terrainEnabled && oldmc$finishState().full();
        ChunkLoadStatusView chunks = terrainEnabled ? oldmc$lastGrid : loadTracker.statusView();
        if (chunks == null && !loadTracker.hasProgress() && !full) {
            // The separate receiving-terrain screen in 1.21.8 had only a title.
            return;
        }

        int percent = full ? 100 : Math.clamp((int) (loadTracker.serverProgress() * 100.0F), 0, 100);
        // Centered text arguments: font, component, center X, top Y, color.
        draw.set(1, Component.translatable("loading.progress", percent));
        if (chunks != null) {
            Font font = draw.get(0);
            int centerY = ((LevelLoadingScreen) (Object) this).height / 2;
            // With two-pixel cells, the diameter in chunks is half the grid height.
            int gridTop = centerY - (2 * chunks.radius() + 1);
            draw.set(3, gridTop - font.lineHeight - 2);
        }
    }

    @Inject(method = RenderTargets.PROGRESS_BAR, at = @At("HEAD"), cancellable = true)
    private void oldmc$hideProgressBar(CallbackInfo callback) {
        if (OldMCOptions.PERCENTAGE.enabled() || oldmc$terrainFrame) callback.cancel();
    }
}
