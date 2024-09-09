package dev.sterner.networking

import dev.sterner.api.rift.SimpleSpiritCharge
import dev.sterner.common.blockentity.OsmoticEnchanterBlockEntity
import me.pepperbell.simplenetworking.SimpleChannel
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientPacketListener
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import team.lodestar.lodestone.systems.network.LodestoneClientNBTPacket

class UpdateSpiritAmountPacket(nbt: CompoundTag) : LodestoneClientNBTPacket(nbt) {

    constructor(buf: FriendlyByteBuf) : this(buf.readNbt()!!)

    constructor(simpleSpiritCharge: SimpleSpiritCharge, asLong: Long) : this(createTag(simpleSpiritCharge, asLong))

    override fun executeClientNbt(
        client: Minecraft,
        listener: ClientPacketListener?,
        responseSender: PacketSender?,
        channel: SimpleChannel?,
        data: CompoundTag
    ) {
        if (client.level != null) {
            val pos = BlockPos.of(data.getLong("Pos"))
            var spiritCharge = SimpleSpiritCharge()
            spiritCharge = spiritCharge.deserializeNBT(data)

            if (client.level!!.getBlockEntity(pos) is OsmoticEnchanterBlockEntity) {
                val be = client.level!!.getBlockEntity(pos) as OsmoticEnchanterBlockEntity
                be.spiritsToConsume = spiritCharge
            }
        }
    }

    companion object {
        private fun createTag(simpleSpiritCharge: SimpleSpiritCharge, asLong: Long): CompoundTag {
            val tag = CompoundTag()
            tag.putLong("Pos", asLong)
            simpleSpiritCharge.serializeNBT(tag)
            return tag
        }
    }
}