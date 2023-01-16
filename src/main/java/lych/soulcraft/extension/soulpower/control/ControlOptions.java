package lych.soulcraft.extension.soulpower.control;

import com.google.common.collect.ImmutableSet;
import lych.soulcraft.extension.highlight.HighlighterType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;

import java.util.Arrays;
import java.util.Objects;

public class ControlOptions {
    public static final ControlOptions DEFAULT = defaultSettings();
    private final boolean attackController;
    private final HighlighterType highlighterType;
    private final ImmutableSet<ControlFlag> flags;

    public ControlOptions(boolean attackController, HighlighterType highlighterType, ControlFlag... flags) {
        this(attackController, highlighterType, Arrays.stream(flags).filter(Objects::nonNull).collect(ImmutableSet.toImmutableSet()));
    }

    public ControlOptions(boolean attackController, HighlighterType highlighterType, ImmutableSet<ControlFlag> flags) {
        this.attackController = attackController;
        this.highlighterType = highlighterType;
        Objects.requireNonNull(flags, "Where are the flags?");
        this.flags = flags;
    }

    public static ControlOptions defaultSettings(ControlFlag... flags) {
        return builder().setFlags(flags).build();
    }

    public boolean attackController() {
        return attackController;
    }

    public ImmutableSet<ControlFlag> getFlags() {
        return flags;
    }

    public static Builder builder() {
        return new Builder();
    }

    public void saveTo(CompoundNBT compoundNBT) {
        compoundNBT.putBoolean("AttackController", attackController);
        compoundNBT.putUUID("HighlighterType", highlighterType.getUUID());
        ListNBT listNBT = new ListNBT();
        for (ControlFlag flag : flags) {
            listNBT.add(flag.save());
        }
        compoundNBT.put("ControlFlags", listNBT);
    }

    public static ControlOptions loadFrom(CompoundNBT compoundNBT) {
        return builder()
                .setAttackController(compoundNBT.getBoolean("AttackController"))
                .setHighlighterType(HighlighterType.get(compoundNBT.getUUID("HighlighterType")))
                .setFlags(compoundNBT.getList("ControlFlags", Constants.NBT.TAG_INT_ARRAY)
                        .stream()
                        .map(ControlFlag::load)
                        .toArray(ControlFlag[]::new))
                .build();
    }

    public HighlighterType getHighlighterType() {
        return highlighterType;
    }

    public static class Builder {
        private boolean attackController = false;
        private HighlighterType highlighterType;
        private ControlFlag[] flags;

        public Builder setAttackController(boolean attackController) {
            this.attackController = attackController;
            return this;
        }

        public Builder setHighlighterType(HighlighterType highlighterType) {
            this.highlighterType = highlighterType;
            return this;
        }

        public Builder setFlags(ControlFlag... flags) {
            this.flags = flags;
            return this;
        }

        public ControlOptions build() {
            return new ControlOptions(attackController, highlighterType, flags);
        }
    }
}
