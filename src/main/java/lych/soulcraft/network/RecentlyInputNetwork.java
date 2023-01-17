package lych.soulcraft.network;

import lych.soulcraft.SoulCraft;
import lych.soulcraft.config.ConfigHelper;
import lych.soulcraft.extension.key.InvokableManager;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.Objects;

public class RecentlyInputNetwork {
    public static SimpleChannel INSTANCE;
    public static final String VERSION = "1.0";
    private static int id = 0;

    public static int nextID() {
        return id++;
    }

    public static void register() {
        INSTANCE = NetworkRegistry.newSimpleChannel(SoulCraft.prefix("recently_input"), () -> VERSION, RecentlyInputNetwork::isCorrectVersion, RecentlyInputNetwork::isCorrectVersion);
        INSTANCE.messageBuilder(Integer.class, nextID())
                .encoder((key, buf) -> buf.writeVarInt(key))
                .decoder(PacketBuffer::readVarInt)
                .consumer((key, ctx) -> {
                    ServerPlayerEntity sender = ctx.get().getSender();
                    Objects.requireNonNull(sender, "Packets that are sent from a client to the server must have a sender");
                    InvokableManager.setRecentlyPressed(sender, key);
                    InvokableManager.setRecentlyPressedTimestamp(sender, sender.tickCount + ConfigHelper.getRecentlyPressCheckInterval());
                })
                .add();
    }

    private static boolean isCorrectVersion(String version) {
        return version.equals(VERSION);
    }
}
