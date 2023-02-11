package lych.soulcraft.listener;

import lych.soulcraft.SoulCraft;
import lych.soulcraft.api.capability.ISoulEnergyStorage;
import lych.soulcraft.dispenser.ModDispenserBehaviors;
import lych.soulcraft.entity.ModEntities;
import lych.soulcraft.extension.control.ControllerType;
import lych.soulcraft.extension.fire.Fires;
import lych.soulcraft.extension.soulpower.reinforce.Reinforcements;
import lych.soulcraft.potion.ModPotions;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.jetbrains.annotations.Nullable;

@Mod.EventBusSubscriber(modid = SoulCraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventListener {
    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        ModDispenserBehaviors.registerBehaviors();
        ModEntities.registerEntitySpawnPlacements();
        ModPotions.registerBrewingRecipes();
        registerCapabilities(event);
        Reinforcements.init();
        Fires.init();
        ControllerType.init();
    }

    private static void registerCapabilities(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            CapabilityManager.INSTANCE.register(ISoulEnergyStorage.class, new DummyCapabilityStorage<>(), () -> null);
        });
    }

    /**
     * Capability providers are used to store data, not this dummy.
     */
    private static class DummyCapabilityStorage<T> implements Capability.IStorage<T> {
        @Nullable
        @Override
        public INBT writeNBT(Capability<T> capability, T instance, Direction side) {
            return null;
        }

        @Override
        public void readNBT(Capability<T> capability, T instance, Direction side, INBT nbt) {}
    }
}
