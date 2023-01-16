package lych.soulcraft.util;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("deprecation")
public final class SoulFireHelper {
    public static final RenderMaterial SOUL_FIRE_0 = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS, new ResourceLocation("block/soul_fire_0"));
    public static final RenderMaterial SOUL_FIRE_1 = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS, new ResourceLocation("block/soul_fire_0"));
    private static final Map<Block, Integer> SOUL_FIRES = new HashMap<>(ImmutableMap.of(Blocks.SOUL_FIRE, -1, Blocks.SOUL_CAMPFIRE, -1));

    private SoulFireHelper() {}

    public static boolean isSoulFire(Block block) {
        return SOUL_FIRES.containsKey(block);
    }

    public static ImmutableMap<Block, Integer> getSoulFires() {
        return ImmutableMap.copyOf(SOUL_FIRES);
    }

    public static void registerSoulFire(Block block, int secondsOnSoulFire) {
        SOUL_FIRES.put(block, secondsOnSoulFire);
    }
}
