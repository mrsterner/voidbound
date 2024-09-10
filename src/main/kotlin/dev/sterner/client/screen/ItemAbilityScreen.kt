package dev.sterner.client.screen

import dev.sterner.api.VoidBoundApi
import dev.sterner.api.item.ItemAbility
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import kotlin.math.max

class ItemAbilityScreen(stack: ItemStack) : Screen(Component.literal("Ability Selection")) {

    var focus: Boolean = false
    private var yOffset = 0f
    private var selection: Int = 0
    private var initialized = false
    var abilities: List<ItemAbility>? = null

    private var w: Int = 0
    private var h: Int = 0

    init {
        this.minecraft = Minecraft.getInstance()
        abilities = VoidBoundApi.getItemAbility(stack)
        focus = false
        yOffset = 0f
        selection = 0
        initialized = false

        w = max((abilities!!.size * 50 + 30).toDouble(), 220.0).toInt()
        h = 30
    }

    fun cycle(direction: Int) {
        selection += if ((direction < 0)) 1 else -1
        selection = (selection + abilities!!.size) % abilities!!.size
    }

    fun update() {
        if (focus) {
            yOffset += (40 - yOffset) * .1f
        } else {
            yOffset *= .9f
        }
    }
}