package lych.soulcraft.extension.soulpower.control.controller;

import lych.soulcraft.api.event.RegisterControlledMobBehaviorsEvent;
import net.minecraftforge.common.MinecraftForge;

import static lych.soulcraft.SoulCraft.prefix;
import static lych.soulcraft.extension.soulpower.control.controller.ControlledMobBehavior.register;

public final class DefaultControllers {
    public static final ControlledMobBehavior<AutoCapturableEnemyControl> AUTO_ENEMY_CONTROL = register(new AutoCapturableEnemyControl.Type(prefix("auto_enemy_control")).mustSee());
    public static final ControlledMobBehavior<EnemyControl> DEFAULT_ENEMY_CONTROL = register(new EnemyControl.Type(prefix("default_enemy_control")).mustSee());

    private DefaultControllers() {}

    public static void init() {
        MinecraftForge.EVENT_BUS.post(new RegisterControlledMobBehaviorsEvent());
    }
}
