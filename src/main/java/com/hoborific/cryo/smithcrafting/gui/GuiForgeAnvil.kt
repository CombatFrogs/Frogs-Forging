package com.hoborific.cryo.smithcrafting.gui

import com.hoborific.cryo.smithcrafting.SmithcraftingMod
import com.hoborific.cryo.smithcrafting.container.ContainerForgeAnvil
import com.hoborific.cryo.smithcrafting.packet.PacketAnvilInteraction
import com.hoborific.cryo.smithcrafting.packet.PacketHandler
import com.hoborific.cryo.smithcrafting.smithing.WorkingTemplate.WorkingTechnique
import com.hoborific.cryo.smithcrafting.tileentities.TileEntityForgeAnvil
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.util.ResourceLocation

class GuiForgeAnvil(
    private val anvilTileEntity: TileEntityForgeAnvil,
    private val container: ContainerForgeAnvil
) : GuiContainer(container) {

    init {
        xSize = WIDTH
        ySize = HEIGHT
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        mc.textureManager.bindTexture(background)
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize)
    }

    override fun initGui() {
        super.initGui()

        buttonMap.entries.forEach { (anvilTechnique, position) ->
            buttonList.add(
                CustomAnvilButton(
                    anvilTechnique,
                    position.first,
                    position.second,
                    position.first + guiLeft,
                    position.second + guiTop,
                    16,
                    16
                )
            )
        }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.drawScreen(mouseX, mouseY, partialTicks)

        if (!container.shouldRenderProgressBar()) return

        mc.textureManager.bindTexture(overlays)

        val progressPercentage = anvilTileEntity.itemWorkingProgress / 100f
        val translation = (progressPercentage - 0.5f) * gradientOverlayDimensions.assetWidth

        renderGuiOverlay(gradientOverlayDimensions)
        renderGuiOverlay(stationaryOverlayDimensions)
        renderGuiOverlayWithTranslation(slidingOverlayDimensions, translation.toInt())
    }

    private fun renderGuiOverlayWithTranslation(overlay: GuiOverlayRenderDimensions, translation: Int) {
        this.drawTexturedModalRect(
            guiLeft + overlay.x + translation,
            guiTop + overlay.y,
            overlay.xCoordInAsset,
            overlay.yCoordInAsset,
            overlay.assetWidth,
            overlay.assetHeight
        )
    }

    private fun renderGuiOverlay(overlay: GuiOverlayRenderDimensions) {
        renderGuiOverlayWithTranslation(overlay, 0)
    }

    override fun actionPerformed(button: GuiButton?) {
        if (button == null || button.id > WorkingTechnique.values().size) return

        PacketHandler.networkInstance!!.sendToServer(PacketAnvilInteraction(container.windowId, button.id))
    }

    companion object {
        const val WIDTH = 180
        const val HEIGHT = 243

        private val background = ResourceLocation(SmithcraftingMod.MODID, "textures/gui/forge_anvil.png")
        private val overlays = ResourceLocation(SmithcraftingMod.MODID, "textures/gui/anvil_overlays.png")

        private val gradientOverlayDimensions = GuiOverlayRenderDimensions(30, 102, 0, 0, 119, 9)
        private val slidingOverlayDimensions = GuiOverlayRenderDimensions(84, 98, 0, 9, 11, 12)
        private val stationaryOverlayDimensions = GuiOverlayRenderDimensions(84, 106, 11, 9, 11, 10)

        private val buttonMap = hashMapOf(
            WorkingTechnique.LIGHT_HIT to Pair(89, 51),
            WorkingTechnique.MEDIUM_HIT to Pair(107, 51),
            WorkingTechnique.HEAVY_HIT to Pair(89, 69),
            WorkingTechnique.DRAW to Pair(107, 69),
            WorkingTechnique.PUNCH to Pair(130, 51),
            WorkingTechnique.BEND to Pair(148, 51),
            WorkingTechnique.UPSET to Pair(130, 69),
            WorkingTechnique.SHRINK to Pair(148, 69)
        )
    }

    private class GuiOverlayRenderDimensions(
        internal val x: Int,
        internal val y: Int,
        internal val xCoordInAsset: Int,
        internal val yCoordInAsset: Int,
        internal val assetWidth: Int,
        internal val assetHeight: Int
    )
}