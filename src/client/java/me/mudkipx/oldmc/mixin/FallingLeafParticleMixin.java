package me.mudkipx.oldmc.mixin;

import me.mudkipx.oldmc.config.OldMCOptions;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ParticleEngine.class)
abstract class FallingLeafParticleMixin {
    @Inject(method = "createParticle", at = @At("HEAD"), cancellable = true)
    private void oldmc$hideFallingLeaves(ParticleOptions particle, double x, double y, double z,
                                         double xd, double yd, double zd, CallbackInfoReturnable<Particle> ci) {
        if (OldMCOptions.fallingLeafMode().hides(particle.getType())) {
            ci.setReturnValue(null);
        }
    }
}
