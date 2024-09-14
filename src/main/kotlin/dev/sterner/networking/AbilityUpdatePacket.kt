package dev.sterner.networking

import dev.sterner.api.item.ItemAbility
import dev.sterner.api.util.VoidBoundItemUtils
import me.pepperbell.simplenetworking.SimpleChannel
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.network.ServerGamePacketListenerImpl
import net.minecraft.world.entity.EquipmentSlot
import team.lodestar.lodestone.systems.network.LodestoneServerPacket
import java.util.*

class AbilityUpdatePacket(val uuid: UUID, val itemAbility: ItemAbility, val helm: Boolean) : LodestoneServerPacket() {

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeUUID(uuid)
        buf.writeUtf(itemAbility.name)
        buf.writeBoolean(helm)
    }

    constructor(buf: FriendlyByteBuf) : this(buf.readUUID(), ItemAbility.valueOf(buf.readUtf()), buf.readBoolean())

    override fun executeServer(
        server: MinecraftServer?,
        player: ServerPlayer?,
        listener: ServerGamePacketListenerImpl?,
        responseSender: PacketSender?,
        channel: SimpleChannel?
    ) {
        server?.execute {
            if (player?.uuid == uuid) {
                val item = if (helm) player.getItemBySlot(EquipmentSlot.HEAD) else player.mainHandItem
                VoidBoundItemUtils.addItemAbility(item, itemAbility, true)
                VoidBoundItemUtils.setActiveAbility(item, itemAbility)
            }
        }
    }
}