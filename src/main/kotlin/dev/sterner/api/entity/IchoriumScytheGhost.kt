package dev.sterner.api.entity

/**
 * When a scythe is a ghost it won't drop or insert itself to an inventory from its entity
 */
interface IchoriumScytheGhost {
    fun isGhost(): Boolean

    fun setGhost(ghost: Boolean)
}