package me.mudkipx.oldmc.mixin;

import net.minecraft.client.gui.components.debug.DebugScreenEntryList;
import net.minecraft.client.gui.components.debug.DebugScreenProfile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DebugScreenEntryList.class)
public interface DebugProfileAccessor {
    @Accessor("profile")
    void oldmc$setProfile(DebugScreenProfile profile);
}
