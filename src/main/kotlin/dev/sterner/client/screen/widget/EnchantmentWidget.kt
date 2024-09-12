package dev.sterner.client.screen.widget

import com.sammy.malum.core.systems.recipe.SpiritWithCount
import dev.sterner.VoidBound
import dev.sterner.api.rift.SimpleSpiritCharge
import dev.sterner.api.util.VoidBoundPlayerUtils
import dev.sterner.api.util.VoidBoundUtils
import dev.sterner.client.screen.OsmoticEnchanterScreen
import dev.sterner.networking.EnchantmentLevelPacket
import dev.sterner.registry.VoidBoundPacketRegistry
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component
import net.minecraft.util.Mth
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.EnchantmentHelper
import java.awt.Color


open class EnchantmentWidget(var screen: OsmoticEnchanterScreen, x: Int, y: Int, width: Int, height: Int) :
    AbstractWidget(
        x, y, width, height,
        Component.empty()
    ) {

    var selected: Boolean = false
    var enchantment: Enchantment? = null

    var level: Int = 1

    override fun onClick(mouseX: Double, mouseY: Double) {

        if (screen.menu.osmoticEnchanter?.activated == true || enchantment == null) {
            return
        }

        val activatedEnchantments = screen.menu.osmoticEnchanter?.activeEnchantments
        val compatible =
            EnchantmentHelper.isEnchantmentCompatible(activatedEnchantments?.map { it.enchantment }!!, enchantment!!)

        val cap = screen.menu.osmoticEnchanter?.activeEnchantments!!.size < 9

        if (selected) {
            val bl = level(mouseX, mouseY)
            if (bl) {
                selected = !selected
                VoidBoundPacketRegistry.VOID_BOUND_CHANNEL.sendToServer(
                    EnchantmentLevelPacket(
                        enchantment!!,
                        level,
                        screen.menu.pos.asLong(),
                        false
                    )
                )
                screen.menu.osmoticEnchanter?.updateEnchantmentData(enchantment!!, level, false)
                screen.refreshEnchants()
            }
        } else if (compatible && cap) {
            VoidBoundPacketRegistry.VOID_BOUND_CHANNEL.sendToServer(
                EnchantmentLevelPacket(
                    enchantment!!,
                    level,
                    screen.menu.pos.asLong(),
                    true
                )
            )
            screen.menu.osmoticEnchanter?.updateEnchantmentData(enchantment!!, level, true)
            screen.refreshEnchants()
        }
        screen.menu.osmoticEnchanter?.calculateSpiritRequired()
        super.onClick(mouseX, mouseY)
    }

    override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        if (enchantment == null || !visible) {
            return
        }

        if (selected) {

            val border = VoidBound.id("textures/gui/enchanter_widget.png")
            guiGraphics.blit(border, x, y, 0f, 0f, width, height, width, height)


            val l = Component.empty().append(Component.translatable("enchantment.level.$level"))
            guiGraphics.drawCenteredString(Minecraft.getInstance().font, l, x + 15, y + 17, Color.WHITE.rgb)
        }

        val icon = VoidBoundUtils.getEnchantmentIcon(enchantment!!)

        var xx = x
        var yy = y

        if (selected) {
            xx += 3
            yy += 7
        }

        guiGraphics.blit(icon, xx, yy, 0f, 0f, 16, 16, 16, 16)

        val bl = EnchantmentHelper.isEnchantmentCompatible(
            screen.menu.osmoticEnchanter!!.activeEnchantments.map { it.enchantment },
            enchantment!!
        )
        if (!bl && !selected) {
            guiGraphics.blit(VoidBound.id("textures/gui/no.png"), xx + 8, yy + 8, 0f, 0f, 9, 9, 9, 9)
        }

        if (mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height) {
            val tooltip: MutableList<Component> = ArrayList()
            if (enchantment != null) tooltip.add(enchantment!!.getFullname(level))
            setTooltip(Tooltip.create(enchantment!!.getFullname(level)))
        }
    }

    private fun level(mouseX: Double, mouseY: Double): Boolean {
        val area1XStart: Double = x + 6.0
        val area1XEnd: Double = x + 16.0
        val area1YStart: Double = y.toDouble()
        val area1YEnd: Double = y + 7.0

        val area2XStart: Double = x + 6.0
        val area2XEnd: Double = x + 16.0
        val area2YStart: Double = y + 24.0
        val area2YEnd: Double = y + 30.0

        val area3XStart: Double = x.toDouble()
        val area3XEnd: Double = x + 22.0
        val area3YStart: Double = y + 8.0
        val area3YEnd: Double = y + 22.0

        when {
            // Cast mouseX and mouseY to Int and check if the mouse is in area 1
            mouseX in area1XStart..area1XEnd && mouseY in area1YStart..area1YEnd -> {
                if (canAddLevel()) {
                    level += 1
                    level = Mth.clamp(level, 0, enchantment!!.maxLevel)
                    VoidBoundPacketRegistry.VOID_BOUND_CHANNEL.sendToServer(
                        EnchantmentLevelPacket(
                            enchantment!!,
                            level,
                            screen.menu.pos.asLong(),
                            true
                        )
                    )
                    screen.menu.osmoticEnchanter!!.updateEnchantmentData(enchantment!!, level, true)
                }
            }

            // Cast mouseX and mouseY to Int and check if the mouse is in area 2
            mouseX in area2XStart..area2XEnd && mouseY in area2YStart..area2YEnd -> {
                level -= 1
                level = Mth.clamp(level, 1, enchantment!!.maxLevel)
                VoidBoundPacketRegistry.VOID_BOUND_CHANNEL.sendToServer(
                    EnchantmentLevelPacket(
                        enchantment!!,
                        level,
                        screen.menu.pos.asLong(),
                        true
                    )
                )
                screen.menu.osmoticEnchanter!!.updateEnchantmentData(enchantment!!, level, true)
            }

            // Cast mouseX and mouseY to Int and check if the mouse is in area 3
            mouseX in area3XStart..area3XEnd && mouseY in area3YStart..area3YEnd -> {
                return true
            }
        }
        return false
    }

    private fun canAddLevel(): Boolean {
        val spirits: List<SpiritWithCount> = VoidBoundUtils.getSpiritFromEnchant(enchantment!!, level)
        val toConsume: SimpleSpiritCharge = screen.menu.osmoticEnchanter!!.spiritsToConsume

        for (spirit in spirits) {
            val charge = toConsume.getChargeForType(spirit.type)
            if (charge + spirit.count > screen.maxSpiritCharge) {
                return false
            }
        }
        return true
    }

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) {

    }
}