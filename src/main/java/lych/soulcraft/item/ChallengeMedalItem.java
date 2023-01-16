package lych.soulcraft.item;

import com.mojang.authlib.GameProfile;
import lych.soulcraft.world.event.challenge.ChallengeMedalType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.Constants;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.SoftOverride;

public class ChallengeMedalItem extends Item {
    private final ChallengeMedalType type;

    public ChallengeMedalItem(Properties properties, ChallengeMedalType type) {
        super(properties);
        this.type = type;
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return type.getRarity();
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return type.isFoil();
    }

    @SoftOverride
    public boolean isSoulFoil(ItemStack stack) {
        return type.isSoulFoil();
    }

    @Override
    public ITextComponent getName(ItemStack stack) {
        if (stack.hasTag()) {
            String name = initName(stack);
            if (name != null) {
                return new TranslationTextComponent(getDescriptionId() + ".named", name);
            }
        }
        return super.getName(stack);
    }

    @Nullable
    private String initName(ItemStack stack) {
        String name = null;
        CompoundNBT tag = stack.getOrCreateTag();
        if (tag.contains("Owner", Constants.NBT.TAG_STRING)) {
            name = tag.getString("Owner");
        } else if (tag.contains("Owner", Constants.NBT.TAG_COMPOUND)) {
            CompoundNBT compoundNBT = tag.getCompound("Owner");
            if (compoundNBT.contains("Name", Constants.NBT.TAG_STRING)) {
                name = compoundNBT.getString("Name");
            }
        }
        return name;
    }

    @Override
    public boolean verifyTagAfterLoad(CompoundNBT compoundNBT) {
        super.verifyTagAfterLoad(compoundNBT);
        if (compoundNBT.contains("Owner", Constants.NBT.TAG_STRING) && !StringUtils.isBlank(compoundNBT.getString("Owner"))) {
            GameProfile profile = new GameProfile(null, compoundNBT.getString("Owner"));
            profile = SkullTileEntity.updateGameprofile(profile);
            if (profile != null) {
                compoundNBT.put("Owner", NBTUtil.writeGameProfile(new CompoundNBT(), profile));
                return true;
            }
        }
        return false;
    }
}
