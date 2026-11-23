package me.mudkipx.oldmc.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import me.mudkipx.oldmc.config.OldMCOptions;
import me.mudkipx.oldmc.debug.ClassicDebugLayout;
import me.mudkipx.oldmc.debug.DebugEntryLines;
import me.mudkipx.oldmc.debug.DebugEntryLookup;
import me.mudkipx.oldmc.debug.DebugItem;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(DebugScreenOverlay.class)
abstract class DebugScreenOverlayMixin {
    @WrapOperation(
        method = RenderTargets.DEBUG_RENDER,
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/debug/DebugScreenEntry;display("
            + "Lnet/minecraft/client/gui/components/debug/DebugScreenDisplayer;Lnet/minecraft/world/level/Level;"
            + "Lnet/minecraft/world/level/chunk/LevelChunk;Lnet/minecraft/world/level/chunk/LevelChunk;)V")
    )
    private void oldmc$collectEntry(DebugScreenEntry entry, DebugScreenDisplayer displayer, Level level,
            LevelChunk clientChunk, LevelChunk serverChunk, Operation<Void> original,
            @Share("layout") LocalRef<ClassicDebugLayout> frame) {
        DebugItem item = OldMCOptions.DEBUG.enabled() ? DebugEntryLookup.get(entry) : null;
        if (item != null) {
            if (frame.get() == null) {
                frame.set(new ClassicDebugLayout());
            }
            displayer = new DebugEntryLines(frame.get().collect(item));
        }
        original.call(entry, displayer, level, clientChunk, serverChunk);
    }

    @ModifyArgs(
        method = RenderTargets.DEBUG_RENDER,
        at = @At(value = "INVOKE", target = RenderTargets.DEBUG_LINES),
        require = 2, allow = 2
    )
    private void oldmc$positionColumn(Args draw, @Share("layout") LocalRef<ClassicDebugLayout> frame) {
        if (frame.get() != null) {
            // Both draw calls take graphics, lines, and alignment. 26.3 also passes the scaled width.
            draw.set(1, frame.get().column(draw.get(2), draw.get(1)));
        }
    }
}
