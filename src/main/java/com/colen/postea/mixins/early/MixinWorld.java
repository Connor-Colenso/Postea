package com.colen.postea.mixins.early;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.BlockSnapshot;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(World.class)
public abstract class MixinWorld {

    @Shadow
    private boolean isRemote;

    @Shadow
    private Profiler theProfiler;

    @Shadow(remap = false)
    public boolean captureBlockSnapshots;

    @Shadow(remap = false)
    public ArrayList<BlockSnapshot> capturedBlockSnapshots;

    @Shadow
    public abstract Chunk getChunkFromChunkCoords(int chunkX, int chunkZ);

    @Shadow(remap = false)
    public abstract void func_147451_t(int x, int y, int z);

    @Shadow(remap = false)
    public abstract void markAndNotifyBlock(int x, int y, int z, Chunk chunk, Block block1, Block blockIn, int flags);

    /**
     * @author t
     * @reason t
     */
    @Overwrite
    public boolean setBlock(int x, int y, int z, Block blockIn, int metadataIn, int flags) {
        System.out.println("TESTING1234");
        if (x >= -30000000 && z >= -30000000 && x < 30000000 && z < 30000000) {
            if (y < 0) {
                return false;
//            } else if (y >= 256) {
//                return false;
            } else {
                Chunk chunk = this.getChunkFromChunkCoords(x >> 4, z >> 4);
                Block block1 = null;
                net.minecraftforge.common.util.BlockSnapshot blockSnapshot = null;

                if ((flags & 1) != 0) {
                    block1 = chunk.getBlock(x & 15, y, z & 15);
                }

                if (this.captureBlockSnapshots && !this.isRemote) {
                    blockSnapshot = net.minecraftforge.common.util.BlockSnapshot.getBlockSnapshot((World) (Object) this, x, y, z, flags);
                    this.capturedBlockSnapshots.add(blockSnapshot);
                }

                boolean flag = chunk.func_150807_a(x & 15, y, z & 15, blockIn, metadataIn);

                if (!flag && blockSnapshot != null) {
                    this.capturedBlockSnapshots.remove(blockSnapshot);
                    blockSnapshot = null;
                }

                this.theProfiler.startSection("checkLight");
                this.func_147451_t(x, y, z);
                this.theProfiler.endSection();

                if (flag && blockSnapshot == null) // Don't notify clients or update physics while capturing blockstates
                {
                    // Modularize client and physic updates
                    this.markAndNotifyBlock(x, y, z, chunk, block1, blockIn, flags);
                }

                return flag;
            }
        } else {
            return false;
        }
    }

}
