package lych.soulcraft.extension.control;

import com.mojang.datafixers.util.Pair;
import lych.soulcraft.SoulCraft;
import lych.soulcraft.config.ConfigHelper;
import lych.soulcraft.extension.highlight.EntityHighlightManager;
import lych.soulcraft.extension.highlight.HighlighterType;
import lych.soulcraft.util.EntityUtils;
import lych.soulcraft.util.Utils;
import lych.soulcraft.util.mixin.IBrainMixin;
import lych.soulcraft.util.mixin.IGoalSelectorMixin;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class SoulManager extends WorldSavedData {
    static final Marker MARKER = MarkerManager.getMarker("SoulManager");
    private static final String NAME = "SoulManager";
    private final ServerWorld level;
    private final Map<UUID, Pair<UUID, PriorityQueue<Controller<?>>>> controllers = new HashMap<>();

    public SoulManager(ServerWorld level) {
        super(NAME);
        this.level = level;
    }

    private static PriorityQueue<Controller<?>> makeQueue(UUID u) {
        return new PriorityQueue<>(Comparator.comparingInt(Controller::getPriority));
    }

    public void tick() {
        for (Iterator<Map.Entry<UUID, Pair<UUID, PriorityQueue<Controller<?>>>>> iterator = controllers.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<UUID, Pair<UUID, PriorityQueue<Controller<?>>>> entry = iterator.next();
            MobEntity mob = getMob(entry);
            PriorityQueue<Controller<?>> controllers = getControllers(entry);
            Controller<?> controller = controllers.element();
            if (EntityUtils.isDead(mob) || !controller.isPreparing() && mob == null) {
                iterator.remove();
                if (EntityUtils.isAlive(mob)) {
                    stopControlling(mob);
                }
                setDirty();
                continue;
            }
            if (controller.isPreparing() && mob != null) {
                startControlling(mob, controller);
                controller.setPreparing(false);
                setDirty();
            }
            if (mob != null) {
                EntityHighlightManager.get(level).highlight(HighlighterType.SOUL_CONTROL, mob);
            }
        }
    }

    private UUID getMobUUID(Map.Entry<UUID, Pair<UUID, PriorityQueue<Controller<?>>>> entry) {
        return entry.getKey();
    }

    @Nullable
    private MobEntity getMob(Map.Entry<UUID, Pair<UUID, PriorityQueue<Controller<?>>>> entry) {
        UUID uuid = getMobUUID(entry);
        return Optional.ofNullable(level.getEntity(uuid)).filter(entity -> entity instanceof MobEntity).map(MobEntity.class::cast).orElse(null);
    }

    private UUID getPlayerUUID(Map.Entry<UUID, Pair<UUID, PriorityQueue<Controller<?>>>> entry) {
        return entry.getValue().getFirst();
    }

    private PriorityQueue<Controller<?>> getControllers(Map.Entry<UUID, Pair<UUID, PriorityQueue<Controller<?>>>> entry) {
        return entry.getValue().getSecond();
    }

    public static SoulManager get(ServerWorld world) {
        return world.getDataStorage().computeIfAbsent(() -> new SoulManager(world), NAME);
    }

    public Pair<UUID, PriorityQueue<Controller<?>>> getControllerData(MobEntity mob) {
        return controllers.get(mob.getUUID());
    }

    public <T extends MobEntity> boolean add(T mob, PlayerEntity player, ControllerType<? super T> type) {
        if (controllers.containsKey(mob.getUUID()) && controllers.get(mob.getUUID()).getSecond().stream().anyMatch(c -> c.getType() == type)) {
            return false;
        }
        Controller<?> controller = type.create(mob, player);
        controllers.computeIfAbsent(mob.getUUID(), uuid -> Pair.of(player.getUUID(), makeQueue(uuid))).getSecond().add(controller);
        setDirty();
        startControlling(mob, controller);
        return true;
    }

    @Nullable
    public Set<MobEntity> getControllingMobs(PlayerEntity player) {
        return controllers.entrySet().stream().filter(e -> Objects.equals(getPlayerUUID(e), player.getUUID())).map(this::getMob).filter(EntityUtils::isAlive).collect(Collectors.toSet());
    }

    @Nullable
    public PlayerEntity getPlayerController(MobEntity mob) {
        return Utils.applyIfNonnull(controllers.get(mob.getUUID()).getFirst(), level::getPlayerByUUID);
    }

    public void remove(MobEntity mob) {
        controllers.remove(mob.getUUID());
        postRemoval(mob);
    }

    public void remove(MobEntity mob, ControllerType<?> type) {
        PriorityQueue<Controller<?>> queue = controllers.get(mob.getUUID()).getSecond();
        queue.removeIf(c -> c.getType() == type);
        if (queue.isEmpty()) {
            remove(mob);
        } else {
            postRemoval(mob);
            startControlling(mob, queue.element());
        }
    }

    private void postRemoval(MobEntity mob) {
        stopControlling(mob);
        setDirty();
    }

    void startControlling(MobEntity mob, Controller<?> controller) {
        ((IBrainMixin<?>) mob.getBrain()).setDisabledIfValid(true);
        controller.startControlling(mob, ((IGoalSelectorMixin) mob.goalSelector).getAlt(), (((IGoalSelectorMixin) mob.targetSelector)).getAlt());
        if (!controller.overrideBehaviorGoals()) {
            ((IGoalSelectorMixin) mob.goalSelector).transferGoals();
        }
        if (!controller.overrideTargetGoals()) {
            ((IGoalSelectorMixin) mob.targetSelector).transferGoals();
        }
    }

    void stopControlling(MobEntity mob) {
        ((IBrainMixin<?>) mob.getBrain()).setDisabledIfValid(false);
        ((IGoalSelectorMixin) mob.goalSelector).removeAllAltGoals();
        ((IGoalSelectorMixin) mob.targetSelector).removeAllAltGoals();
    }

    @Override
    public void load(CompoundNBT compoundNBT) {
        if (compoundNBT.contains("Controllers", Constants.NBT.TAG_LIST)) {
            controllers.clear();
            ListNBT controllersNBT = compoundNBT.getList("Controllers", Constants.NBT.TAG_LIST);
            for (int i = 0; i < controllersNBT.size(); i++) {
                ListNBT controllersForOneMobNBT = controllersNBT.getList(i);
                for (int j = 0; j < controllersForOneMobNBT.size(); j++) {
                    CompoundNBT singleNBT = controllersForOneMobNBT.getCompound(j);
                    loadOne(singleNBT);
                }
            }
        }
    }

    private void loadOne(CompoundNBT singleNBT) {
        String name = singleNBT.getString("Type");
        ResourceLocation location;
        try {
            location = new ResourceLocation(name);
        } catch (ResourceLocationException e) {
            if (ConfigHelper.shouldFailhard()) {
                throw new ResourceLocationException(ConfigHelper.FAILHARD_MESSAGE + "Failed to parse a controller's registry name: " + e.getMessage());
            }
            SoulCraft.LOGGER.error(MARKER, "Failed to parse a controller's registry name", e);
            return;
        }
        ControllerType<?> type = ControllerType.byRegistryName(location);
        if (type == null) {
            SoulCraft.LOGGER.warn(MARKER, "Found unknown controller {}, ignored", location);
            return;
        }
        CompoundNBT controllerData = singleNBT.getCompound("ControllerData");
        Controller<?> controller = type.load(controllerData, level);
        controllers.computeIfAbsent(controller.getMobUUID(), u -> Pair.of(controller.getPlayerUUID(), makeQueue(u))).getSecond().add(controller);
        controller.setPreparing(true);
    }

    @Override
    public CompoundNBT save(CompoundNBT compoundNBT) {
        ListNBT controllersNBT = new ListNBT();
        for (Map.Entry<UUID, Pair<UUID, PriorityQueue<Controller<?>>>> entry : controllers.entrySet()) {
            ListNBT controllersForOneMobNBT = new ListNBT();
            for (Controller<?> controller : getControllers(entry)) {
                CompoundNBT singleNBT = new CompoundNBT();
                singleNBT.putString("Type", controller.getRegistryName().toString());
                singleNBT.put("ControllerData", controller.save());
                controllersForOneMobNBT.add(singleNBT);
            }
            controllersNBT.add(controllersForOneMobNBT);
        }
        compoundNBT.put("Controllers", controllersNBT);
        return compoundNBT;
    }
}
