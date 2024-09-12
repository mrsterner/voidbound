package dev.sterner.common.item.focus

import com.sammy.malum.core.systems.spirit.MalumSpiritType
import com.sammy.malum.registry.client.ParticleRegistry
import com.sammy.malum.registry.common.SpiritTypeRegistry
import com.sammy.malum.visual_effects.SpiritLightSpecs
import dev.sterner.api.util.VoidBoundPlayerUtils
import dev.sterner.api.util.VoidBoundPosUtils
import dev.sterner.api.wand.IWandFocus
import dev.sterner.mixin_logic.ParticleEngineMixinLogic
import dev.sterner.networking.ExcavationPacket
import dev.sterner.registry.VoidBoundPacketRegistry
import net.minecraft.client.Minecraft
import net.minecraft.sounds.SoundSource
import net.minecraft.util.RandomSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import team.lodestar.lodestone.handlers.RenderHandler
import team.lodestar.lodestone.helpers.RandomHelper
import team.lodestar.lodestone.registry.common.particle.LodestoneParticleRegistry
import team.lodestar.lodestone.systems.easing.Easing
import team.lodestar.lodestone.systems.particle.ParticleEffectSpawner
import team.lodestar.lodestone.systems.particle.builder.WorldParticleBuilder
import team.lodestar.lodestone.systems.particle.data.GenericParticleData
import team.lodestar.lodestone.systems.particle.data.spin.SpinParticleData
import team.lodestar.lodestone.systems.particle.render_types.LodestoneWorldParticleRenderType
import team.lodestar.lodestone.systems.particle.world.behaviors.components.DirectionalBehaviorComponent
import kotlin.math.cos
import kotlin.math.sin


class ExcavationFocus : IWandFocus {

    private var timeToBreak: Float? = null
    private var breakTime: Int = 0
    private var breakProgress: Int = -1
    private var blockState: BlockState? = null

    override fun onUsingFocusTick(stack: ItemStack, level: Level, player: Player) {
        val maxReach = 10.0
        val tickDelta = 1.0f
        val includeFluids = false

        val hit: HitResult? = player.pick(maxReach, tickDelta, includeFluids)

        if (hit != null) {
            if (hit.type == HitResult.Type.BLOCK) {
                val blockHit = hit as BlockHitResult
                val blockPos = blockHit.blockPos
                val newState = level.getBlockState(blockPos) ?: return

                if (blockState != newState) {
                    this.breakTime = 0
                    this.breakProgress = -1
                }

                blockState = newState


                val pos = getProjectileSpawnPos(player, InteractionHand.MAIN_HAND, 1.5f, 0.6f)
                if (level.isClientSide) {
                    spawnChargeParticles(player.level(), player, pos, 0.5f)
                    spec(level, player.lookAngle.normalize(), pos, SpiritTypeRegistry.EARTHEN_SPIRIT, level.random)
                }

                if (!VoidBoundPlayerUtils.canPlayerBreakBlock(level, player, blockPos)) {
                    ParticleEngineMixinLogic.logic(level, blockPos, blockState!!, level.random, hit.direction)
                    return
                }

                timeToBreak = (20 * blockState!!.getDestroySpeed(level, blockPos))
                if (level.isClientSide) {
                    val coordPos: List<Vec3> = VoidBoundPosUtils.getFaceCoords(level, blockState!!, blockPos)
                    for (pos1 in coordPos) {
                        val lightSpecs: ParticleEffectSpawner =
                            SpiritLightSpecs.spiritLightSpecs(level, pos1, SpiritTypeRegistry.EARTHEN_SPIRIT)
                        lightSpecs.builder.multiplyLifetime(1.5f)
                        lightSpecs.bloomBuilder.multiplyLifetime(1.5f)

                        lightSpecs.spawnParticles()
                        lightSpecs.spawnParticles()
                    }
                }

                this.breakTime++
                val progress: Int = (this.breakTime / this.timeToBreak!!.toFloat() * 10).toInt()

                if (level.isClientSide) {
                    VoidBoundPacketRegistry.VOID_BOUND_CHANNEL.sendToServer(
                        ExcavationPacket(
                            blockPos,
                            breakTime,
                            timeToBreak!!.toInt(),
                            progress
                        )
                    )
                }

                if (breakTime % 6 == 0 && level.isClientSide) {
                    level.playSound(player, blockPos, blockState!!.soundType.breakSound, SoundSource.BLOCKS)
                }

                if (progress != this.breakProgress) {
                    this.breakProgress = progress
                }
                level.destroyBlockProgress(player.id + ExcavationPacket.generatePosHash(blockPos), blockPos, progress)

                if (this.breakTime >= this.timeToBreak!!) {
                    this.breakTime = 0
                    this.breakProgress = -1
                }
            }
        }
    }

