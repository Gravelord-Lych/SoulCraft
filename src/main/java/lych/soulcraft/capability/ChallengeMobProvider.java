package lych.soulcraft.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChallengeMobProvider implements ICapabilityProvider, INBTSerializable<CompoundNBT> {
    private final IChallengeMob challengeMob;

    public ChallengeMobProvider(ServerWorld world) {
        this.challengeMob = new ChallengeMob(world);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == NonAPICapabilities.CHALLENGE_MOB ? LazyOptional.of(() -> challengeMob).cast() : LazyOptional.empty();
    }

    @Override
    public CompoundNBT serializeNBT() {
        return challengeMob.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        challengeMob.deserializeNBT(nbt);
    }
}
