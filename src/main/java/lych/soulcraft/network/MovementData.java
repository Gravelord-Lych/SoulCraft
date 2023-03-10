package lych.soulcraft.network;

import lych.soulcraft.extension.control.Controller;
import lych.soulcraft.extension.control.MindOperator;
import lych.soulcraft.extension.control.MindOperatorSynchronizer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.MovementInput;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class MovementData {
    private final int mob;
    public final float leftImpulse;
    public final float forwardImpulse;
    public final boolean up;
    public final boolean down;
    public final boolean left;
    public final boolean right;
    public final boolean jumping;
    public final boolean shiftKeyDown;

    public MovementData(int mob, MovementInput input) {
        this(mob,
                input.leftImpulse,
                input.forwardImpulse,
                input.up,
                input.down,
                input.left,
                input.right,
                input.jumping,
                input.shiftKeyDown);
    }

    public MovementData(int mob, float leftImpulse, float forwardImpulse, boolean up, boolean down, boolean left, boolean right, boolean jumping, boolean shiftKeyDown) {
        this.mob = mob;
        this.leftImpulse = leftImpulse;
        this.forwardImpulse = forwardImpulse;
        this.up = up;
        this.down = down;
        this.left = left;
        this.right = right;
        this.jumping = jumping;
        this.shiftKeyDown = shiftKeyDown;
    }

    public MovementData(PacketBuffer buffer) {
        this(buffer.readVarInt(),
                buffer.readFloat(),
                buffer.readFloat(),
                buffer.readBoolean(),
                buffer.readBoolean(),
                buffer.readBoolean(),
                buffer.readBoolean(),
                buffer.readBoolean(),
                buffer.readBoolean());
    }

    @Nullable
    public MobEntity getMob(World world) {
        Entity entity = world.getEntity(mob);
        return entity instanceof MobEntity ? (MobEntity) entity : null;
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeVarInt(mob);
        buf.writeFloat(leftImpulse);
        buf.writeFloat(forwardImpulse);
        buf.writeBoolean(up);
        buf.writeBoolean(down);
        buf.writeBoolean(left);
        buf.writeBoolean(right);
        buf.writeBoolean(jumping);
        buf.writeBoolean(shiftKeyDown);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void handler(Supplier<NetworkEvent.Context> ctx) {
        ServerPlayerEntity sender = ctx.get().getSender();
        MobEntity mob = getMob(sender.getLevel());
        Controller<?> controller = MindOperatorSynchronizer.getActiveController(sender.getLevel(), mob);
        if (controller == null) {
            ctx.get().setPacketHandled(true);
            return;
        }
        if (!(controller instanceof MindOperator)) {
            throw new AssertionError();
        }
        ctx.get().enqueueWork(() -> MindOperatorSynchronizer.handleMovementS(mob, sender, (MindOperator) controller, this));
        ctx.get().setPacketHandled(true);
    }
}
