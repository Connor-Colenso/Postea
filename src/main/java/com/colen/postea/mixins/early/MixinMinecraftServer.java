package com.colen.postea.mixins.early;

import net.minecraft.server.MinecraftServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {

    @Shadow
    private int buildLimit;

    /**
     * @author t
     * @reason t
     */
    @Overwrite
    public void setBuildLimit(int maxBuildHeight) {
        this.buildLimit = 320;
    }

}
