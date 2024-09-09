package dev.sterner.common.worldgen

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.registry.VoidBoundStructureRegistry
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.ExtraCodecs
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.level.levelgen.WorldGenerationContext
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider
import net.minecraft.world.level.levelgen.structure.Structure
import net.minecraft.world.level.levelgen.structure.StructureType
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool
import java.util.*

@Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
class EldritchObeliskStructure(
    settings: StructureSettings,
    private val startPool: Holder<StructureTemplatePool>,
    private val startJigsawName: Optional<ResourceLocation>,
    private val maxDepth: Int,
    private val startHeight: HeightProvider,
    private val useExpansionHack: Boolean,
    private val projectStartToHeightmap: Optional<Heightmap.Types>,
    private val maxDistanceFromCenter: Int
) : Structure(settings) {

    public override fun findGenerationPoint(context: GenerationContext): Optional<GenerationStub> {
        val chunkPos = context.chunkPos()
        val i = startHeight.sample(
            context.random(),
            WorldGenerationContext(context.chunkGenerator(), context.heightAccessor())
        )
        val blockPos = BlockPos(chunkPos.minBlockX, i, chunkPos.minBlockZ)
        return JigsawPlacement.addPieces(
            context,
            this.startPool,
            this.startJigsawName,
            this.maxDepth, blockPos,
            this.useExpansionHack,
            this.projectStartToHeightmap,
            this.maxDistanceFromCenter
        )
    }

    override fun type(): StructureType<*> {
        return VoidBoundStructureRegistry.ELDRITCH_OBELISK.get()
    }

    companion object {
        const val MAX_TOTAL_STRUCTURE_RANGE: Int = 128
        val CODEC: Codec<EldritchObeliskStructure> = ExtraCodecs.validate(
            RecordCodecBuilder.mapCodec { instance: RecordCodecBuilder.Instance<EldritchObeliskStructure> ->
                instance.group<StructureSettings, Holder<StructureTemplatePool>, Optional<ResourceLocation>, Int, HeightProvider, Boolean, Optional<Heightmap.Types>, Int>(
                    settingsCodec(instance),
                    StructureTemplatePool.CODEC.fieldOf("start_pool")
                        .forGetter { jigsawStructure: EldritchObeliskStructure -> jigsawStructure.startPool },
                    ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name")
                        .forGetter { jigsawStructure: EldritchObeliskStructure -> jigsawStructure.startJigsawName },
                    Codec.intRange(0, 7).fieldOf("size")
                        .forGetter { jigsawStructure: EldritchObeliskStructure -> jigsawStructure.maxDepth },
                    HeightProvider.CODEC.fieldOf("start_height")
                        .forGetter { jigsawStructure: EldritchObeliskStructure -> jigsawStructure.startHeight },
                    Codec.BOOL.fieldOf("use_expansion_hack")
                        .forGetter { jigsawStructure: EldritchObeliskStructure -> jigsawStructure.useExpansionHack },
                    Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap")
                        .forGetter { jigsawStructure: EldritchObeliskStructure -> jigsawStructure.projectStartToHeightmap },
                    Codec.intRange(1, MAX_TOTAL_STRUCTURE_RANGE).fieldOf("max_distance_from_center")
                        .forGetter { jigsawStructure: EldritchObeliskStructure -> jigsawStructure.maxDistanceFromCenter }
                )
                    .apply(
                        instance
                    ) { settings: StructureSettings, startPool: Holder<StructureTemplatePool>, startJigsawName: Optional<ResourceLocation>, maxDepth: Int, startHeight: HeightProvider, useExpansionHack: Boolean, projectStartToHeightmap: Optional<Heightmap.Types>, maxDistanceToCenter: Int ->
                        EldritchObeliskStructure(
                            settings, startPool, startJigsawName,
                            maxDepth, startHeight,
                            useExpansionHack, projectStartToHeightmap,
                            maxDistanceToCenter
                        )
                    }
            }
        ) { structure: EldritchObeliskStructure ->
            verifyRange(
                structure
            )
        }
            .codec()

        private fun verifyRange(structure: EldritchObeliskStructure): DataResult<EldritchObeliskStructure> {
            val i = when (structure.terrainAdaptation()) {
                TerrainAdjustment.NONE -> 0
                TerrainAdjustment.BURY, TerrainAdjustment.BEARD_THIN, TerrainAdjustment.BEARD_BOX -> 12
            }
            return if (structure.maxDistanceFromCenter + i > MAX_TOTAL_STRUCTURE_RANGE
            ) DataResult.error { "Structure size including terrain adaptation must not exceed 128" }
            else DataResult.success(structure)
        }
    }
}
