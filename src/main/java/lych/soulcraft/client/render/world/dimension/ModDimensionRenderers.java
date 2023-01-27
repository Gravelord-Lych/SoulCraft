package lych.soulcraft.client.render.world.dimension;

import lych.soulcraft.world.gen.dimension.ModDimensionNames;
import net.minecraft.client.world.DimensionRenderInfo;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static lych.soulcraft.SoulCraft.prefix;

@OnlyIn(Dist.CLIENT)
public class ModDimensionRenderers {
    public static void registerDimensionRenderers() {
        register(prefix(ModDimensionNames.SOUL_LAND), new SoulLandRenderInfo());
        register(prefix(ModDimensionNames.ESV), new ESVRenderInfo());
    }

    public static void register(ResourceLocation location, DimensionRenderInfo renderInfo) {
        DimensionRenderInfo.EFFECTS.put(location, renderInfo);
    }
}
