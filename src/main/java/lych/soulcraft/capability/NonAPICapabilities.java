package lych.soulcraft.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class NonAPICapabilities {
    @CapabilityInject(IChallengeMob.class)
    public static Capability<IChallengeMob> CHALLENGE_MOB = null;
}
