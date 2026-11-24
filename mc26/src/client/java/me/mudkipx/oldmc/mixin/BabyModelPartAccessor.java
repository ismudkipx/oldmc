package me.mudkipx.oldmc.mixin;

import java.util.Map;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ModelPart.class)
public interface BabyModelPartAccessor {
    @Accessor("children")
    Map<String, ModelPart> oldmc$children();
}
