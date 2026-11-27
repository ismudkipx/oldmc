package me.mudkipx.oldmc.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.mudkipx.oldmc.config.OldMCOptions;
import me.mudkipx.oldmc.sound.ClassicBabySounds;
import me.mudkipx.oldmc.sound.ClassicBiomeSounds;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractSoundInstance.class)
abstract class BabySoundMixin {
    @ModifyExpressionValue(method = {"resolve", "getOrResolve"}, at = @At(value = "FIELD",
            target = "Lnet/minecraft/client/resources/sounds/AbstractSoundInstance;identifier:Lnet/minecraft/resources/Identifier;"))
    private Identifier oldmc$resolveMobSound(Identifier original) {
        if (OldMCOptions.CLASSIC_BIOME_SOUNDS.enabled()) original = ClassicBiomeSounds.replacement(original);
        return OldMCOptions.CLASSIC_BABY_SOUNDS.enabled() ? ClassicBabySounds.replacement(original) : original;
    }
}
