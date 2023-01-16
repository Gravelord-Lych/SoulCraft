package lych.soulcraft.world.event.manager;

import lych.soulcraft.world.event.challenge.ChallengeType;
import lych.soulcraft.world.event.challenge.UndeadSurvivalChallenge;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;

public class UndeadSurvivalChallengeManager extends ChallengeManager<UndeadSurvivalChallenge> {
    private static final String NAME = "UndeadSurvivalChallenge";

    public UndeadSurvivalChallengeManager(ServerWorld level) {
        super(NAME, level, ChallengeType.UNDEAD_SURVIVAL, UndeadSurvivalChallenge::new, UndeadSurvivalChallenge::new);
    }

    public static UndeadSurvivalChallengeManager get(ServerWorld level) {
        DimensionSavedDataManager storage = level.getDataStorage();
        return storage.computeIfAbsent(() -> new UndeadSurvivalChallengeManager(level), NAME);
    }
}
