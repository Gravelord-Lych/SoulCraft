package lych.soulcraft.client.render.world.sky;

import lych.soulcraft.SoulCraft;
import lych.soulcraft.world.gen.dimension.ModDimensionNames;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ISkyRenderHandler;

import java.awt.Color;

public class ModSkyRenderers {
    private static final ResourceLocation LOCATION_SOUL_LAND = make(ModDimensionNames.SOUL_LAND);

    public static final ISkyRenderHandler ESV = new ESVSkyRenderer();
    public static final ISkyRenderHandler SOUL_LAND = new SimpleSkyRenderer(LOCATION_SOUL_LAND, new Color(0x244646));

    private static ResourceLocation make(String name) {
        return SoulCraft.prefixTex(String.format("environment/%s_sky.png", name));
    }
}
