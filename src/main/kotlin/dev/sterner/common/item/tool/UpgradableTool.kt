package dev.sterner.common.item.tool

import net.minecraft.world.item.ItemStack

interface UpgradableTool {

    fun setNetherited(stack: ItemStack) {
        stack.orCreateTag.putBoolean("Netherited", true)
    }

    fun getNetherited(stack: ItemStack): Boolean {
        return stack.tag?.getBoolean("Netherited") == true
    }

    fun getExtraDamage(stack: ItemStack): Float {
        return if (getNetherited(stack)) 1f else 0f
    }

    fun getExtraMiningSpeed(stack: ItemStack): Float {
        return if (getNetherited(stack)) 1f else 0f
    }

    fun isFireproof(stack: ItemStack): Boolean {
        return getNetherited(stack)
    }
}