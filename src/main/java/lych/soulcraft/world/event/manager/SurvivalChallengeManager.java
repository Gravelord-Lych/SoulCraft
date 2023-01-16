package lych.soulcraft.world.event.manager;

import lych.soulcraft.world.event.challenge.ChallengeType;
import lych.soulcraft.world.event.challenge.SurvivalChallenge;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;

public class SurvivalChallengeManager extends ChallengeManager<SurvivalChallenge> {
    private static final String NAME = "SurvivalChallenge";

    public SurvivalChallengeManager(ServerWorld level) {
        super(NAME, level, ChallengeType.SURVIVAL, SurvivalChallenge::new, SurvivalChallenge::new);
    }

    public static SurvivalChallengeManager get(ServerWorld level) {
        DimensionSavedDataManager storage = level.getDataStorage();
        return storage.computeIfAbsent(() -> new SurvivalChallengeManager(level), NAME);
    }
}
