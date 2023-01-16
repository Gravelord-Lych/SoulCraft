package lych.soulcraft.extension.laser;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class LaserAttackResult {
    private final List<LivingEntity> passedEntities;
    private final List<BlockPos> passedBlockPos;
    private final List<Vector3d> passedPositions;
    private final LaserData data;
    private final World world;

    public LaserAttackResult(List<LivingEntity> passedEntities, List<BlockPos> passedBlockPos, List<Vector3d> passedPositions, LaserData data, World world) {
        this.passedEntities = passedEntities;
        this.passedBlockPos = passedBlockPos;
        this.passedPositions = passedPositions;
        this.data = data;
        this.world = world;
    }

    public List<LivingEntity> getPassedEntities() {
        return passedEntities;
    }

    public List<BlockPos> getPassedBlockPos() {
        return passedBlockPos;
    }

    public List<Vector3d> getPassedPositions() {
        return passedPositions;
    }

    public LaserData getData() {
        return data;
    }

    public World getWorld() {
        return world;
    }

    public List<BlockPos> getHitBlockPos() {
        return passedBlockPos.stream().filter(pos -> world.getBlockState(pos).getMaterial().isSolid()).collect(Collectors.toList());
    }

    public Optional<BlockPos> getLastHitBlock() {
        return Optional.ofNullable(passedBlockPos.isEmpty() ? null : passedBlockPos.get(passedBlockPos.size() - 1));
    }

    public Optional<Vector3d> getLastHitPos() {
        return Optional.ofNullable(passedPositions.isEmpty() ? null : passedPositions.get(passedPositions.size() - 1));
    }
}
