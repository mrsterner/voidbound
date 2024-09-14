package dev.sterner.registry

import dev.sterner.client.renderer.blockentity.*
import dev.sterner.client.renderer.entity.*
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers

object VoidBoundEntityRenderers {

    fun init() {

        BlockEntityRenderers.register(
            VoidBoundBlockEntityTypeRegistry.ELDRITCH_OBELISK.get(),
            ::EldritchObeliskBlockEntityRenderer
        )
        BlockEntityRenderers.register(
            VoidBoundBlockEntityTypeRegistry.SPIRIT_BINDER.get(),
            ::SpiritBinderBlockEntityRenderer
        )
        BlockEntityRenderers.register(
            VoidBoundBlockEntityTypeRegistry.SPIRIT_BINDER_STABILIZER.get(),
            ::SpiritStabilizerBlockEntityRenderer
        )
        BlockEntityRenderers.register(
            VoidBoundBlockEntityTypeRegistry.SPIRIT_RIFT.get(),
            ::SpiritRiftBlockEntityRenderer
        )
        BlockEntityRenderers.register(
            VoidBoundBlockEntityTypeRegistry.PORTABLE_HOLE.get(),
            ::PortableHoleBlockEntityRenderer
        )
        BlockEntityRenderers.register(
            VoidBoundBlockEntityTypeRegistry.OSMOTIC_ENCHANTER.get(),
            ::OsmoticEnchanterBlockEntityRenderer
        )

        EntityRendererRegistry.register(
            VoidBoundEntityTypeRegistry.PARTICLE_ENTITY.get(),
            ::ParticleEntityRenderer
        )
        EntityRendererRegistry.register(
            VoidBoundEntityTypeRegistry.SOUL_STEEL_GOLEM_ENTITY.get(),
            ::SoulSteelGolemEntityRenderer
        )
        EntityRendererRegistry.register(
            VoidBoundEntityTypeRegistry.GRIMCULT_KNIGHT_ENTITY.get(),
            ::GrimcultKnightEntityRenderer
        )
        EntityRendererRegistry.register(
            VoidBoundEntityTypeRegistry.GRIMCULT_ARCHER_ENTITY.get(),
            ::GrimcultArcherEntityRenderer
        )
        EntityRendererRegistry.register(
            VoidBoundEntityTypeRegistry.GRIMCULT_CLERIC_ENTITY.get(),
            ::GrimcultClericEntityRenderer
        )
        EntityRendererRegistry.register(
            VoidBoundEntityTypeRegistry.GRIMCULT_NECROMANCER_ENTITY.get(),
            ::GrimcultNecromancerEntityRenderer
        )
        EntityRendererRegistry.register(
            VoidBoundEntityTypeRegistry.GRIMCULT_JESTER_ENTITY.get(),
            ::GrimcultJesterEntityRenderer
        )
        EntityRendererRegistry.register(
            VoidBoundEntityTypeRegistry.GRIMCULT_HEAVY_KNIGHT_ENTITY.get(),
            ::GrimcultHeavyKnightEntityRenderer
        )
        EntityRendererRegistry.register(
            VoidBoundEntityTypeRegistry.BOLT_ENTITY.get(),
            ::BoltEntityRenderer
        )
    }
}