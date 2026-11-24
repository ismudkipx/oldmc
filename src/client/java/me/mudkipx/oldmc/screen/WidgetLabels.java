package me.mudkipx.oldmc.screen;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;

public final class WidgetLabels {
    private WidgetLabels() {}

    /** Ignore styling, including vanilla's gray text on disabled buttons. */
    public static String key(Component message) {
        return message.getContents() instanceof TranslatableContents text ? text.getKey() : "";
    }

    static String optionKey(Component message) {
        if (message.getContents() instanceof TranslatableContents text && text.getKey().equals("options.generic_value")
                && text.getArgs().length > 0 && text.getArgs()[0] instanceof Component name) {
            return key(name);
        }
        return key(message);
    }
}
