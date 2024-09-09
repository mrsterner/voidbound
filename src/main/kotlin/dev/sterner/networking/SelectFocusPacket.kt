package dev.sterner.networking

import dev.sterner.common.item.WandItem
import me.pepperbell.simplenetworking.SimpleChannel
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.network.ServerGamePacketListenerImpl
import net.minecraft.world.item.ItemStack
import team.lodestar.lodestone.systems.network.LodestoneServerNBTPacket
import java.util.*

class SelectFocusPacket(data: CompoundTag?) : LodestoneServerNBTPacket(data) {

    override fun encode(buf: FriendlyByteBuf?) {
        super.encode(buf)
    }

    constructor(buf: FriendlyByteBuf) : this(buf.readNbt()!!)

    constructor(uuid: UUID, focus: ItemStack) : this(CompoundTag().apply {
        put("Focus", focus.save(CompoundTag()))
        putUUID("UUID", uuid)
    })

    override fun executeServerNbt(
        server: MinecraftServer?,
        player: ServerPlayer?,
        listener: ServerGamePacketListenerImpl?,
        responseSender: PacketSender?,
        channel: SimpleChannel?,
        data: CompoundTag
    ) {
        server?.execute {
            val focus = ItemStack.of(data.getCompound("Focus"))

            if (player!!.uuid == data.getUUID("UUID")) {
                val wandItem = player.mainHandItem.item as WandItem
                wandItem.updateSelectedFocus(player.mainHandItem, focus)
            }
        }
    }
}