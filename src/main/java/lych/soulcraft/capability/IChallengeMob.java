package lych.soulcraft.capability;

import lych.soulcraft.world.event.challenge.Challenge;
import lych.soulcraft.world.event.challenge.ChallengeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

public interface IChallengeMob extends INBTSerializable<CompoundNBT> {
    @Nullable
    Challenge getChallenge();

    void setChallenge(Challenge challenge);

    @Nullable
    default Challenge getChallengeByType(ChallengeType<?> type) {
        return getChallenge() != null && getChallenge().getType() == type ? getChallenge() : null;
    }
}
