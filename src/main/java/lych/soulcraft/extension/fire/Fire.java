package lych.soulcraft.extension.fire;

import com.google.common.base.MoreObjects;
import com.mojang.datafixers.util.Pair;
import lych.soulcraft.SoulCraft;
import lych.soulcraft.util.Utils;
import lych.soulcraft.util.mixin.IAbstractFireBlockMixin;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class Fire {
    public static final Pair<RenderMaterial, RenderMaterial> DEFAULT_FIRE_OVERLAYS = Pair.of(ModelBakery.FIRE_0, ModelBakery.FIRE_1);
    public static final int DEFAULT_PRIORITY = 100;
    private static final Handler DUMMY_HANDLER = new Handler(){};
    @Nullable
    private final Pair<RenderMaterial, RenderMaterial> fireOverlays;
    private final Block fireBlock;
    @Nullable
    private final ITag<Fluid> lavaTag;
    private final Handler handler;
    private final float fireDamage;
    private final int priority;

    Fire(Block fireBlock, @Nullable Pair<RenderMaterial, RenderMaterial> fireOverlays, @Nullable ITag<Fluid> lavaTag, Handler handler, float fireDamage, int priority) {
        this.fireBlock = fireBlock;
        this.fireOverlays = fireOverlays;
        this.lavaTag = lavaTag;
        this.handler = handler;
        this.fireDamage = fireDamage;
        this.priority = priority;
    }

    public static Handler noHandlerNeeded() {
        return DUMMY_HANDLER;
    }

    static Fire noFire() {
        Fire noFire = new Fire(Blocks.AIR, null, null, noHandlerNeeded(), 0, Integer.MAX_VALUE);
        Fires.FIRES.put(noFire.getBlock(), noFire);
        Fires.FIRE_IDS.put(0, noFire);
        return noFire;
    }

    static Fire createAndRegister(FireProperties properties) {
        Fire fire = create(properties);
        register(fire);
        return fire;
    }

    public static Fire create(FireProperties properties) {
        return new Fire(properties.fireBlock, properties.fireOverlays, properties.lavaTag, properties.handler, properties.fireDamage, properties.priority);
    }

    public static void register(Fire fire) {
        Fire oldFire = registerFireType(fire.getBlock(), fire);
        if (oldFire == null) {
            return;
        }
        throw new UnsupportedOperationException(String.format("Fire block %s has already bound fire type!", fire.getBlock().getRegistryName()));
    }

    public static Fire autoCreateFireFor(AbstractFireBlock block) {
        return Fire.create(new FireProperties().setBlock(block).withDamage(((IAbstractFireBlockMixin) block).getFireDamage()));
    }

    public static List<Fire> getTrueFires() {
        return Fires.FIRES.values().stream().filter(Fire::isRealFire).sorted(Comparator.comparingInt(Fire::getPriority)).collect(Collectors.toList());
    }

    @Nullable
    private static Fire registerFireType(Block block, Fire fire) {
        Fire oldFire = Fires.FIRES.put(block, fire);
        if (oldFire == null) {
            Fires.FIRE_IDS.put(Fires.nextID(), fire);
        }
        return oldFire;
    }

    public static Fire empty() {
        return Fires.NO_FIRE;
    }

    public int getId() {
        return Fires.FIRE_IDS.inverse().get(this);
    }

    public void writeToNBT(CompoundNBT compoundNBT, String name) {
        compoundNBT.putString(name, Utils.getRegistryName(fireBlock, "FireBlock is not found").toString());
    }

    public static Fire fromNBT(CompoundNBT compoundNBT, String name) {
        String blockName = compoundNBT.getString(name);
        ResourceLocation location = new ResourceLocation(blockName);
        if (ForgeRegistries.BLOCKS.containsKey(location)) {
            Block block = ForgeRegistries.BLOCKS.getValue(location);
            if (block != null) {
                return byBlock(block);
            }
        }
        return warnAndUseDefault(String.format("No FireBlock named %s found, used default", location));
    }

    public static Fire byBlock(Block block) {
        Fire fire = Fires.FIRES.get(block);
        if (fire == null && block instanceof AbstractFireBlock) {
            AbstractFireBlock fireBlock = (AbstractFireBlock) block;
            fire = Fire.autoCreateFireFor(fireBlock);
        }
        if (fire == null) {
            return warnAndUseDefault(String.format("No fire matches FireBlock %s, used default", block.getRegistryName()));
        }
        return fire;
    }

    public static Fire byId(int id) {
        Fire fire = Fires.FIRE_IDS.get(id);
        if (fire == null) {
            return warnAndUseDefault(String.format("No fire with id %d found, used default", id));
        }
        return fire;
    }

    private static Fire warnAndUseDefault(String message) {
        SoulCraft.LOGGER.warn(Fires.FIRE_MARKER, message);
        return empty();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("fireBlock", fireBlock)
                .toString();
    }

    public boolean canSurviveOnBlock(IBlockReader reader, BlockPos firePos, BlockState state) {
        try {
            return handler.canSurviveOnBlock(reader, firePos, state, this);
        } catch (UnableToHandleException e) {
            return true;
        }
    }

    public BlockState getState(IBlockReader reader, BlockPos pos) {
        try {
            return handler.getState(reader, pos, this);
        } catch (UnableToHandleException e) {
            return getBlock().defaultBlockState();
        }
    }

    public Block getBlock() {
        return fireBlock;
    }

    public float getFireDamage(Entity entity, World world) {
        try {
            return handler.getFireDamage(entity, world, this);
        } catch (UnableToHandleException e) {
            return getDefaultFireDamage();
        }
    }

    public float getDefaultFireDamage() {
        return fireDamage;
    }

    public void entityInsideFire(BlockState fireBlockState, World world, BlockPos pos, Entity entity) {
        handler.entityInsideFire(fireBlockState, world, pos, entity, this);
    }

    public void entityOnFire(Entity entity) {
        handler.entityOnFire(entity, this);
    }

    public boolean isRealFire() {
        return this != Fires.NO_FIRE;
    }

    public int getPriority() {
        return priority;
    }

    public boolean canApplyTo(Entity entity) {
        try {
            return handler.canApplyTo(entity, this);
        } catch (UnableToHandleException e) {
            return true;
        }
    }

    public Fire applyTo(Entity entity) {
        try {
            return handler.applyTo(entity, this);
        } catch (UnableToHandleException e) {
            return this;
        }
    }

    public boolean canReplace(Fire oldFire) {
        return canReplace(this, oldFire);
    }

    public static boolean canReplace(Fire fire, Fire oldFire) {
        if (!fire.isRealFire() && oldFire.isRealFire()) {
            return true;
        }
        return fire.getPriority() <= oldFire.getPriority();
    }

    public Pair<RenderMaterial, RenderMaterial> getFireOverlays() {
        return Utils.getOrDefault(fireOverlays, DEFAULT_FIRE_OVERLAYS);
    }

    public ITag<Fluid> getLavaTag() {
        return Utils.getOrDefault(lavaTag, FluidTags.LAVA);
    }

    @SuppressWarnings("unused")
    public interface Handler {
        default boolean canSurviveOnBlock(IBlockReader reader, BlockPos firePos, BlockState state, Fire fire) throws UnableToHandleException {
            throw new UnableToHandleException();
        }

        default BlockState getState(IBlockReader reader, BlockPos pos, Fire fire) throws UnableToHandleException {
            throw new UnableToHandleException();
        }

        default float getFireDamage(Entity entity, World world, Fire fire) throws UnableToHandleException {
            throw new UnableToHandleException();
        }

        default boolean canApplyTo(Entity entity, Fire fire) throws UnableToHandleException {
            throw new UnableToHandleException();
        }

        default Fire applyTo(Entity entity, Fire fire) throws UnableToHandleException {
            throw new UnableToHandleException();
        }

        default void entityInsideFire(BlockState fireBlockState, World world, BlockPos pos, Entity entity, Fire fire) {}

        default void entityOnFire(Entity entity, Fire fire) {}
    }

    private static class UnableToHandleException extends Exception {}
}
