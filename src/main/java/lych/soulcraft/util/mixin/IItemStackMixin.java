package lych.soulcraft.util.mixin;

public interface IItemStackMixin {
    boolean hasSoulFoil();

    default int getMaxReinforcementCount() {
        return 3;
    }
}
