package lych.soulcraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import lych.soulcraft.SoulCraft;
import lych.soulcraft.api.exa.IExtraAbility;
import lych.soulcraft.extension.ExtraAbility;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class ExtraAbilityArgument implements ArgumentType<IExtraAbility> {
    private static final Collection<String> EXAMPLES = Arrays.asList("monster_sabotage", "enhanced_auto_jump");
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_EXA = new DynamicCommandExceptionType(exa -> new TranslationTextComponent(SoulCraft.prefixMsg("commands", "exa.exa_not_found"), exa));

    public static IExtraAbility getExA(CommandContext<CommandSource> context, String name) {
        return context.getArgument(name, IExtraAbility.class);
    }

    @Override
    public IExtraAbility parse(StringReader reader) throws CommandSyntaxException {
        ResourceLocation registryName = ResourceLocation.read(reader);
        return ExtraAbility.getOptional(registryName).orElseThrow(() -> ERROR_UNKNOWN_EXA.create(registryName));
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return ISuggestionProvider.suggestResource(ExtraAbility.getRegisteredExtraAbilities().keySet(), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
