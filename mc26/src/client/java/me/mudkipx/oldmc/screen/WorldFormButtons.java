package me.mudkipx.oldmc.screen;

import net.minecraft.client.gui.components.CycleButton;

final class WorldFormButtons {
    private WorldFormButtons() {}

    static CycleButton.Builder<Boolean> onOffBuilder(boolean initialValue) {
        return CycleButton.onOffBuilder(initialValue);
    }
}
