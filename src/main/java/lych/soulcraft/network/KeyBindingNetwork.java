package lych.soulcraft.network;

import lych.soulcraft.SoulCraft;
import lych.soulcraft.extension.key.InvokableManager;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

public class KeyBindingNetwork {
    public static SimpleChannel INSTANCE;
    public static final String VERSION = "1.0";
    private static int id = 0;

    public static int nextID() {
        return id++;
    }

    public static void register() {
        INSTANCE = NetworkRegistry.newSimpleChannel(SoulCraft.prefix("key_binding"), () -> VERSION, KeyBindingNetwork::isCorrectVersion, KeyBindingNetwork::isCorrectVersion);
        INSTANCE.messageBuilder(KeyPacket.class, nextID())
                .encoder(KeyPacket::toBytes)
                .decoder(KeyPacket::new)
                .consumer(KeyPacket::handler)
                .add();
    }

    private static boolean isCorrectVersion(String version) {
        return version.equals(VERSION);
    }

    public static class KeyPacket {
        private final UUID invokableUUID;

        public KeyPacket(PacketBuffer buffer) {
            invokableUUID = buffer.readUUID();
        }

        public KeyPacket(UUID invokableUUID) {
            this.invokableUUID = invokableUUID;
        }

        public void toBytes(PacketBuffer buf) {
            buf.writeUUID(invokableUUID);
        }

        public void handler(Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> InvokableManager
                    .get(invokableUUID)
                    .onKeyPressed(Objects.requireNonNull(ctx.get().getSender(), "Packets that are sent from a client to the server must have a sender")));
            ctx.get().setPacketHandled(true);
        }
    }
}
