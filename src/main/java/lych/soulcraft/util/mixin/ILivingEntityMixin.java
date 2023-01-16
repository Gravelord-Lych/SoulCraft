package lych.soulcraft.util.mixin;

public interface ILivingEntityMixin {
    double getKnockupStrength();

    void setKnockupStrength(double knockupStrength);

    long getSheepReinforcementTickCount();

    long getSheepReinforcementLastHurtByTimestamp();

    void setSheepReinforcementLastHurtByTimestamp(long sheepReinforcementLastHurtByTimestamp);
}
