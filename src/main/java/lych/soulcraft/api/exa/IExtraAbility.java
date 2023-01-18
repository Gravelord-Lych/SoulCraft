package lych.soulcraft.api.exa;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

public interface IExtraAbility extends Comparable<IExtraAbility> {
    /**
     * @return The registry name of the <i>Extra Ability</i>.
     */
    ResourceLocation getRegistryName();

    /**
     * @return True if the player has this <i>Extra Ability</i>.
     */
    boolean isOn(PlayerEntity player);

    /**
     * Add this <i>Extra Ability</i> to the specified player.
     * @return True if successfully added. False if player had this <i>Extra Ability</i>.
     */
    boolean addTo(PlayerEntity player);

    /**
     * Remove this <i>Extra Ability</i> from the specified player.
     * @return True if successfully removed. False if player did not have this <i>Extra Ability</i>.
     */
    boolean removeFrom(PlayerEntity player);

    ITextComponent getDisplayName();

    /**
     * Returns the count of <i>Soul Container</i> needed to apply the <i>Extra Ability</i> to a player.
     * @return The count of <i>Soul Container</i>
     */
    int getSoulContainerCost();

    static boolean hasExtraAbility(PlayerEntity player, IExtraAbility exa) {
        return exa.isOn(player);
    }

    static boolean addExtraAbility(PlayerEntity player, IExtraAbility exa) {
        return exa.addTo(player);
    }

    static boolean removeExtraAbility(PlayerEntity player, IExtraAbility exa) {
        return exa.removeFrom(player);
    }

    /**
     * Indicates if the <i>Extra Ability</i> is special.
     * @return True if the <i>Extra Ability</i> is special
     */
    boolean isSpecial();

    /**
     * Gets the text style to format the <i>Extra Ability</i>.
     * @return The text style
     */
    TextFormatting getStyle();
}
