package me.jellysquid.mods.lithium.mixin.world.chunk_inline_block_access;

import me.jellysquid.mods.lithium.common.util.Pos;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = WorldChunk.class, priority = 500)
public abstract class WorldChunkMixin implements Chunk {
    private static final BlockState DEFAULT_BLOCK_STATE = Blocks.AIR.getDefaultState();
    private static final FluidState DEFAULT_FLUID_STATE = Fluids.EMPTY.getDefaultState();

    @Shadow
    @Final
    private ChunkSection[] sections;

    @Shadow
    @Final
    public static ChunkSection EMPTY_SECTION;

    /**
     * @reason Reduce method size to help the JVM inline
     * @author JellySquid
     */
    @Overwrite
    public BlockState getBlockState(BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        if (!this.isOutOfHeightLimit(y)) {
            ChunkSection section = this.sections[Pos.SectionYIndex.fromBlockCoord(this, y)];

            if (section != EMPTY_SECTION) {
                return section.getBlockState(x & 15, y & 15, z & 15);
            }
        }

        return DEFAULT_BLOCK_STATE;
    }

    /**
     * @reason Reduce method size to help the JVM inline
     * @author JellySquid, Maity
     */
    @Overwrite
    public FluidState getFluidState(int x, int y, int z) {
        if (!this.isOutOfHeightLimit(y)) {
            ChunkSection section = this.sections[Pos.SectionYIndex.fromBlockCoord(this, y)];

            if (section != EMPTY_SECTION) {
                return section.getFluidState(x & 15, y & 15, z & 15);
            }
        }

        return DEFAULT_FLUID_STATE;
    }
}
