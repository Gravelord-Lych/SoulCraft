package lych.soulcraft.listener;

import lych.soulcraft.SoulCraft;
import lych.soulcraft.extension.key.InvokableManager;
import lych.soulcraft.network.InvokableNetwork;
import lych.soulcraft.network.RecentlyInputNetwork;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SoulCraft.MOD_ID, value = Dist.CLIENT)
public final class KeyInputHandler {
    private KeyInputHandler() {}

    @SubscribeEvent
    public static void onKeyboardInput(InputEvent.KeyInputEvent event) {
        InvokableManager.getKeyInvokables().keySet().stream()
                .filter(invokable -> invokable.getKey().isDown())
                .forEach(invokable -> InvokableNetwork.INSTANCE.sendToServer(new InvokableNetwork.KeyPacket(invokable.getUUID())));
        RecentlyInputNetwork.INSTANCE.sendToServer(event.getKey());
    }
}
