package lych.soulcraft.entity.iface;

public interface IDamageMultipliable {
    float getDamageMultiplier();

    default boolean multiplyFinalDamage() {
        return false;
    }
}
