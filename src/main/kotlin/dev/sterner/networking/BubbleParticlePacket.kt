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
import team.lodestar.lodestone.systems.particle.data.color.ColorParticleData
import team.lodestar.lodestone.systems.particle.render_types.LodestoneWorldParticleRenderType
import java.awt.Color

class BubbleParticlePacket(private val motion: Vector3f, private val vector3f: Vector3f) : LodestoneClientPacket() {

    constructor(buf: FriendlyByteBuf) : this(buf.readVector3f(), buf.readVector3f())

    override fun executeClient(
        client: Minecraft,
        listener: ClientPacketListener?,
        responseSender: PacketSender?,
        channel: SimpleChannel?
    ) {
        if (client.level != null) {
            val scaleData =
                GenericParticleData.create(0.1f, 0.0f)
                    .setEasing(Easing.SINE_OUT, Easing.SINE_IN)
                    .setCoefficient(RandomHelper.randomBetween(client.level!!.random, 1f, 1.25f))
                    .build()

            WorldParticleBuilder.create(VoidBoundParticleTypeRegistry.BUBBLE_PARTICLE.get())
                .setRenderTarget(RenderHandler.LATE_DELAYED_RENDER)
                .setTransparencyData(
                    GenericParticleData.create(0.9f, 0.5f, 0f).setEasing(Easing.CUBIC_OUT, Easing.EXPO_IN).build()
                )
                .setScaleData(scaleData)
                .setColorData(ColorParticleData.create(Color(100, 100, 255).brighter()).build())
                .setLifetime(10)
                .setLifeDelay(2)
                .enableNoClip()
                .enableForcedSpawn()
                .setRenderType(LodestoneWorldParticleRenderType.TRANSPARENT)
                .setMotion(motion)
                .spawn(client.level, vector3f.x.toDouble(), vector3f.y.toDouble(), vector3f.z.toDouble())
        }
    }

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeVector3f(motion)
        buf.writeVector3f(vector3f)
    }
}