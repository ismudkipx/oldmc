package me.mudkipx.oldmc.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.realmsclient.gui.screens.RealmsClientOutdatedScreen;
import me.mudkipx.oldmc.version.VersionLabels;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RealmsClientOutdatedScreen.class)
abstract class RealmsOutdatedVersionMixin {
    @ModifyReturnValue(method = "getErrorMessage", at = @At("RETURN"))
    private Component oldmc$dialogVersion(Component message) {
        String version = SharedConstants.getCurrentVersion().name();
        String label = VersionLabels.display(version);
        if (label.equals(version) || !(message.getContents() instanceof TranslatableContents text)
                || text.getArgs().length != 1 || !(text.getArgs()[0] instanceof Component argument)) return message;
        return Component.translatable(text.getKey(), Component.literal(label).withStyle(argument.getStyle()))
                .withStyle(message.getStyle());
    }
}
