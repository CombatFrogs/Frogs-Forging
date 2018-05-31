package com.hoborific.cryo.frogsforging.gui

import com.hoborific.cryo.frogsforging.FrogsForgingMod
import com.hoborific.cryo.frogsforging.container.ContainerForgeAnvil
import com.hoborific.cryo.frogsforging.packet.PacketAnvilInteraction
import com.hoborific.cryo.frogsforging.packet.PacketHandler
import com.hoborific.cryo.frogsforging.smithing.WorkingTemplate.WorkingTechnique
import com.hoborific.cryo.frogsforging.tileentities.TileEntityForgeAnvil
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

        val progressPercentage = (anvilTileEntity.itemWorkingProgress / 100f) - 0.5f
        val translation =
            (progressPercentage * gradientOverlayDimensions.assetWidth) - (0.5f * slidingOverlayDimensions.assetWidth)

        renderGuiOverlay(gradientOverlayDimensions)
        renderGuiOverlay(stationaryOverlayDimensions)
        renderGuiOverlayWithTranslation(slidingOverlayDimensions, translation.toInt())

        mc.textureManager.bindTexture(buttonOverlay)
        for (i in 1..anvilTileEntity.techniqueList.size) {
            val j = anvilTileEntity.techniqueList[anvilTileEntity.techniqueList.size - i]
            if (j !in WorkingTechnique.LIGHT_HIT.ordinal..WorkingTechnique.SHRINK.ordinal) {
                continue
            }
            val coordsInTexture = buttonMap[WorkingTechnique.values()[j.toInt()]] ?: continue

            drawTexturedModalRect(
                guiLeft + previousWorkedTechniqueXCoords[i - 1],
                guiTop + previousWorkedTechniqueYCoords,
                coordsInTexture.first,
                coordsInTexture.second,
                13,
                13
            )
        }
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

        PacketHandler.networkInstance!!.sendToServer(
            PacketAnvilInteraction(
                container.windowId,
                button.id
            )
        )
    }

    companion object {
        const val WIDTH = 180
        const val HEIGHT = 243

        private val background = ResourceLocation(FrogsForgingMod.MODID, "textures/gui/forge_anvil.png")
        private val overlays = ResourceLocation(FrogsForgingMod.MODID, "textures/gui/anvil_overlays.png")
        internal val buttonOverlay = ResourceLocation(FrogsForgingMod.MODID, "textures/gui/custom_button.png")

        private val gradientOverlayDimensions =
            GuiOverlayRenderDimensions(30, 108, 0, 0, 119, 9)
        private val slidingOverlayDimensions =
            GuiOverlayRenderDimensions(89, 104, 0, 9, 11, 12)
        private val stationaryOverlayDimensions =
            GuiOverlayRenderDimensions(89, 112, 11, 9, 11, 10)

        private val buttonMap = hashMapOf(
            WorkingTechnique.LIGHT_HIT to Pair(88, 57),
            WorkingTechnique.MEDIUM_HIT to Pair(106, 57),
            WorkingTechnique.HEAVY_HIT to Pair(89, 75),
            WorkingTechnique.DRAW to Pair(107, 75),
            WorkingTechnique.PUNCH to Pair(130, 57),
            WorkingTechnique.BEND to Pair(148, 57),
            WorkingTechnique.UPSET to Pair(130, 75),
            WorkingTechnique.SHRINK to Pair(148, 75)
        )

        private val previousWorkedTechniqueYCoords = 35
        private val previousWorkedTechniqueXCoords = arrayOf(93, 118, 143)
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