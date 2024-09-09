package dev.sterner.common.worldgen

import com.mojang.serialization.Codec
import dev.sterner.registry.VoidBoundBlockRegistry
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.levelgen.feature.Feature
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration

class TearOfEnderFeature(codec: Codec<NoneFeatureConfiguration?>) :
    Feature<NoneFeatureConfiguration?>(codec) {

    /**
     * Generates at the bottom of end islands on the roof
     */
    override fun place(context: FeaturePlaceContext<NoneFeatureConfiguration?>): Boolean {
        val worldGenLevel = context.level()
        val blockPos = context.origin()

        if (worldGenLevel.isEmptyBlock(blockPos) && worldGenLevel.getBlockState(blockPos.above())
                .`is`(Blocks.END_STONE)
        ) {
            worldGenLevel.setBlock(blockPos, (VoidBoundBlockRegistry.TEAR_OF_ENDER.get()).defaultBlockState(), 2)
            return true
        } else {
            return false
        }
    }
}