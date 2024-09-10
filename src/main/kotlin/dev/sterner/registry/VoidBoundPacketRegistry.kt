package dev.sterner.registry

import dev.sterner.VoidBound
import dev.sterner.networking.*
import io.github.fabricators_of_create.porting_lib.util.EnvExecutor
import me.pepperbell.simplenetworking.SimpleChannel
import net.fabricmc.api.EnvType

object VoidBoundPacketRegistry {

    val VOID_BOUND_CHANNEL: SimpleChannel = SimpleChannel(VoidBound.id("main"))

    fun registerVoidBoundPackets() {
        VOID_BOUND_CHANNEL.initServerListener()
        EnvExecutor.runWhenOn(
            EnvType.CLIENT
        ) { Runnable { VOID_BOUND_CHANNEL.initClientListener() } }

        var index = 0
        VOID_BOUND_CHANNEL.registerS2CPacket(
            SpiritBinderParticlePacket::class.java, index++
        )
        VOID_BOUND_CHANNEL.registerS2CPacket(
            CultistRiftParticlePacket::class.java, index++
        )

        VOID_BOUND_CHANNEL.registerS2CPacket(
            HeartParticlePacket::class.java, index++
        )
        VOID_BOUND_CHANNEL.registerS2CPacket(
            BubbleParticlePacket::class.java, index++
        )
        VOID_BOUND_CHANNEL.registerS2CPacket(
            UpdateSpiritAmountPacket::class.java, index++
        )
        VOID_BOUND_CHANNEL.registerS2CPacket(
            AxeOfTheStreamParticlePacket::class.java, index++
        )

        //C2S
        VOID_BOUND_CHANNEL.registerC2SPacket(
            EnchantmentLevelPacket::class.java, index++
        )
        VOID_BOUND_CHANNEL.registerC2SPacket(
            StartEnchantingPacket::class.java, index++
        )
        VOID_BOUND_CHANNEL.registerC2SPacket(
            SelectFocusPacket::class.java, index++
        )
        VOID_BOUND_CHANNEL.registerC2SPacket(
            ExcavationPacket::class.java, index++
        )
        VOID_BOUND_CHANNEL.registerC2SPacket(
            AbilityUpdatePacket::class.java, index++
        )
        VOID_BOUND_CHANNEL.registerC2SPacket(
            UnlockAbilityPacket::class.java, index++
        )
    }
}