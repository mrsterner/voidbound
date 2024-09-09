package dev.sterner.networking

import dev.sterner.registry.VoidBoundParticleTypeRegistry
import me.pepperbell.simplenetworking.SimpleChannel
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientPacketListener
import net.minecraft.network.FriendlyByteBuf
import org.joml.Vector3f
import team.lodestar.lodestone.handlers.RenderHandler
import team.lodestar.lodestone.helpers.RandomHelper
import team.lodestar.lodestone.systems.easing.Easing
import team.lodestar.lodestone.systems.network.LodestoneClientPacket
import team.lodestar.lodestone.systems.particle.builder.WorldParticleBuilder
import team.lodestar.lodestone.systems.particle.data.GenericParticleData
import team.lodestar.lodestone.systems.particle.render_types.LodestoneWorldParticleRenderType

class HeartParticlePacket(private val vector3f: Vector3f) : LodestoneClientPacket() {

    constructor(buf: FriendlyByteBuf) : this(buf.readVector3f())

    override fun executeClient(
        client: Minecraft,
        listener: ClientPacketListener?,
        responseSender: PacketSender?,
        channel: SimpleChannel?
    ) {
        if (client.level != null) {
            val scaleData =
                GenericParticleData.create(0.1f, RandomHelper.randomBetween(client.level!!.random, 1.7f, 1.8f), 0.5f)
                    .setEasing(Easing.SINE_OUT, Easing.SINE_IN)
                    .setCoefficient(RandomHelper.randomBetween(client.level!!.random, 1f, 1.25f))
                    .build()

            WorldParticleBuilder.create(VoidBoundParticleTypeRegistry.HEAL_PARTICLE.get())
                .setRenderTarget(RenderHandler.LATE_DELAYED_RENDER)
                .setTransparencyData(
                    GenericParticleData.create(0.9f, 0.05f, 0f).setEasing(Easing.CUBIC_OUT, Easing.EXPO_IN).build()
                )
                .setScaleData(scaleData)
                .setLifetime(20)
                .setLifeDelay(2)
                .enableNoClip()
                .enableForcedSpawn()
                .setRenderType(LodestoneWorldParticleRenderType.ADDITIVE)
                .spawn(client.level, vector3f.x.toDouble(), vector3f.y.toDouble(), vector3f.z.toDouble())
        }
    }

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeVector3f(vector3f)
    }

}