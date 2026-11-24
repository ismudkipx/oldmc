package me.mudkipx.oldmc.mixin;

import me.mudkipx.oldmc.render.ClassicCoat;
import net.minecraft.client.renderer.entity.state.CatRenderState;
import net.minecraft.client.renderer.entity.state.WolfRenderState;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({CatRenderState.class, WolfRenderState.class})
abstract class BabyCoatStateMixin implements ClassicCoat {
    @Unique private Identifier oldmc$coat;

    public Identifier oldmc$classicCoat() {
        return oldmc$coat;
    }

    public void oldmc$setClassicCoat(Identifier texture) {
        oldmc$coat = texture;
    }
}
