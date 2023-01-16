package lych.soulcraft.extension.soulpower.control;

import lych.soulcraft.util.mixin.IGoalSelectorMixin;
import lych.soulcraft.util.mixin.INearestAttackableTargetGoalMixin;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class GoalCapturer {
    private GoalCapturer() {}

    public static Optional<? extends NearestAttackableTargetGoal<?>> captureTargetGoalForPlayer(GoalSelector selector) {
        return captureActiveTargetGoals(selector).stream()
                .filter(pg -> pg.getGoal() instanceof NearestAttackableTargetGoal)
                .map(pg -> ((NearestAttackableTargetGoal<?>) pg.getGoal()))
                .filter(goal -> ((INearestAttackableTargetGoalMixin<?>) goal).getTargetType() == PlayerEntity.class)
                .findFirst();
    }

    public static List<PrioritizedGoal> captureActiveTargetGoals(GoalSelector selector) {
        return captureAll(selector, goal -> goal instanceof TargetGoal);
    }

    public static List<PrioritizedGoal> captureAll(GoalSelector selector, Predicate<? super Goal> predicate) {
        return ((IGoalSelectorMixin) selector).getAvailableGoals().stream().filter(pg -> predicate.test(pg.getGoal())).collect(Collectors.toList());
    }
}
