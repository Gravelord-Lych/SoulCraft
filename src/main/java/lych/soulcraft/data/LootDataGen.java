package lych.soulcraft.data;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import lych.soulcraft.SoulCraft;
import lych.soulcraft.data.loot.ModBlockLootTables;
import lych.soulcraft.data.loot.ModChestLootTables;
import net.minecraft.data.DataGenerator;
import net.minecraft.loot.LootParameterSet;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootTable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.data.ForgeLootTableProvider;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class LootDataGen extends ForgeLootTableProvider {
    private final List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> tables = ImmutableList.of(
            Pair.of(ModBlockLootTables::new, LootParameterSets.BLOCK),
            Pair.of(ModChestLootTables::new, LootParameterSets.CHEST)
    );

    public LootDataGen(DataGenerator gen) {
        super(gen);
    }

    @Override
    public List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> getTables() {
        return tables;
    }

    @Override
    public String getName() {
        return super.getName() + ": " + SoulCraft.MOD_ID;
    }
}
