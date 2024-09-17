package dev.sterner.common.item.equipment

import dev.sterner.api.util.VoidBoundUtils
import dev.sterner.registry.VoidBoundParticleTypeRegistry
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.util.Mth
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Tier
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import team.lodestar.lodestone.handlers.RenderHandler
import team.lodestar.lodestone.helpers.RandomHelper
import team.lodestar.lodestone.systems.easing.Easing
import team.lodestar.lodestone.systems.item.tools.magic.MagicSwordItem
import team.lodestar.lodestone.systems.particle.SimpleParticleOptions
import team.lodestar.lodestone.systems.particle.builder.WorldParticleBuilder
import team.lodestar.lodestone.systems.particle.data.GenericParticleData
import team.lodestar.lodestone.systems.particle.render_types.LodestoneWorldParticleRenderType
import java.awt.Color
import kotlin.math.cos
import kotlin.math.sin

open class GalesEdgeItem(
    material: Tier?, attackDamage: Int, attackSpeed: Float, magicDamage: Float,
    properties: Properties?
) : MagicSwordItem(
    material, attackDamage,
    attackSpeed,
    magicDamage, properties
), UpgradableTool {

    override fun getDestroySpeed(stack: ItemStack, state: BlockState): Float {
        return super.getDestroySpeed(stack, state) + getExtraMiningSpeed(stack)
    }

    override fun getUseDuration(stack: ItemStack): Int {
        return 72000
    }

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        player.startUsingItem(usedHand)
        return super.use(level, player, usedHand)
    }

    override fun onUseTick(level: Level, player: LivingEntity, stack: ItemStack, remainingUseDuration: Int) {
        ascend(player, stack, this.getUseDuration(stack) - remainingUseDuration)
        super.onUseTick(level, player, stack, remainingUseDuration)
    }

    companion object {

        fun ascend(player: LivingEntity, stack: ItemStack, ticks: Int) {
           //TODO
        }

        fun genParticleOrbit(level: Level, pos: Vec3, range: Int, direction: Int) {
            val clampedDir = Mth.clamp(direction, 1, 4)
            val discRad = (range * (1 / 3f) + level.getRandom().nextGaussian() / 5f)
            val yRand = (level.getRandom().nextGaussian() - 0.5) / 4

            val builder = WorldParticleBuilder.create(VoidBoundParticleTypeRegistry.SMOKE_PARTICLE)

            builder
                .setRenderTarget(RenderHandler.LATE_DELAYED_RENDER)
                .setTransparencyData(
                    GenericParticleData.create(0f, 0.2f, 0f).setEasing(Easing.SINE_IN, Easing.QUAD_IN)
                        .setCoefficient(3.5f).build()
                )
                .setRenderType(LodestoneWorldParticleRenderType.TRANSPARENT)
                .setGravityStrength(0f)
                .setFrictionStrength(0.98f)
                .setScaleData(GenericParticleData.create(0.425f).build())
                .setMotion(discRad, 0.01, discRad)
                .setDiscardFunction(SimpleParticleOptions.ParticleDiscardFunctionType.ENDING_CURVE_INVISIBLE)
                .addTickActor {
                    val baseSpeed = 0.3f
                    val speedFactor = 3f   // Speed factor that increases the speed of motion without affecting radius
                    val time: Float = it.age / 6f * speedFactor   // Multiply time progression by speedFactor

                    // Calculate new positions based on time, but keep the radius fixed
                    val (newX, newZ) = when (clampedDir) {
                        1 -> Pair(cos(time) * discRad, sin(time) * discRad)
                        2 -> Pair(cos(time) * discRad, -sin(time) * discRad)
                        3 -> Pair(-cos(time) * discRad, sin(time) * discRad)
                        4 -> Pair(-cos(time) * discRad, -sin(time) * discRad)
                        else -> Pair(0f, 0f)
                    }

                    it.setParticleSpeed(
                        newX.toDouble() * baseSpeed,   // Multiply by base speed for motion scaling
                        it.particleSpeed.y,
                        newZ.toDouble() * baseSpeed
                    )
                }
                .setLifetime(RandomHelper.randomBetween(level.random, 40, 80))
                .spawn(
                    level,
                    pos.x,
                    pos.y + yRand,
                    pos.z + if (direction % 2 == 0) discRad / 2 else -discRad / 2
                )
        }
    }

    override fun appendHoverText(
        stack: ItemStack,
        level: Level?,
        tooltipComponents: MutableList<Component>,
        isAdvanced: TooltipFlag
    ) {
        VoidBoundUtils.addNetheritedTooltip(stack, tooltipComponents)
        tooltipComponents.add(
            Component.translatable("Not yet implemented").withStyle(ChatFormatting.ITALIC).withStyle(
                Style.EMPTY.withColor(Color.red.rgb)
            )
        )

        super.appendHoverText(stack, level, tooltipComponents, isAdvanced)
    }
}