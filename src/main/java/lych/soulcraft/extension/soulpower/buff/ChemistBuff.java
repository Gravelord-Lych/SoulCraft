package lych.soulcraft.extension.soulpower.buff;

import lych.soulcraft.api.exa.PlayerBuff;
import lych.soulcraft.util.ExtraAbilityConstants;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum ChemistBuff implements PlayerBuff {
    INSTANCE;

    @Override
    public void startApplyingTo(PlayerEntity player, World world) {}

    @Override
    public void stopApplyingTo(PlayerEntity player, World world) {}

    @Override
    public void serverTick(ServerPlayerEntity player, ServerWorld world) {
        if (player.getRandom().nextInt(ExtraAbilityConstants.CHEMIST_RANDOM_INTERVAL) != 0) {
            return;
        }
        List<Effect> effects = new ArrayList<>(ExtraAbilityConstants.CHEMIST_AVAILABLE_EFFECTS);
        effects.removeIf(player::hasEffect);
        if (effects.isEmpty()) {
            return;
        }
        Collections.shuffle(effects, player.getRandom());
        effects.stream().findFirst().map(effect -> new EffectInstance(effect, ExtraAbilityConstants.CHEMIST_DURATION, ExtraAbilityConstants.CHEMIST_AMPLIFIER)).ifPresent(player::addEffect);
    }
}
