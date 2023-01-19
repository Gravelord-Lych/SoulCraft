package lych.soulcraft.extension.fire;

import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.fluid.Fluid;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;

public class FireProperties {
    Block fireBlock = Blocks.FIRE;
    Pair<RenderMaterial, RenderMaterial> fireOverlays = Fire.DEFAULT_FIRE_OVERLAYS;
    ITag<Fluid> lavaTag = FluidTags.LAVA;
    Fire.Handler handler = Fire.noHandlerNeeded();
    float fireDamage = 1;
    int priority = Fire.DEFAULT_PRIORITY;

    public FireProperties setBlock(Block fireBlock) {
        this.fireBlock = fireBlock;
        return this;
    }

    public FireProperties useOverlays(RenderMaterial fire0, RenderMaterial fire1) {
        return useOverlays(Pair.of(fire0, fire1));
    }

    public FireProperties useOverlays(Pair<RenderMaterial, RenderMaterial> fireOverlays) {
        this.fireOverlays = fireOverlays;
        return this;
    }

    public FireProperties withLava(ITag<Fluid> lavaTag) {
        this.lavaTag = lavaTag;
        return this;
    }

    public FireProperties handler(Fire.Handler handler) {
        this.handler = handler;
        return this;
    }

    public FireProperties withDamage(float fireDamage) {
        this.fireDamage = fireDamage;
        return this;
    }

    public FireProperties withPriority(int priority) {
        this.priority = priority;
        return this;
    }
}