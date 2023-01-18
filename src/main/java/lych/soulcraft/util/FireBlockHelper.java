package lych.soulcraft.util;

import com.google.common.collect.ImmutableMap;
import lych.soulcraft.extension.fire.Fire;
import lych.soulcraft.extension.fire.Fires;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.LazyValue;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("deprecation")
public final class FireBlockHelper {
    public static final RenderMaterial SOUL_FIRE_0 = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS, new ResourceLocation("block/soul_fire_0"));
    public static final RenderMaterial SOUL_FIRE_1 = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS, new ResourceLocation("block/soul_fire_1"));
    private static final LazyValue<Map<Block, Fire>> FIRES = new LazyValue<>(() -> new HashMap<>(ImmutableMap.of(Blocks.SOUL_FIRE, Fires.SOUL_FIRE, Blocks.SOUL_CAMPFIRE, Fires.SOUL_FIRE)));

    private FireBlockHelper() {}

    public static boolean isSoulFire(Block block) {
        return FIRES.get().containsKey(block);
    }

    public static ImmutableMap<Block, Fire> getFireBlocks() {
        return ImmutableMap.copyOf(FIRES.get());
    }

    public static void bindFireType(Block block, Fire fire) {
        FIRES.get().put(block, fire);
    }
}
