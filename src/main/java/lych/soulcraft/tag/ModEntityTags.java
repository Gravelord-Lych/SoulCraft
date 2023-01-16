package lych.soulcraft.tag;

import lych.soulcraft.SoulCraft;
import lych.soulcraft.api.TagNames;
import net.minecraft.entity.EntityType;
import net.minecraft.tags.EntityTypeTags;
import net.minecraftforge.common.Tags;

public class ModEntityTags {
    public static final Tags.IOptionalNamedTag<EntityType<?>> TIERED_BOSS = tag(TagNames.TIERED_BOSS.getPath());

    private static Tags.IOptionalNamedTag<EntityType<?>> tag(String name) {
        return EntityTypeTags.createOptional(SoulCraft.prefix(name));
    }
}
