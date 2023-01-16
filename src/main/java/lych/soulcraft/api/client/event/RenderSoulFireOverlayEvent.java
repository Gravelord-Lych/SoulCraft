package lych.soulcraft.api.client.event;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.Nullable;

import static net.minecraftforge.eventbus.api.Event.*;

/**
 * Called when Soul Craft Mod used its renderer to render a fire that is soul fire.<br>
 * <br>
 * This event is not {@link Cancelable}.<br>
 * <br>
 * This event has a result {@link HasResult}. <strong>But the results here have different meanings.</strong>
 * <li>{@link Result#ALLOW} will <strong>use vanilla renderer</strong>.</li>
 * <li>{@link Result#DEFAULT} will <strong>use SoulCraft's renderer</strong>.</li>
 * <li>{@link Result#DENY} <strong>will not use any renderer</strong>.</li><br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 */
@HasResult
public class RenderSoulFireOverlayEvent extends Event {
    private final Entity entity;
    private final MatrixStack stack;
    private final Type type;
    @Nullable
    private final IRenderTypeBuffer buffer;
    @Nullable
    private final BlockState state;
    @Nullable
    private final BlockPos blockPos;

    public RenderSoulFireOverlayEvent(Entity entity, MatrixStack stack, Type type, @Nullable IRenderTypeBuffer buffer, @Nullable BlockState state, @Nullable BlockPos pos) {
        this.entity = entity;
        this.stack = stack;
        this.type = type;
        this.buffer = buffer;
        this.state = state;
        this.blockPos = pos;
    }

    /**
     * The player which the overlay will apply to. If the <code>type</code> is not {@link Type#PLAYER}, an <code>UnsupportedOperationException</code> will be thrown.
     */
    public PlayerEntity getPlayer() {
        if (getType() != Type.PLAYER) {
            throw new UnsupportedOperationException();
        }
        return (PlayerEntity) entity;
    }

    public Entity getEntity() {
        return entity;
    }

    public MatrixStack getMatrixStack() {
        return stack;
    }

    public Type getType() {
        return type;
    }

    /**
     * @return Null if the <code>type</code> is {@link Type#ENTITY}
     */
    @Nullable
    public BlockState getState() {
        return state;
    }

    /**
     * @return Null if the <code>type</code> is {@link Type#ENTITY}
     */
    @Nullable
    public BlockPos getBlockPos() {
        return blockPos;
    }

    /**
     * @return Null if the <code>type</code> is {@link Type#PLAYER}
     */
    @Nullable
    public IRenderTypeBuffer getBuffer() {
        return buffer;
    }

    public enum Type {
//      Render soul fire on entities.
        ENTITY,
//      Render soul fire on a player who is using FirstPersonRenderer.
        PLAYER
    }
}
