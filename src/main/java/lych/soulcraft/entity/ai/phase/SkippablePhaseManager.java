package lych.soulcraft.entity.ai.phase;

import lych.soulcraft.util.IIdentifiableEnum;
import net.minecraft.util.math.MathHelper;

import java.util.function.Supplier;

public class SkippablePhaseManager<E extends Enum<E> & ISkippablePhase & IIdentifiableEnum> extends PhaseManager<E> {
    public SkippablePhaseManager(Supplier<? extends E[]> values) {
        super(values);
    }

    @Override
    public void nextPhase() {
        nextPhaseDirectly();
        E nextPhase = getNextPhase();
        if (shouldSkip(nextPhase)) {
            nextPhase();
        }
    }

    private void nextPhaseDirectly() {
        int maxPhase = getValues().length - 1;
        if (getPhaseId() >= maxPhase) {
            setPhaseId(0);
        } else {
            setPhaseId(getPhaseId() + 1);
        }
    }

    private boolean shouldSkip(E nextPhase) {
        double skipProbability = getSkipProbability(nextPhase);
        if (skipProbability <= 0) {
            return false;
        }
        if (skipProbability >= 1) {
            return true;
        }
        return getRandom().nextDouble() < nextPhase.getSkipProbability();
    }

    private E getNextPhase() {
        int maxPhase = getValues().length - 1;
        if (getPhaseId() >= maxPhase) {
            return getPhase(0);
        }
        return getPhase(getPhaseId() + 1);
    }

    private double getSkipProbability(E phase) {
        return MathHelper.clamp(phase.getSkipProbability(), 0, 1);
    }
}
