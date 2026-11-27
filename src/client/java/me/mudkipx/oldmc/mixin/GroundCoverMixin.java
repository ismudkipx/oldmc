package me.mudkipx.oldmc.mixin;

import me.mudkipx.oldmc.config.OldMCOptions;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerBedBlock;
import net.minecraft.world.level.block.LeafLitterBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({FlowerBedBlock.class, LeafLitterBlock.class})
abstract class GroundCoverMixin extends VegetationBlock {
    protected GroundCoverMixin(Properties properties) {
        super(properties);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        if ((state.is(Blocks.WILDFLOWERS) || state.is(Blocks.LEAF_LITTER))
                && OldMCOptions.HIDE_WILDFLOWERS_AND_LEAF_LITTER.enabled()) {
            return RenderShape.INVISIBLE;
        }
        return super.getRenderShape(state);
    }
}
