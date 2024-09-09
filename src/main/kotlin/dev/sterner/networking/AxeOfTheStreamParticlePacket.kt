package dev.sterner.networking

import com.sammy.malum.registry.common.SpiritTypeRegistry
import com.sammy.malum.visual_effects.SpiritLightSpecs
import dev.sterner.api.util.VoidBoundPosUtils
import me.pepperbell.simplenetworking.SimpleChannel
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientPacketListener
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderGetter
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtUtils
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import team.lodestar.lodestone.systems.network.LodestoneClientNBTPacket
import team.lodestar.lodestone.systems.particle.ParticleEffectSpawner

class AxeOfTheStreamParticlePacket(data: CompoundTag) : LodestoneClientNBTPacket(data) {

    constructor(buf: FriendlyByteBuf) : this(buf.readNbt()!!)

    constructor(state: BlockState, pos: BlockPos) : this(createTag(state, pos))

    override fun executeClient(
        client: Minecraft,
        listener: ClientPacketListener?,
        responseSender: PacketSender?,
        channel: SimpleChannel?
    ) {
        if (client.level != null) {
            val level = client.level!!

            val holderGetter =
                (if (client.level != null) level.holderLookup(Registries.BLOCK) else BuiltInRegistries.BLOCK.asLookup()) as HolderGetter<Block?>

            val state = NbtUtils.readBlockState(holderGetter, data.getCompound("BlockState"))
            val pos = NbtUtils.readBlockPos(data.getCompound("BlockPos"))

            val coordPos = VoidBoundPosUtils.getFaceCoords(level, state, pos)
            for (i in coordPos) {
                val lightSpecs: ParticleEffectSpawner =
                    SpiritLightSpecs.spiritLightSpecs(level, i, SpiritTypeRegistry.AQUEOUS_SPIRIT)
                lightSpecs.builder.multiplyLifetime(1.5f)
                lightSpecs.bloomBuilder.multiplyLifetime(1.5f)
                lightSpecs.spawnParticles()
                lightSpecs.spawnParticles()
            }
        }
    }

    companion object {
        private fun createTag(state: BlockState, pos: BlockPos): CompoundTag {
            val tag = CompoundTag()

            tag.put("BlockState", NbtUtils.writeBlockState(state))
            tag.put("BlockPos", NbtUtils.writeBlockPos(pos))

            return tag
        }
    }
}