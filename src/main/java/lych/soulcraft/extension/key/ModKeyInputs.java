package lych.soulcraft.extension.key;

import lych.soulcraft.SoulCraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

import java.util.UUID;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ModKeyInputs {
//  TODO - remove
    private static final KeyBinding TEST_KEY = new KeyBinding(SoulCraft.prefixKeyMessage("test"),
            KeyConflictContext.IN_GAME,
            KeyModifier.NONE,
            InputMappings.Type.KEYSYM,
            GLFW.GLFW_KEY_J,
            SoulCraft.prefixKeyCategory("test"));
    public static final InvokableData TEST = new InvokableData(UUID.fromString("FEFFB414-DCF7-E7BB-878A-449A2D8F9740"), TEST_KEY);

    private ModKeyInputs() {}

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        InvokableManager.register(event, TEST, player -> player.heal(5));
    }
}
