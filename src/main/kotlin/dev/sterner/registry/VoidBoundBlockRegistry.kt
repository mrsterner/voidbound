package dev.sterner.registry

import dev.sterner.VoidBound
import dev.sterner.common.block.*
import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar
import io.github.fabricators_of_create.porting_lib.util.RegistryObject
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour.Properties
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.level.material.PushReaction

object VoidBoundBlockRegistry {

    val BLOCKS: LazyRegistrar<Block> = LazyRegistrar.create(BuiltInRegistries.BLOCK, VoidBound.modid)

    var SPIRIT_BINDER: RegistryObject<SpiritBinderBlock> = BLOCKS.register("spirit_binder") {
        SpiritBinderBlock(
            FabricBlockSettings.create()
                .mapColor(MapColor.STONE)
                .requiresCorrectToolForDrops()
                .strength(1.5F, 8.0F)
        ).setBlockEntity {
            VoidBoundBlockEntityTypeRegistry.SPIRIT_BINDER.get()
        } as SpiritBinderBlock
    }

    var SPIRIT_STABILIZER: RegistryObject<SpiritBinderStabilizerBlock> = BLOCKS.register("spirit_stabilizer") {
        SpiritBinderStabilizerBlock(
            FabricBlockSettings.create()
                .mapColor(MapColor.STONE)
                .requiresCorrectToolForDrops()
                .strength(1.5F, 8.0F)
        )
    }

    var SPIRIT_RIFT: RegistryObject<SpiritRiftBlock> = BLOCKS.register("spirit_rift") {
        SpiritRiftBlock(Properties.of()).setBlockEntity {
            VoidBoundBlockEntityTypeRegistry.SPIRIT_RIFT.get()
        } as SpiritRiftBlock
    }

    var GRIMCULT_RITES: RegistryObject<GrimcultRitesBlock> = BLOCKS.register("grimcult_rites") {
        GrimcultRitesBlock(Properties.of())
    }

    var PORTABLE_HOLE: RegistryObject<PortableHoleBlock> = BLOCKS.register("portable_hole") {
        PortableHoleBlock(
            FabricBlockSettings.create()
                .luminance(5)
                .noCollission()
                .strength(-1.0F, 3600000.0F)
                .noLootTable()
                .pushReaction(PushReaction.BLOCK)
        ).setBlockEntity {
            VoidBoundBlockEntityTypeRegistry.PORTABLE_HOLE.get()
        } as PortableHoleBlock
    }

    var OSMOTIC_ENCHANTER: RegistryObject<OsmoticEnchanterBlock> = BLOCKS.register("osmotic_enchanter") {
        OsmoticEnchanterBlock(
            FabricBlockSettings.create()
                .mapColor(MapColor.STONE)
                .requiresCorrectToolForDrops()
                .strength(1.5F, 8.0F)
        )
    }

    var TEAR_OF_ENDER: RegistryObject<TearBlock> = BLOCKS.register("tear_of_ender") {
        TearBlock(TearBlock.Type.ENDER, FabricBlockSettings.create().noOcclusion())
    }

    var TEAR_OF_BRIMSTONE: RegistryObject<TearBlock> = BLOCKS.register("tear_of_brimstone") {
        TearBlock(TearBlock.Type.BRIMSTONE, FabricBlockSettings.create().noOcclusion())
    }

    val ELDRITCH_OBELISK: RegistryObject<EldritchObeliskBlock> = BLOCKS.register(
        "eldritch_obelisk"
    ) {
        EldritchObeliskBlock(
            FabricBlockSettings.create()
                .noOcclusion()
                .strength(-1.0F, 3600000.0F)
        )
            .setBlockEntity {
                VoidBoundBlockEntityTypeRegistry.ELDRITCH_OBELISK.get()
            } as EldritchObeliskBlock
    }

    val ELDRITCH_OBELISK_COMPONENT: RegistryObject<EldritchObeliskComponentBlock> = BLOCKS.register(
        "eldritch_obelisk_component"
    ) {
        EldritchObeliskComponentBlock(
            FabricBlockSettings.create()
                .noOcclusion()
                .strength(-1.0F, 3600000.0F)
                .noOcclusion()
        )
    }
}