package lych.soulcraft.api.exa;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import org.jetbrains.annotations.NotNull;

public interface IExtraAbility extends Comparable<IExtraAbility> {
    /**
     * @return The registry name of the <i>Extra Ability</i>.
     */
    ResourceLocation getRegistryName();

    /**
     * @return True if the player has this <i>Extra Ability</i>
     */
    boolean isOn(PlayerEntity player);

    /**
     * Add this <i>Extra Ability</i> to the specified player.
     * @return True if successfully added. False if player had this <i>Extra Ability</i>
     */
    boolean addTo(PlayerEntity player);

    /**
     * Remove this <i>Extra Ability</i> from the specified player.
     * @return True if successfully removed. False if player did not have this <i>Extra Ability</i>
     */
    boolean removeFrom(PlayerEntity player);

    ITextComponent getDisplayName();

    /**
     * Returns the count of <i>Soul Container</i> needed to apply the <i>Extra Ability</i> to a player.
     * @return The count of <i>Soul Container</i>
     */
    int getSoulContainerCost();

    /**
     * Returns the count of <i>SE</i> needed to apply the <i>Extra Ability</i> to a player.
     * @return The count of <i>SE</i>
     */
    int getSECost();

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

    /**
     * @return True if this is a dummy
     */
    default boolean isDummy() {
        return this instanceof Dummy;
    }

    static IExtraAbility dummy() {
        return Dummy.INSTANCE;
    }
}
class Dummy implements IExtraAbility {
    static final IExtraAbility INSTANCE = new Dummy();

    private Dummy() {}

    private static final ResourceLocation DUMMY_REGISTRY_NAME = new ResourceLocation("dummy");
    private static final ITextComponent DUMMY_DISPLAY_NAME = new StringTextComponent("Dummy");

    @Override
    public ResourceLocation getRegistryName() {
        return DUMMY_REGISTRY_NAME;
    }

    @Override
    public boolean isOn(PlayerEntity player) {
        return false;
    }

    @Override
    public boolean addTo(PlayerEntity player) {
        return false;
    }

    @Override
    public boolean removeFrom(PlayerEntity player) {
        return false;
    }

    @Override
    public ITextComponent getDisplayName() {
        return DUMMY_DISPLAY_NAME;
    }

    @Override
    public int getSoulContainerCost() {
        return 0;
    }

    @Override
    public int getSECost() {
        return 0;
    }

    @Override
    public boolean isSpecial() {
        return false;
    }

    @Override
    public TextFormatting getStyle() {
        return TextFormatting.GRAY;
    }

    @Override
    public int compareTo(@NotNull IExtraAbility o) {
        return o.isDummy() ? getRegistryName().compareTo(o.getRegistryName()) : -1;
    }

    @Override
    public String toString() {
        return "DummyExtraAbility";
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof IExtraAbility && ((IExtraAbility) obj).isDummy();
    }

    @Override
    public int hashCode() {
        return 1;
    }
}
