package lych.soulcraft.extension.fire;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.datafixers.util.Pair;
import lych.soulcraft.SoulCraft;
import lych.soulcraft.tag.ModFluidTags;
import lych.soulcraft.util.FireBlockHelper;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public final class Fires {
    static final Marker FIRE_MARKER = MarkerManager.getMarker("Fires");
    static final BiMap<Block, Fire> FIRES = HashBiMap.create();
    static final BiMap<Integer, Fire> FIRE_IDS = HashBiMap.create();
    static int currentID = 1;
    public static final Fire NO_FIRE = Fire.noFire();
    public static final Fire FIRE = Fire.create(Blocks.FIRE, 1, 100);
    public static final Fire SOUL_FIRE = Fire.create(Blocks.SOUL_FIRE, Pair.of(FireBlockHelper.SOUL_FIRE_0, FireBlockHelper.SOUL_FIRE_1), ModFluidTags.SOUL_LAVA, 2, 99);

    public static Fire empty() {
        return NO_FIRE;
    }

    static int nextID() {
        return currentID++;
    }

    private Fires() {}

    public static void init() {
        SoulCraft.LOGGER.info(FIRE_MARKER, "Registering fires...");
    }
}
