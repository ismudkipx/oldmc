package me.mudkipx.oldmc.mixin;

import me.mudkipx.oldmc.gui.PauseMenuFriends;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;

@Pseudo
@Mixin(targets = "net.minecraft.client.gui.components.FriendsButton", remap = false)
abstract class FriendsButtonMixin implements PauseMenuFriends {
    @Shadow private int incomingRequestCount;

    @Override
    public int oldmc$incomingRequests() {
        return incomingRequestCount;
    }
}
