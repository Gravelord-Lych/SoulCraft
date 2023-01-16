package lych.soulcraft.world.event.manager;

import lych.soulcraft.world.event.challenge.ChallengeType;
import lych.soulcraft.world.event.challenge.HuntingChallenge;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;

public class HuntingChallengeManager extends ChallengeManager<HuntingChallenge> {
    private static final String NAME = "HuntingChallenge";

    public HuntingChallengeManager(ServerWorld level) {
        super(NAME, level, ChallengeType.HUNTING, HuntingChallenge::new, HuntingChallenge::new);
    }

    public static HuntingChallengeManager get(ServerWorld level) {
        DimensionSavedDataManager storage = level.getDataStorage();
        return storage.computeIfAbsent(() -> new HuntingChallengeManager(level), NAME);
    }
}
