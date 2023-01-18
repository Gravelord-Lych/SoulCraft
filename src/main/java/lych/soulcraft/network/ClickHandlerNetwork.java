package lych.soulcraft.network;

import lych.soulcraft.SoulCraft;
import lych.soulcraft.listener.CommonEventListener;
import lych.soulcraft.util.Utils;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.Objects;

public class ClickHandlerNetwork {
    public static SimpleChannel INSTANCE;
    public static final String VERSION = "1.0";
    private static int id = 0;

    public static int nextID() {
        return id++;
    }

    public static void register() {
        INSTANCE = NetworkRegistry.newSimpleChannel(SoulCraft.prefix("clicks"), () -> VERSION, ClickHandlerNetwork::isCorrectVersion, ClickHandlerNetwork::isCorrectVersion);
        INSTANCE.messageBuilder(Object.class, nextID())
                .encoder((o, buf) -> {})
                .decoder(buf -> Utils.DUMMY)
                .consumer((key, ctx) -> {
                    CommonEventListener.handleEmptyClickServerside(Objects.requireNonNull(ctx.get().getSender(), "Packets that are sent from a client to the server must have a sender"));
                })
                .add();
    }

    private static boolean isCorrectVersion(String version) {
        return version.equals(VERSION);
    }
}
