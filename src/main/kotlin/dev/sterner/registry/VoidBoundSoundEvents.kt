package dev.sterner.registry

import dev.sterner.VoidBound
import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar
import io.github.fabricators_of_create.porting_lib.util.RegistryObject
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.sounds.SoundEvent

object VoidBoundSoundEvents {

    val SOUNDS = LazyRegistrar.create(BuiltInRegistries.SOUND_EVENT, VoidBound.modid)


    val SOUL_SPEAK: RegistryObject<SoundEvent> =
        register(SoundEvent.createVariableRangeEvent(VoidBound.id("soul_speak")))

    fun register(soundEvent: SoundEvent): RegistryObject<SoundEvent> {
        return SOUNDS.register(
            soundEvent.location.path
        ) { soundEvent }
    }

}