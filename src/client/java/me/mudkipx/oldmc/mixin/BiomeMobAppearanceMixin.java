package me.mudkipx.oldmc.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.mudkipx.oldmc.config.OldMCOptions;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.ChickenRenderState;
import net.minecraft.client.renderer.entity.state.CowRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.PigRenderState;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.TemperatureVariants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityRenderer.class)
abstract class BiomeMobAppearanceMixin {
    @ModifyReturnValue(method = "createRenderState(Lnet/minecraft/world/entity/Entity;F)Lnet/minecraft/client/renderer/entity/state/EntityRenderState;", at = @At("RETURN"))
    private EntityRenderState oldmc$useDefaultAppearance(EntityRenderState state, Entity entity, float partialTick) {
        if (!OldMCOptions.CLASSIC_BIOME_MOBS.enabled()) return state;
        if (state instanceof CowRenderState cow) {
            cow.variant = oldmc$temperate(entity.registryAccess().lookupOrThrow(Registries.COW_VARIANT), cow.variant);
        } else if (state instanceof PigRenderState pig) {
            pig.variant = oldmc$temperate(entity.registryAccess().lookupOrThrow(Registries.PIG_VARIANT), pig.variant);
        } else if (state instanceof ChickenRenderState chicken) {
            chicken.variant = oldmc$temperate(entity.registryAccess().lookupOrThrow(Registries.CHICKEN_VARIANT), chicken.variant);
        }
        return state;
    }

    @Unique
    private static <T> T oldmc$temperate(Registry<T> variants, T current) {
        if (current == null) return null;
        var id = variants.getKey(current);
        if (TemperatureVariants.WARM.equals(id) || TemperatureVariants.COLD.equals(id)) {
            return variants.get(TemperatureVariants.TEMPERATE).map(Holder::value).orElse(current);
        }
        return current;
    }
}
