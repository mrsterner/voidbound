package dev.sterner.networking

import com.sammy.malum.core.handlers.SpiritHarvestHandler
import dev.sterner.api.util.VoidBoundUtils
import me.pepperbell.simplenetworking.SimpleChannel
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientPacketListener
import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.LivingEntity
import team.lodestar.lodestone.systems.network.LodestoneClientPacket

class CultistRiftParticlePacket(private val entityId: Int, val pos: BlockPos, private val spiritType: String) :
    LodestoneClientPacket() {

    constructor(buf: FriendlyByteBuf) : this(buf.readInt(), buf.readBlockPos(), buf.readUtf())

    override fun executeClient(
        client: Minecraft,
        listener: ClientPacketListener?,
        responseSender: PacketSender?,
        channel: SimpleChannel?
    ) {
        val spirit = SpiritHarvestHandler.getSpiritType(spiritType)
        if (spirit != null && client.level != null) {
            val entity = client.level!!.getEntity(entityId)
            if (entity is LivingEntity) {
                VoidBoundUtils.spawnSpiritParticle(
                    client.level!!,
                    entity.position().add(0.0, 1.2, 0.0),
                    pos.center.add(0.5, 0.5, 0.5),
                    0f,
                    spirit,
                    false
                )
            }
        }
    }

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeInt(entityId)
        buf.writeBlockPos(pos)
        buf.writeUtf(spiritType)
    }
}