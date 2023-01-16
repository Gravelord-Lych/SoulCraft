package lych.soulcraft.extension.soulpower.control;

import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import lych.soulcraft.entity.iface.IStrongMinded;
import lych.soulcraft.extension.highlight.EntityHighlightManager;
import lych.soulcraft.extension.soulpower.control.controller.Adjustment;
import lych.soulcraft.extension.soulpower.control.controller.ControlledMobBehavior.Instance;
import lych.soulcraft.util.EntityUtils;
import lych.soulcraft.util.Utils;
import lych.soulcraft.world.event.manager.EventManager;
import lych.soulcraft.world.event.manager.NotLoadedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static lych.soulcraft.SoulCraft.LOGGER;

public class SoulManager extends WorldSavedData {
    private static final String NAME = "SoulManager";
    private static final int SAVE_FREQ = 40;
    public static final Marker CONTROLLER = MarkerManager.getMarker(NAME);
    private final ServerWorld level;
    private final Set<UUID> entitiesControlling = new HashSet<>(32);
    private final Map<UUID, Pair<ControlledMobData, ControlOptions>> entitiesControllingDataMap = new HashMap<>(32);
    private final Map<UUID, Instance> controllerMap = new HashMap<>(32);
    private final EventHelper eventHelper = new EventHelper();
    private long tickCounter;
    private int dataSaver = SAVE_FREQ;
    private boolean initialized;

    public SoulManager(ServerWorld level) {
        super(NAME);
        this.level = level;
        init();
    }

    public static SoulManager get(ServerWorld level) {
        DimensionSavedDataManager storage = level.getDataStorage();
        return storage.computeIfAbsent(() -> new SoulManager(level), NAME);
    }

    public void init() {
        if (!initialized) {
            LOGGER.info(CONTROLLER, "SoulManager in {} initialized", level.dimension().location());
            initialized = true;
        }
    }

    public boolean isControlling(MobEntity mob) {
        return isControlling(mob.getUUID());
    }

    public boolean isControlling(UUID uuid) {
        return getControllingTime(uuid) >= 0;
    }

    public long getControllingTime(UUID uuid) {
        ControlledMobData data = Utils.applyIfNonnull(entitiesControllingDataMap.get(uuid), Pair::getFirst);
        if (data == null) {
            return -1;
        } else if (data.isInfiniteControlTime()) {
            return Long.MAX_VALUE;
        }
        return Math.max(data.getControlTime() - tickCounter, -1);
    }

    /**
     * Start controlling a mob.
     * @param mob The mob
     * @param data The {@link ControlledMobData}
     * @return True if started controlling the mob, false if updated control time.
     */
    public boolean control(MobEntity mob, ControlledMobData data) {
        return control(mob, data, ControlOptions.DEFAULT);
    }

    /**
     * Start controlling a mob.
     * @param mob The mob
     * @param data The {@link ControlledMobData}
     * @param options The {@link ControlOptions}
     * @return True if started controlling the mob, false if updated control time or failed to control the mob.
     */
    public boolean control(MobEntity mob, ControlledMobData data, ControlOptions options) {
        if (mob instanceof IStrongMinded && !(data.getBehaviorType() instanceof Adjustment<?>)) {
            return false;
        }
        EntityHighlightManager.get(level).highlight(options.getHighlighterType(), mob, data.getControlTime());
        data = data.plusTime(tickCounter);
        entitiesControllingDataMap.put(mob.getUUID(), Pair.of(data, options));
        Instance instance = data.getBehaviorType().create(mob, level);
        controllerMap.putIfAbsent(mob.getUUID(), instance);
        setDirty();
        if (entitiesControlling.add(mob.getUUID())) {
            if (!options.attackController() && Objects.equals(mob.getTarget(), level.getEntity(data.getController()))) {
                EntityUtils.setTarget(mob, null);
            }
            instance.startControllingMob(mob);
            eventHelper.onStartControllingMob(mob);
            return true;
        }
        eventHelper.onContinueToControlMob(mob);
        return false;
    }

    /**
     * Stop controlling a mob <b>immediately</b>.
     * @param mob The mob
     * @return True if stopped controlling the mob.
     */
    public boolean stopControlling(MobEntity mob) {
        if (entitiesControlling.remove(mob.getUUID())) {
            EntityHighlightManager.get(level).unhighlightType(mob, Objects.requireNonNull(getOptions(mob.getUUID())).getHighlighterType());
            stop(mob.getUUID());
            eventHelper.onStopControllingMob(mob);
            return true;
        }
        return false;
    }

    private void stop(UUID uuid) {
        entitiesControllingDataMap.remove(uuid);
        Instance instance = controllerMap.get(uuid);
        Entity entity = level.getEntity(uuid);
        if (entity instanceof MobEntity && EntityUtils.isAlive(entity)) {
            instance.stopControllingMob((MobEntity) entity);
        }
        controllerMap.remove(uuid);
        setDirty();
    }

