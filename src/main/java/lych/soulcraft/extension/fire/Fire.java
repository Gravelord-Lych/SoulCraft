package lych.soulcraft.extension.fire;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
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
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class Fire {
    public static final Pair<RenderMaterial, RenderMaterial> DEFAULT_FIRE_OVERLAYS = Pair.of(ModelBakery.FIRE_0, ModelBakery.FIRE_1);
    public static final int DEFAULT_PRIORITY = 100;
    private static final int DEFAULT_SECONDS_ON_FIRE = 8;
    @Nullable
    private final Pair<RenderMaterial, RenderMaterial> fireOverlays;
    private final Block fireBlock;
    @Nullable
    private final ITag<Fluid> lavaTag;
    private final float fireDamage;
    private final int priority;

    private Fire(Block fireBlock, @Nullable Pair<RenderMaterial, RenderMaterial> fireOverlays, @Nullable ITag<Fluid> lavaTag, float fireDamage, int priority) {
        this.fireBlock = fireBlock;
        this.fireOverlays = fireOverlays;
        this.lavaTag = lavaTag;
        this.fireDamage = fireDamage;
        this.priority = priority;
    }

    static Fire noFire() {
        Fire noFire = new Fire(Blocks.AIR, null, null, 0, Integer.MAX_VALUE);
        Fires.FIRES.put(noFire.getBlock(), noFire);
        Fires.FIRE_IDS.put(0, noFire);
        return noFire;
    }

    public static Fire create(Block fireBlock, float fireDamage, int priority) {
        return create(fireBlock, DEFAULT_FIRE_OVERLAYS, FluidTags.LAVA, fireDamage, priority);
    }

    public static Fire create(Block fireBlock, Pair<RenderMaterial, RenderMaterial> fireOverlay, ITag<Fluid> tag, float fireDamage, int priority) {
        Fire fire = new Fire(fireBlock, fireOverlay, tag, fireDamage, priority);
        Fire oldFire = registerFireType(fireBlock, fire);
        if (oldFire == null) {
            return fire;
        }
        throw new UnsupportedOperationException(String.format("Fire block %s has already bound fire type!", fireBlock.getRegistryName()));
    }

    public static Fire autoCreateFireFor(AbstractFireBlock block) {
        return create(block, ((IAbstractFireBlockMixin) block).getFireDamage(), DEFAULT_PRIORITY);
    }

    public static ImmutableMap<Block, Fire> getFireMap() {
        return ImmutableMap.copyOf(Fires.FIRES);
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

    public void writeToNetwork(PacketBuffer buffer) {
        buffer.writeVarInt(getId());
    }

    public static Fire fromNetwork(PacketBuffer buffer) {
        int id = buffer.readVarInt();
        return byId(id);
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
        return Fires.empty();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("fireBlock", fireBlock)
                .toString();
    }

    public void onFire(Entity entity) {
        entity.setSecondsOnFire(DEFAULT_SECONDS_ON_FIRE);
    }

    public Block getBlock() {
        return fireBlock;
    }

    public float getFireDamage(Entity entity, World world) {
        return fireDamage;
    }

    public void entityInsideFire(BlockState fireBlockState, World world, BlockPos pos, Entity entity) {}

    public void entityOnFire(Entity entity) {}

    public boolean isRealFire() {
        return this != Fires.NO_FIRE;
    }

    public int getPriority() {
        return priority;
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
        if (isRealFire()) {
            return Utils.getOrDefault(fireOverlays, DEFAULT_FIRE_OVERLAYS);
        }
        throw new UnsupportedOperationException("Non-real fire cannot have overlays");
    }

    public ITag<Fluid> getLavaTag() {
        if (isRealFire()) {
            return Utils.getOrDefault(lavaTag, FluidTags.LAVA);
        }
        throw new UnsupportedOperationException("Non-real fire cannot have lavas");
    }
}
