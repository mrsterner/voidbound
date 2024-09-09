package dev.sterner.client.screen

import com.sammy.malum.core.systems.spirit.MalumSpiritType
import com.sammy.malum.registry.common.SpiritTypeRegistry
import dev.sterner.VoidBound
import dev.sterner.client.screen.widget.EnchantmentWidget
import dev.sterner.client.screen.widget.SpiritBarWidget
import dev.sterner.client.screen.widget.StartEnchantingWidget
import dev.sterner.common.blockentity.OsmoticEnchanterBlockEntity
import dev.sterner.common.menu.OsmoticEnchanterMenu
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory


class OsmoticEnchanterScreen(
    menu: OsmoticEnchanterMenu,
    playerInventory: Inventory, title: Component
) : AbstractContainerScreen<OsmoticEnchanterMenu>(menu, playerInventory, title) {

    private var blockEntity: OsmoticEnchanterBlockEntity? = null
    var maxSpiritCharge = 128

    init {
        imageWidth = 232
        imageHeight = 247
        blockEntity = getBlockEntity(playerInventory, menu.pos)
    }

    override fun init() {
        super.init()
        menu.shouldRefresh = true
    }

    override fun containerTick() {
        if (menu.shouldRefresh) {
            refreshEnchants()
            menu.shouldRefresh = false
        }
        super.containerTick()
    }

    override fun resize(minecraft: Minecraft, width: Int, height: Int) {
        refreshEnchants()
        super.resize(minecraft, width, height)
    }

    fun refreshEnchants() {
        clearWidgets()

        val xInMenu = (this.width - this.imageWidth) / 2
        val yInMenu = (this.height - this.imageHeight) / 2

        addEnchantments(blockEntity?.availableEnchantments, xInMenu, yInMenu, false)
        addEnchantments(blockEntity?.activeEnchantments, xInMenu, yInMenu, true)

        addStartEnchantingWidget(xInMenu, yInMenu)
        addSpiritBarWidget(xInMenu, yInMenu, true)
        addSpiritBarWidget(xInMenu, yInMenu, false)

        blockEntity?.calculateSpiritRequired()
    }

    private fun addEnchantments(
        enchantments: MutableList<OsmoticEnchanterBlockEntity.EnchantmentData>?,
        xInMenu: Int,
        yInMenu: Int,
        selected: Boolean
    ) {
        enchantments?.forEachIndexed { index, data ->
            var width = 16
            var height = 16

            val (xOffset, yOffset) = if (selected) {
                width = 22
                height = 33
                calculateWidgetPosition(index, 3, 83, 5, 34, 23)
            } else {
                calculateWidgetPosition(index, 3, 168, 15 + 26, 17, 17)
            }

            val widget = EnchantmentWidget(this, xInMenu + xOffset, yInMenu + yOffset, width, height)
            widget.enchantment = data.enchantment
            // println(data.enchantment.descriptionId)
            widget.level = data.level
            widget.selected = selected
            this.addRenderableWidget(widget)
        }
    }

    private fun addStartEnchantingWidget(xInMenu: Int, yInMenu: Int) {
        val widgetX = xInMenu + 13 + 16 * 6 - 3
        val widgetY = yInMenu + 18 * 5 + 19 + 28
        this.addRenderableWidget(StartEnchantingWidget(this, widgetX, widgetY))
    }

    private fun addSpiritBarWidget(xInMenu: Int, yInMenu: Int, isScry: Boolean) {
        val topRowSpirits = arrayOf(
            SpiritTypeRegistry.AERIAL_SPIRIT,
            SpiritTypeRegistry.AQUEOUS_SPIRIT,
            SpiritTypeRegistry.EARTHEN_SPIRIT,
            SpiritTypeRegistry.INFERNAL_SPIRIT
        )

        val bottomRowSpirits = arrayOf(
            SpiritTypeRegistry.ARCANE_SPIRIT,
            SpiritTypeRegistry.ELDRITCH_SPIRIT,
            SpiritTypeRegistry.WICKED_SPIRIT,
            SpiritTypeRegistry.SACRED_SPIRIT
        )

        fun addSpiritWidgets(spiritTypes: Array<MalumSpiritType>, xInMenu: Int, yOffset: Int) {
            for ((index, spiritType) in spiritTypes.withIndex()) {
                val widget = SpiritBarWidget(this, xInMenu + 12 + 17 * index, yInMenu + yOffset)
                widget.spiritType = spiritType
                widget.isScry = isScry
                this.addRenderableWidget(widget)
            }
        }

        addSpiritWidgets(topRowSpirits, xInMenu, 37)

        addSpiritWidgets(bottomRowSpirits, xInMenu, 100)
    }

    private fun calculateWidgetPosition(
        index: Int,
        itemsPerRow: Int,
        baseX: Int,
        baseY: Int,
        offsetY: Int,
        offsetX: Int
    ): Pair<Int, Int> {
        val xOffset = (index % itemsPerRow) * offsetX + baseX
        val yOffset = (index / itemsPerRow) * offsetY + baseY
        return Pair(xOffset, yOffset)
    }

    private fun getBlockEntity(playerInventory: Inventory, blockPos: BlockPos): OsmoticEnchanterBlockEntity? {
        val blockEntity = playerInventory.player.level().getBlockEntity(blockPos)
        if (blockEntity is OsmoticEnchanterBlockEntity) {
            return blockEntity
        }
        return null
    }

    override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        val k = (this.width - this.imageWidth) / 2
        val l = (this.height - this.imageHeight) / 2
        guiGraphics.blit(CONTAINER_TEXTURE, k, l, 0, 0, this.imageWidth, this.imageHeight)
    }

    override fun renderLabels(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int) {

    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        super.render(guiGraphics, mouseX, mouseY, partialTick)
        this.renderTooltip(guiGraphics, mouseX, mouseY)
    }

    companion object {
        private val CONTAINER_TEXTURE: ResourceLocation =
            VoidBound.id("textures/gui/enchanter.png")
    }
}