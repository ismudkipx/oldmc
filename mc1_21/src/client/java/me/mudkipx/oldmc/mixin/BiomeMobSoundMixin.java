package me.mudkipx.oldmc.mixin;

import me.mudkipx.oldmc.config.OldMCOptions;
import me.mudkipx.oldmc.sound.ClassicWolfSounds;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Map;

@Mixin(AbstractSoundInstance.class)
abstract class BiomeMobSoundMixin {
    @Unique private static final Map<ResourceLocation, ResourceLocation> oldmc$wolfSounds =
            ClassicWolfSounds.replacements(ResourceLocation::withDefaultNamespace);

    @ModifyArg(method = "resolve", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sounds/SoundManager;getSoundEvent(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/sounds/WeighedSoundEvents;"), index = 0)
    private ResourceLocation oldmc$classicWolfSound(ResourceLocation original) {
        return OldMCOptions.CLASSIC_BIOME_SOUNDS.enabled() ? oldmc$wolfSounds.getOrDefault(original, original) : original;
    }
}
