package lych.soulcraft.network;

import lych.soulcraft.SoulCraft;
import lych.soulcraft.util.mixin.IPlayerEntityMixin;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.Objects;

public class StaticStatusHandler {
    public static SimpleChannel INSTANCE;
    public static final String VERSION = "1.0";
    private static int id = 0;

    public static int nextID() {
        return id++;
    }

    public static void register() {
        INSTANCE = NetworkRegistry.newSimpleChannel(SoulCraft.prefix("static_status_handler"), () -> VERSION, StaticStatusHandler::isCorrectVersion, StaticStatusHandler::isCorrectVersion);
        INSTANCE.messageBuilder(Boolean.class, nextID())
                .encoder((isStatic, buf) -> buf.writeBoolean(isStatic))
                .decoder(PacketBuffer::readBoolean)
                .consumer((isStatic, ctx) -> {
                    ((IPlayerEntityMixin) Objects.requireNonNull(ctx.get().getSender(), "Packets that are sent from a client to the server must have a sender")).setStatic(isStatic);
                })
                .add();
    }

    private static boolean isCorrectVersion(String version) {
        return version.equals(VERSION);
    }
}
