package lych.soulcraft.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import lych.soulcraft.SoulCraft;
import lych.soulcraft.api.exa.IExtraAbility;
import lych.soulcraft.command.argument.ExtraAbilityArgument;
import lych.soulcraft.util.mixin.IPlayerEntityMixin;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Collections;

import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;

public final class ExtraAbilityCommand {
    private ExtraAbilityCommand() {}

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(literal("exa").requires(source -> source.hasPermission(2))
                .then(literal("add")
                        .then(argument("player", EntityArgument.player())
                                .then(argument("exa", new ExtraAbilityArgument())
                                        .executes(context -> addExtraAbility(context, EntityArgument.getPlayer(context, "player"))))))
                .then(literal("clear")
                        .executes(context -> removeAllExtraAbilities(context, context.getSource().getPlayerOrException()))
                        .then(argument("player", EntityArgument.player())
                                .executes(context -> removeAllExtraAbilities(context, EntityArgument.getPlayer(context, "player")))
                                .then(argument("exa", new ExtraAbilityArgument())
                                        .executes(context -> removeExtraAbility(context, EntityArgument.getPlayer(context, "player")))))));
    }

    private static int removeAllExtraAbilities(CommandContext<CommandSource> context, ServerPlayerEntity player) {
        if (((IPlayerEntityMixin) player).getExtraAbilities().isEmpty()) {
            context.getSource().sendFailure(new TranslationTextComponent(SoulCraft.prefixMsg("commands", "exa.remove_all_failed")));
            return 0;
        }
        ((IPlayerEntityMixin) player).setExtraAbilities(Collections.emptySet());
        context.getSource().sendSuccess(new TranslationTextComponent(SoulCraft.prefixMsg("commands", "exa.remove_all"), player.getDisplayName()), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int addExtraAbility(CommandContext<CommandSource> context, ServerPlayerEntity player) {
        IExtraAbility exa = ExtraAbilityArgument.getExA(context, "exa");
        if (exa.addTo(player)) {
            context.getSource().sendSuccess(new TranslationTextComponent(SoulCraft.prefixMsg("commands", "exa.add_success"), exa.getDisplayName(), player.getDisplayName()), true);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(new TranslationTextComponent(SoulCraft.prefixMsg("commands", "exa.add_failed"), exa.getDisplayName(), player.getDisplayName()));
            return 0;
        }
    }

    private static int removeExtraAbility(CommandContext<CommandSource> context, ServerPlayerEntity player) {
        IExtraAbility exa = ExtraAbilityArgument.getExA(context, "exa");
        if (exa.removeFrom(player)) {
            context.getSource().sendSuccess(new TranslationTextComponent(SoulCraft.prefixMsg("commands", "exa.remove_success"), exa.getDisplayName(), player.getDisplayName()), true);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(new TranslationTextComponent(SoulCraft.prefixMsg("commands", "exa.remove_failed"), exa.getDisplayName(), player.getDisplayName()));
            return 0;
        }
    }
}
