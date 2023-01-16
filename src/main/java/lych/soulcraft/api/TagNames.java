package lych.soulcraft.api;

import net.minecraft.util.ResourceLocation;

public final class TagNames {
    /**
     * Entities inside will receive double damage. You'd better make fluids with this tag have {@link net.minecraft.tags.FluidTags#LAVA LAVA} tag.
     */
    public static final ResourceLocation SOUL_LAVA = new ResourceLocation(SoulCraftAPI.MOD_ID, "soul_lava");

    /**
     * Soul mobs are immune to some players' abilities.
     */
    public static final ResourceLocation SOUL_MOB = new ResourceLocation(SoulCraftAPI.MOD_ID, "soul_mob");

    /**
     * Bosses who are tiered should have this tag.
     */
    public static final ResourceLocation TIERED_BOSS = new ResourceLocation(SoulCraftAPI.MOD_ID, "tiered_boss");

    private TagNames() {}
}

