package me.mudkipx.oldmc.mixin;

import me.mudkipx.oldmc.config.OldMCOptions;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(ReloadableResourceManager.class)
abstract class ClassicMapPackMixin {
    @Shadow @Final private PackType type;

    @ModifyVariable(method = "createReload", at = @At("HEAD"), argsOnly = true)
    private List<PackResources> oldmc$addClassicMaps(List<PackResources> packs) {
        if (type != PackType.CLIENT_RESOURCES || !OldMCOptions.CLASSIC_MAP_TEXTURES.available()
                || !OldMCOptions.CLASSIC_MAP_TEXTURES.enabled()) return packs;

        for (int i = 0; i < packs.size(); i++) {
            if (!packs.get(i).packId().equals("vanilla")) continue;
            var root = FabricLoader.getInstance().getModContainer("oldmc").orElseThrow()
                    .findPath("oldmc_classic_maps").orElseThrow();
            var info = new PackLocationInfo("oldmc:classic_maps", Component.literal("OldMC"), PackSource.BUILT_IN, Optional.empty());
            var result = new ArrayList<>(packs);
            // Place the old textures directly above vanilla so player packs still take precedence.
            result.add(i + 1, new PathPackResources(info, root));
            return result;
        }
        return packs;
    }
}
