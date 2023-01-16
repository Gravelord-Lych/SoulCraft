package lych.soulcraft.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.AbstractRaiderEntity;
import net.minecraft.world.raid.Raid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Raid.WaveMember.class)
public interface WaveMemberAccessor {
    @Accessor
    EntityType<? extends AbstractRaiderEntity> getEntityType();
}
