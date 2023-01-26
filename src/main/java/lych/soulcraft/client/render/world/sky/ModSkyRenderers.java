package lych.soulcraft.client.render.world.sky;

import lych.soulcraft.SoulCraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ISkyRenderHandler;

public class ModSkyRenderers {
    public static final ISkyRenderHandler ESV = new ESVSkyRenderer();

    private static ResourceLocation make(String name) {
        return SoulCraft.prefixTex(String.format("environment/%s_sky.png", name));
    }
}
