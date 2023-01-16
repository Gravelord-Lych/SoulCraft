package lych.soulcraft.extension.laser;

import com.google.common.collect.ImmutableSortedMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.TriPredicate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LaserSource {
    private final LaserData data;
    final Vector3d src;
    final World level;

    /**
     * Use {@link LaserData#create(Vector3d, World)}
     */
    LaserSource(LaserData data, Vector3d src, World level) {
        this.data = data;
        this.src = src;
        this.level = level;
    }

    public LaserAttackResult attack(Entity target, Object... toAvoid) {
        return directlyAttack(src.vectorTo(target.getBoundingBox().getCenter()), toAvoid);
    }

    public LaserAttackResult attack(Vector3d target, Object... toAvoid) {
        return directlyAttack(src.vectorTo(target), toAvoid);
    }

//  TODO - Merge this, attackAndStopIfHit and getResult method to remove redundant methods.
    public LaserAttackResult directlyAttack(Vector3d vecToTarget, Object... toAvoid) {
        final double stepLen = data.getSpacing();
        final Vector3d stepVec = vecToTarget.normalize().scale(stepLen);
        final ImmutableSortedMap<LaserHitPredicate<?>, Integer> predicates = data.getPredicates();

        final List<Object> listToAvoid = Arrays.asList(toAvoid);
        final List<LivingEntity> passedEntities = new ArrayList<>();
        final List<BlockPos> passedBlockPos = new ArrayList<>(50);
        final List<Vector3d> passedPositions = new ArrayList<>(100);

        int durabilityRemaining = data.getDurability();
        for (int i = 1; durabilityRemaining > 0; i++) {
            Vector3d vec = src.add(stepVec.scale(i));
            for (LaserHitPredicate<?> predicate : predicates.keySet()) {
                if (!listToAvoid.contains(vec)) {
                    passedPositions.add(vec);
                }
                Object result = predicate.apply(vec, level);
                BlockPos pos = new BlockPos(vec);
                if (!listToAvoid.contains(pos) && !passedBlockPos.contains(pos)) {
                    passedBlockPos.add(pos);
                }
                final boolean passed = predicate.test(result);
                if (predicate.getHitType() == LaserHitType.ENTITY && !listToAvoid.contains(result) && passed) {
                    if (predicate.shouldAddToLaserHitResult()) {
                        passedEntities.add((LivingEntity) result);
                    }
                }
                if (!listToAvoid.contains(result) && passed) {
                    durabilityRemaining -= predicates.get(predicate);
                }
                if (passed) {
                    break;
                }
            }
        }
        return new LaserAttackResult(passedEntities, passedBlockPos, passedPositions, data, level);
    }

    public LaserAttackResult attackAndStopIfHit(Entity target, double dist, Object... toAvoid) {
        return attackAndStopIfHit(target.getBoundingBox().getCenter(), dist, toAvoid);
    }

    public LaserAttackResult attackAndStopIfHit(Vector3d target, double dist, Object... toAvoid) {
        Vector3d vecToTarget = src.vectorTo(target);
        if (dist < 0 || Double.isInfinite(dist) || Double.isNaN(dist)) {
            return directlyAttack(vecToTarget, toAvoid);
        }
        return getResult(target, dist, vecToTarget, (vecIn, targetIn, distIn) -> vecIn.distanceToSqr(targetIn) <= distIn * distIn, toAvoid);
    }

    private LaserAttackResult getResult(Vector3d target, double dist, Vector3d vecToTarget, TriPredicate<? super Vector3d, ? super Vector3d, ? super Double> shouldBreakPredicate, Object... toAvoid) {
        final double stepLen = data.getSpacing();
        final Vector3d stepVec = vecToTarget.normalize().scale(stepLen);
        final ImmutableSortedMap<LaserHitPredicate<?>, Integer> predicates = data.getPredicates();

        final List<Object> listToAvoid = Arrays.asList(toAvoid);
        final List<LivingEntity> passedEntities = new ArrayList<>();
        final List<BlockPos> passedBlockPos = new ArrayList<>(50);
        final List<Vector3d> passedPositions = new ArrayList<>(100);

        int durabilityRemaining = data.getDurability();
        for (int i = 1; durabilityRemaining > 0; i++) {
            Vector3d vec = src.add(stepVec.scale(i));
            boolean shouldBreak = shouldBreakPredicate.test(vec, target, dist);
            for (LaserHitPredicate<?> predicate : predicates.keySet()) {
                if (!listToAvoid.contains(vec)) {
                    passedPositions.add(vec);
                }
                Object result = predicate.apply(vec, level);
                BlockPos pos = new BlockPos(vec);
                if (!listToAvoid.contains(pos) && !passedBlockPos.contains(pos)) {
                    passedBlockPos.add(pos);
                }

                final boolean passed = predicate.test(result);

                if (predicate.getHitType() == LaserHitType.ENTITY && !listToAvoid.contains(result) && passed) {
                    if (predicate.shouldAddToLaserHitResult()) {
                        passedEntities.add((LivingEntity) result);
                    }
                }
                if (!listToAvoid.contains(result) && passed) {
                    durabilityRemaining -= predicates.get(predicate);
                }

                if (passed) {
                    break;
                }
            }
            if (shouldBreak) {
                Vector3d nextVec = src.add(stepVec.scale(i + 1));
//                  If next position laser will hit is invalid or is worse than current, break.
                if (!shouldBreakPredicate.test(nextVec, target, dist) || !(nextVec.distanceToSqr(target) <= vec.distanceToSqr(target))) {
                    break;
                }
            }
        }
        return new LaserAttackResult(passedEntities, passedBlockPos, passedPositions, data, level);
    }
}
