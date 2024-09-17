package dev.sterner.common.item.focus

import com.sammy.malum.registry.client.ParticleRegistry
import dev.sterner.common.entity.BoltEntity
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.ProjectileUtil
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import team.lodestar.lodestone.handlers.RenderHandler
import team.lodestar.lodestone.helpers.RandomHelper
import team.lodestar.lodestone.registry.common.particle.LodestoneParticleRegistry
import team.lodestar.lodestone.systems.easing.Easing
import team.lodestar.lodestone.systems.particle.builder.WorldParticleBuilder
import team.lodestar.lodestone.systems.particle.data.GenericParticleData
import team.lodestar.lodestone.systems.particle.data.color.ColorParticleData
import team.lodestar.lodestone.systems.particle.data.spin.SpinParticleData
import team.lodestar.lodestone.systems.particle.render_types.LodestoneWorldParticleRenderType
import team.lodestar.lodestone.systems.particle.world.behaviors.components.DirectionalBehaviorComponent
import java.awt.Color
import kotlin.math.pow

class ShockFocusItem(properties: Properties) : AbstractFocusItem(properties) {

    override fun color(): Color = Color(155, 255, 255)

    override fun endColor(): Color = Color(50, 125, 203)

    private var cooldown = 0

    fun onUsingAbilityTick(stack: ItemStack, level: Level, player: Player) {
        val distance: Double = getMaxDistance().pow(2.0)
        val vec3d: Vec3 = player.getEyePosition(1f)
        val vec3d2: Vec3 = player.getViewVector(1f)
        val vec3d3: Vec3 =
            vec3d.add(vec3d2.x * getMaxDistance(), vec3d2.y * getMaxDistance(), vec3d2.z * getMaxDistance())
        val blockHit: HitResult = level
            .clip(
                ClipContext(
                    vec3d,
                    vec3d3,
                    ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE,
                    player
                )
            )
        val hit: EntityHitResult? = ProjectileUtil.getEntityHitResult(
            level, player, vec3d, vec3d3,
            player.boundingBox.expandTowards(vec3d2.multiply(distance, distance, distance)).inflate(1.0, 1.0, 1.0)
        ) { target -> !target.isSpectator }

        if (!level.isClientSide && blockHit.location != null && cooldown <= 0) {
            val bolt = BoltEntity(
                player,
                (if (hit?.entity != null) hit.entity.distanceTo(player)
                else blockHit.location.distanceTo(player.position())).toDouble()
            )
            level.addFreshEntity(bolt)
            cooldown = 4
        }

        if (!level.isClientSide) {
            if (cooldown > 0) {
                cooldown--
            }
        }

        if (hit != null && blockHit.distanceTo(player) > hit.entity.distanceTo(player)) {
            val hitEntity = hit.entity

            hitEntity.hurt(player.damageSources().magic(), 2f)
        }
    }

    private fun getMaxDistance(): Double {
        return 24.0
    }

    companion object {

        fun spawnChargeParticles(
            pLevel: Level,
            pLivingEntity: Entity,
            pos: Vec3,
            pct: Float
        ) {
            if (!pLevel.isClientSide) {
                return
            }
            val random = pLevel.random

            val spinData = SpinParticleData.createRandomDirection(random, 0.25f, 0.5f)
                .setSpinOffset(RandomHelper.randomBetween(random, 0f, 6.28f)).build()

            WorldParticleBuilder.create(
                LodestoneParticleRegistry.WISP_PARTICLE,
                DirectionalBehaviorComponent(pLivingEntity.lookAngle.normalize())
            )
                .setRenderTarget(RenderHandler.LATE_DELAYED_RENDER)
                .setTransparencyData(
                    GenericParticleData.create(0.75f * pct, 0f).setEasing(Easing.SINE_IN_OUT, Easing.SINE_IN).build()
                )
                .setScaleData(GenericParticleData.create(0.1f * pct, 0f).setEasing(Easing.SINE_IN_OUT).build())
                .setSpinData(spinData)
                .setColorData(ColorParticleData.create(Color(190, 196, 250)).build())
                .setLifetime(9)
                .setMotion(pLivingEntity.lookAngle.normalize().scale(0.05))
                .enableNoClip()
                .enableForcedSpawn()
                .setLifeDelay(2)
                .spawn(pLevel, pos.x, pos.y, pos.z)
                .setRenderType(LodestoneWorldParticleRenderType.ADDITIVE)
                .spawn(pLevel, pos.x, pos.y, pos.z)

            WorldParticleBuilder.create(
                ParticleRegistry.RITUAL_CIRCLE_WISP,
                DirectionalBehaviorComponent(pLivingEntity.lookAngle.normalize())
            )
                .setRenderTarget(RenderHandler.LATE_DELAYED_RENDER)
                .setTransparencyData(
                    GenericParticleData.create(0.95f * pct, 0f).setEasing(Easing.SINE_IN_OUT, Easing.SINE_IN).build()
                )
                .setScaleData(GenericParticleData.create(0.25f * pct, 0f).setEasing(Easing.SINE_IN_OUT).build())
                .setSpinData(spinData)
                .setColorData(ColorParticleData.create(Color(190, 196, 250)).build())
                .setLifetime(5)
                .setMotion(pLivingEntity.lookAngle.normalize().scale(0.05))
                .enableNoClip()
                .enableForcedSpawn()
                .setLifeDelay(2)
                .spawn(pLevel, pos.x, pos.y, pos.z)
                .setRenderType(LodestoneWorldParticleRenderType.ADDITIVE)
                .spawn(pLevel, pos.x, pos.y, pos.z)
        }
    }
}