    private fun spec(level: Level, angle: Vec3, pos: Vec3, spiritType: MalumSpiritType, random: RandomSource) {

        val lightSpecs: ParticleEffectSpawner = SpiritLightSpecs.spiritLightSpecs(level, pos, spiritType)
        lightSpecs.builder
            .multiplyLifetime(2.5f)
            .setMotion(angle.scale(0.3))
            .setTransparencyData(GenericParticleData.create(0.2f, 0.8f, 0f).build())
            .modifyData({ obj: WorldParticleBuilder -> obj.scaleData },
                { d: GenericParticleData ->
                    d.multiplyValue(
                        RandomHelper.randomBetween(
                            random,
                            1f,
                            2f
                        )
                    )
                })
        lightSpecs.bloomBuilder
            .multiplyLifetime(1.5f)
            .setMotion(angle.scale(0.3))
            .setTransparencyData(GenericParticleData.create(0.05f, 0.35f, 0f).build())
            .modifyData({ obj: WorldParticleBuilder -> obj.scaleData },
                { d: GenericParticleData ->
                    d.multiplyValue(
                        RandomHelper.randomBetween(
                            random,
                            0.5f,
                            1f
                        )
                    )
                })
        lightSpecs.spawnParticles()

    }

    private fun spawnChargeParticles(
        pLevel: Level,
        pLivingEntity: LivingEntity,
        pos: Vec3,
        pct: Float
    ) {
        val random = pLevel.random

        val spinData = SpinParticleData.createRandomDirection(random, 0.25f, 0.5f)
            .setSpinOffset(RandomHelper.randomBetween(random, 0f, 6.28f)).build()
        WorldParticleBuilder.create(
            LodestoneParticleRegistry.WISP_PARTICLE,
            DirectionalBehaviorComponent(pLivingEntity.lookAngle.normalize())
        )
            .setRenderTarget(RenderHandler.LATE_DELAYED_RENDER)
            .setTransparencyData(
                GenericParticleData.create(0.95f * pct, 0f).setEasing(Easing.SINE_IN_OUT, Easing.SINE_IN).build()
            )
            .setScaleData(GenericParticleData.create(0.35f * pct, 0f).setEasing(Easing.SINE_IN_OUT).build())
            .setSpinData(spinData)
            .setColorData(SpiritTypeRegistry.EARTHEN_SPIRIT.createColorData().build())
            .setLifetime(5)
            .setMotion(pLivingEntity.lookAngle.normalize().scale(0.05))
            .addTickActor {
                it.particleSpeed = pLivingEntity.lookAngle
            }
            .enableNoClip()
            .enableForcedSpawn()
            .setLifeDelay(2)
            .spawn(pLevel, pos.x, pos.y, pos.z)
            .setRenderType(LodestoneWorldParticleRenderType.LUMITRANSPARENT)
            .spawn(pLevel, pos.x, pos.y, pos.z)

        WorldParticleBuilder.create(
            ParticleRegistry.RITUAL_CIRCLE_WISP,
            DirectionalBehaviorComponent(pLivingEntity.lookAngle.normalize())
        )
            .setRenderTarget(RenderHandler.LATE_DELAYED_RENDER)
            .setTransparencyData(
                GenericParticleData.create(0.95f * pct, 0f).setEasing(Easing.SINE_IN_OUT, Easing.SINE_IN).build()
            )
            .setScaleData(GenericParticleData.create(0.35f * pct, 0f).setEasing(Easing.SINE_IN_OUT).build())
            .setSpinData(spinData)
            .setColorData(SpiritTypeRegistry.EARTHEN_SPIRIT.createColorData().build())
            .setLifetime(5)
            .setMotion(pLivingEntity.lookAngle.normalize().scale(0.05))
            .enableNoClip()
            .enableForcedSpawn()
            .setLifeDelay(2)
            .spawn(pLevel, pos.x, pos.y, pos.z)
            .setRenderType(LodestoneWorldParticleRenderType.LUMITRANSPARENT)
            .spawn(pLevel, pos.x, pos.y, pos.z)
    }

    private fun getProjectileSpawnPos(
        player: LivingEntity,
        hand: InteractionHand,
        distance: Float,
        spread: Float
    ): Vec3 {
        val angle = if (hand == InteractionHand.MAIN_HAND) 225 else 90
        val radians = Math.toRadians((angle - player.yHeadRot).toDouble())
        return player.position().add(player.lookAngle.scale(distance.toDouble()))
            .add(spread * sin(radians), (player.bbHeight * 0.9f).toDouble(), spread * cos(radians))
    }
}