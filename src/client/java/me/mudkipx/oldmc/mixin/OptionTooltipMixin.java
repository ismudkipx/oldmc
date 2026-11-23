package me.mudkipx.oldmc.mixin;

import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Tooltip.class)
abstract class OptionTooltipMixin {
    @ModifyArg(method = "splitTooltip", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Font;split(Lnet/minecraft/network/chat/FormattedText;I)Ljava/util/List;"),
            index = 1)
    private static int oldmc$optionTooltipWidth(FormattedText text, int width) {
        if (text instanceof Component message && message.getContents() instanceof TranslatableContents translation) {
            String key = translation.getKey();
            if (key.equals("oldmc.resetOption") || (key.startsWith("oldmc.option.") && key.endsWith(".tooltip"))) {
                return Integer.MAX_VALUE;
            }
        }
        return width;
    }
}
