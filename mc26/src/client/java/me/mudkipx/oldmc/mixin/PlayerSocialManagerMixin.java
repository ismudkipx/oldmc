package me.mudkipx.oldmc.mixin;

import me.mudkipx.oldmc.gui.FriendListStatus;
import net.minecraft.client.gui.screens.social.PlayerSocialManager;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerSocialManager.class)
abstract class PlayerSocialManagerMixin implements FriendListStatus {}
