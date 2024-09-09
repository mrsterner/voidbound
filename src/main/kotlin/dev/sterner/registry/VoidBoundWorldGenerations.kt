package dev.sterner.registry

import dev.sterner.VoidBound
import dev.sterner.common.worldgen.TearOfBrimstoneFeature
import dev.sterner.common.worldgen.TearOfEnderFeature
import net.fabricmc.fabric.api.biome.v1.BiomeModifications
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.levelgen.GenerationStep
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration
import net.minecraft.world.level.levelgen.placement.PlacedFeature

object VoidBoundWorldGenerations {

    val TEAR_OF_ENDER = TearOfEnderFeature(NoneFeatureConfiguration.CODEC)
    val TEAR_OF_ENDER_ID = VoidBound.id("tear_of_ender")
    val TEAR_OF_ENDER_FEATURE: ResourceKey<PlacedFeature> = registerKey("tear_of_ender")

    val TEAR_OF_CRIMSON = TearOfBrimstoneFeature(NoneFeatureConfiguration.CODEC)
    val TEAR_OF_CRIMSON_ID = VoidBound.id("tear_of_brimstone")
    val TEAR_OF_CRIMSON_FEATURE: ResourceKey<PlacedFeature> = registerKey("tear_of_brimstone")

    private fun registerKey(name: String): ResourceKey<PlacedFeature> {
        return ResourceKey.create(Registries.PLACED_FEATURE, VoidBound.id(name))
    }

    fun init() {
        Registry.register(BuiltInRegistries.FEATURE, TEAR_OF_ENDER_ID, TEAR_OF_ENDER)
        Registry.register(BuiltInRegistries.FEATURE, TEAR_OF_CRIMSON_ID, TEAR_OF_CRIMSON)

        BiomeModifications.addFeature(
            BiomeSelectors.foundInTheEnd(),
            GenerationStep.Decoration.VEGETAL_DECORATION,
            TEAR_OF_ENDER_FEATURE
        )

        BiomeModifications.addFeature(
            BiomeSelectors.foundInTheNether(),
            GenerationStep.Decoration.VEGETAL_DECORATION,
            TEAR_OF_CRIMSON_FEATURE
        )
    }

}