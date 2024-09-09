package dev.sterner.registry

import com.mojang.blaze3d.platform.InputConstants
import dev.sterner.client.screen.FocusSelectionScreen
import dev.sterner.common.item.WandItem
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft

object VoidBoundKeyBindings {

    private var focusKeyBind: KeyMapping? = KeyBindingHelper.registerKeyBinding(
        KeyMapping("key.voidbound.focus_select", InputConstants.KEY_F, "category.voidbound")
    )

    fun init() {
        ClientTickEvents.END_CLIENT_TICK.register(::listenFocusKey)
    }

    private fun listenFocusKey(minecraft: Minecraft) {
        if (minecraft.player != null) {
            if (focusKeyBind?.isDown == true) {
                if (minecraft.screen == null && minecraft.player!!.mainHandItem.item is WandItem) {
                    minecraft.setScreen(FocusSelectionScreen(minecraft.player!!))
                }
            }
        }
    }
}