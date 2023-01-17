package lych.soulcraft.extension.key;

import lych.soulcraft.SoulCraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

import java.util.UUID;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ModKeyInputs {
//  TODO - remove
    public static final KeyBinding DRAGON_WIZARD_KEY = createExtraAbilityKey(GLFW.GLFW_KEY_T, "dragon_wizard");
    public static final InvokableData DRAGON_WIZARD = new InvokableData(UUID.fromString("FEFFB414-DCF7-E7BB-878A-449A2D8F9740"), DRAGON_WIZARD_KEY);

    private ModKeyInputs() {}

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        InvokableManager.register(event, DRAGON_WIZARD, DragonWizardInvokable.INSTANCE);
    }

    private static KeyBinding createExtraAbilityKey(int key, String name) {
        return new KeyBinding(SoulCraft.prefixKeyMessage(name), KeyConflictContext.IN_GAME, KeyModifier.NONE, InputMappings.Type.KEYSYM, key, SoulCraft.prefixKeyCategory("exa"));
    }
}
