package lych.soulcraft.command;

import com.mojang.brigadier.CommandDispatcher;
import lych.soulcraft.SoulCraft;
import net.minecraft.command.CommandSource;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SoulCraft.MOD_ID)
public final class ModCommands {
    private ModCommands() {}

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSource> dispatcher = event.getDispatcher();
        ExtraAbilityCommand.register(dispatcher);
        ReinforcementCommand.register(dispatcher);
        ResetBossCommand.register(dispatcher);
        SoulEnergyCommand.register(dispatcher);
    }
}