    public void tick() {
        if (!EventManager.canTick()) {
            return;
        }
        tickCounter++;
        if (dataSaver > 0) {
            dataSaver--;
        } else {
            setDirty();
            dataSaver = SAVE_FREQ;
        }
        Iterator<UUID> itr = entitiesControlling.iterator();
        while (itr.hasNext()) {
            UUID uuid = itr.next();
            Entity entity = level.getEntity(uuid);
            ControlledMobData data = entitiesControllingDataMap.get(uuid).getFirst();
            if (EntityUtils.isDead(entity) || !data.isInfiniteControlTime() && data.getControlTime() < tickCounter) {
                itr.remove();
                if (EntityUtils.isAlive(entity)) {
                    EntityHighlightManager.get(level).unhighlightType(entity, Objects.requireNonNull(getOptions(entity.getUUID())).getHighlighterType());
                }
                stop(uuid);
                if (entity instanceof MobEntity) {
                    eventHelper.onStopControllingMob((MobEntity) entity);
                }
                continue;
            }
            controllerMap.get(uuid).tick();
        }
    }

    @Nullable
    public ControlledMobData getData(MobEntity mob) {
        return getData(mob.getUUID());
    }

    @Nullable
    public ControlledMobData getData(UUID uuid) {
        return Utils.applyIfNonnull(entitiesControllingDataMap.get(uuid), Pair::getFirst);
    }

    @Nullable
    public ControlOptions getOptions(MobEntity mob) {
        return getOptions(mob.getUUID());
    }

    @Nullable
    public ControlOptions getOptions(UUID uuid) {
        return Utils.applyIfNonnull(entitiesControllingDataMap.get(uuid), Pair::getSecond);
    }

    public Set<ControlFlag> getFlagIntersection(MobEntity m1, MobEntity m2) {
        return getFlagIntersection(m1.getUUID(), m2.getUUID());
    }

    public Set<ControlFlag> getFlagIntersection(UUID u1, UUID u2) {
        ControlOptions o1 = getOptions(u1);
        ControlOptions o2 = getOptions(u2);
        if (o1 == null || o2 == null) {
            return Collections.emptySet();
        }
        return Sets.intersection(o1.getFlags(), o2.getFlags());
    }

    @Override
    public void load(CompoundNBT compoundNBT) {
        tickCounter = compoundNBT.getLong("Ticks");
        if (compoundNBT.contains("SoulControlData", Constants.NBT.TAG_LIST)) {
            entitiesControllingDataMap.clear();
            entitiesControlling.clear();
            ListNBT listNBT = compoundNBT.getList("SoulControlData", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < listNBT.size(); i++) {
                CompoundNBT single = listNBT.getCompound(i);
                UUID uuid = single.getUUID("UUID");
                ControlledMobData data = ControlledMobData.loadFrom(single);
                ControlOptions options = ControlOptions.loadFrom(single);
                Instance instance;
                try {
                    instance = data.getBehaviorType().load(level, single.getCompound("ControllerInstance"));
                    entitiesControllingDataMap.put(uuid, Pair.of(data, options));
                    entitiesControlling.add(uuid);
                    controllerMap.putIfAbsent(uuid, instance);
                } catch (NotLoadedException e) {
                    LOGGER.warn(CONTROLLER, "Failed to control entity " + uuid, e);
                }
            }
        }
        postLoad();
    }

    private boolean postLoad() {
        if (entitiesControlling.isEmpty()) {
            return true;
        }
        LOGGER.info(CONTROLLER, "SoulManager in {} found {} mob(s) to control", level.dimension().location(), entitiesControlling.size());
        int controlCount = 0;
        for (UUID uuid : entitiesControlling) {
            Entity entity = level.getEntity(uuid);
            if (entity instanceof MobEntity) {
                MobEntity mob = (MobEntity) entity;
                Instance controller = controllerMap.get(mob.getUUID());
                controller.startControllingMob(mob);
                controlCount++;
            } else if (entity == null) {
                Instance controller = controllerMap.get(uuid);
                controller.prepareToControlMob();
                controlCount++;
            }
        }
        infoControlCount(controlCount, entitiesControlling.size());
        return controlCount == entitiesControlling.size();
    }

    private void infoControlCount(int controlCount, int expectedControlCount) {
        LOGGER.info(CONTROLLER, "SoulManager in {} actually controls {}/{} mob(s)", level.dimension().location(), controlCount, expectedControlCount);
    }

    @Override
    public CompoundNBT save(CompoundNBT compoundNBT) {
        compoundNBT.putLong("Ticks", tickCounter);
        ListNBT listNBT = new ListNBT();
        for (UUID uuid : entitiesControlling) {
            CompoundNBT single = new CompoundNBT();
            single.putUUID("UUID", uuid);
            entitiesControllingDataMap.get(uuid).getFirst().saveTo(single);
            entitiesControllingDataMap.get(uuid).getSecond().saveTo(single);
            single.put("ControllerInstance", controllerMap.get(uuid).save());
            listNBT.add(single);
        }
        compoundNBT.put("SoulControlData", listNBT);
        return compoundNBT;
    }

    public boolean isInitialized() {
        return initialized;
    }

    private static class EventHelper {
        private void onStartControllingMob(MobEntity mob) {}

        private void onContinueToControlMob(MobEntity mob) {}

        private void onStopControllingMob(MobEntity mob) {}
    }
}
