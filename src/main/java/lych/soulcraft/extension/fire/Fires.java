package lych.soulcraft.extension.fire;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import lych.soulcraft.SoulCraft;
import lych.soulcraft.api.event.RegisterFiresEvent;
import lych.soulcraft.tag.ModFluidTags;
import lych.soulcraft.util.FireBlockHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoulFireBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public final class Fires {
    static final Marker FIRE_MARKER = MarkerManager.getMarker("Fires");
    static final BiMap<Block, Fire> FIRES = HashBiMap.create();
    static final BiMap<Integer, Fire> FIRE_IDS = HashBiMap.create();
    static int currentID = 1;
    static final Fire NO_FIRE = Fire.noFire();
    public static final Fire FIRE = Fire.createAndRegister(new FireProperties().setBlock(Blocks.FIRE).withDamage(1).withPriority(Fire.DEFAULT_PRIORITY));
    public static final Fire SOUL_FIRE = Fire.createAndRegister(new FireProperties().setBlock(Blocks.SOUL_FIRE).useOverlays(FireBlockHelper.SOUL_FIRE_0, FireBlockHelper.SOUL_FIRE_1).handler(SoulFireHandler.INSTANCE).withLava(ModFluidTags.SOUL_LAVA).withDamage(2).withPriority(66));

    static {
        onRegisteringFire();
    }

    static int nextID() {
        return currentID++;
    }

    private Fires() {}

    public static void init() {
        SoulCraft.LOGGER.info(FIRE_MARKER, "Registering fires...");
    }

    private static void onRegisteringFire() {
        FMLJavaModLoadingContext.get().getModEventBus().post(new RegisterFiresEvent());
    }

    public enum SoulFireHandler implements Fire.Handler {
        INSTANCE;

        @Override
        public boolean canSurviveOnBlock(IBlockReader reader, BlockPos firePos, BlockState state, Fire fire) {
            return SoulFireBlock.canSurviveOnBlock(state.getBlock());
        }
    }
}
