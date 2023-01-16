package lych.soulcraft.capability;

import lych.soulcraft.world.event.challenge.Challenge;
import lych.soulcraft.world.event.challenge.ChallengeType;
import lych.soulcraft.world.event.manager.ChallengeManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;

public class ChallengeMob implements IChallengeMob {
    private final ServerWorld level;
    @Nullable
    private Challenge challenge;

    public ChallengeMob(ServerWorld level) {
        this.level = level;
    }

    @Nullable
    @Override
    public Challenge getChallenge() {
        return challenge;
    }

    @Override
    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT compoundNBT = new CompoundNBT();
        if (challenge != null) {
            compoundNBT.putString("ChallengeType", challenge.getType().getRegistryName().toString());
            compoundNBT.putInt("ChallengeId", challenge.getId());
        }
        return compoundNBT;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if (nbt.contains("ChallengeType") && nbt.contains("ChallengeId")) {
            ChallengeType<?> type = ChallengeType.getOptional(new ResourceLocation(nbt.getString("ChallengeType"))).orElseThrow(() -> new IllegalStateException(String.format("Challenge %s was not found", nbt.getString("ChallengeType"))));
            challenge = ChallengeManager.byType(type, level).get(nbt.getInt("ChallengeId"));
        }
    }
}
