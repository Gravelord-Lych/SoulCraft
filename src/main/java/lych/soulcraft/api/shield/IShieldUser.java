package lych.soulcraft.api.shield;

import net.minecraft.util.DamageSource;
import org.jetbrains.annotations.Nullable;

/**
 * A shield user that can use {@link ISharedShield shared shield}
 */
public interface IShieldUser {
    /**
     * Gets the shield that is currently using by the entity.
     * @return The shield. <code>null</code> if no shield is using by the entity
     */
    @Nullable
    ISharedShield getSharedShield();

    /**
     * Sets the shield that is currently using by the entity.
     * @param sharedShield The shield. <code>null</code> if you want to remove the entity's shield
     */
    void setSharedShield(@Nullable ISharedShield sharedShield);

    /**
     * Returns true if the shield returned by the {@link IShieldUser#getSharedShield()} method can absorb damage.
     * @return True if the shield is valid
     */
    default boolean isShieldValid() {
        return getSharedShield() != null;
    }

    /**
     * This method will be called after the currently using shield is exhausted. (<code>shield.setHealth(0)</code>)<br>
     * The method will always be called no matter whether the currently using shield is
     * {@link ISharedShield#canBeConsumed() consumable} or not
     */
    default void onShieldExhausted() {}

    /**
     * This method will be called after the currently using shield is broken. (<code>setSharedShield(null)</code>)<br>
     * Unless the currently using shield is {@link ISharedShield#canBeConsumed() consumable},
     * the method will <b>not</b> be called.
     */
    default void onShieldBreak() {}

    /**
     * Returns whether hit particles should be shown or not.
     * @param source The damage source.
     * @param amount The amount of the damage.
     * @return True if hit particles should be shown.
     */
    default boolean showHitParticles(DamageSource source, float amount) {
        return true;
    }

    /**
     * @see ISharedShield#canBeConsumed() canBeConsumed
     * @return True if the entity has a consumable shield
     */
    default boolean hasConsumableShield() {
        return getSharedShield() != null && getSharedShield().canBeConsumed();
    }
}
