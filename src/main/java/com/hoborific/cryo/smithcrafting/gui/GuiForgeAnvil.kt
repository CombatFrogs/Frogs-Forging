package com.hoborific.cryo.smithcrafting.gui

import com.hoborific.cryo.smithcrafting.SmithcraftingMod
import com.hoborific.cryo.smithcrafting.container.ContainerForgeAnvil
import com.hoborific.cryo.smithcrafting.packet.PacketAnvilInteraction
import com.hoborific.cryo.smithcrafting.packet.PacketHandler
import com.hoborific.cryo.smithcrafting.smithing.WorkingTemplate.WorkingTechnique
import com.hoborific.cryo.smithcrafting.tileentities.TileEntityForgeAnvil
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation

class GuiForgeAnvil(
    private val forgeAnvilTile: TileEntityForgeAnvil,
    private val container: ContainerForgeAnvil
) : GuiContainer(container) {

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

    override fun actionPerformed(button: GuiButton?) {
        if (button == null || button.id > WorkingTechnique.values().size) return

        PacketHandler.networkInstance!!.sendToServer(PacketAnvilInteraction(container.windowId, button.id))
    }

    companion object {
        const val WIDTH = 180
        const val HEIGHT = 243

        private val background = ResourceLocation(SmithcraftingMod.MODID, "textures/gui/forge_anvil.png")
    }

    private class CustomAnvilButton(
            anvilTechnique: WorkingTechnique,
            private val buttonTextureX: Int,
            private val buttonTextureY: Int,
            x: Int,
            y: Int,
            width: Int,
            height: Int
    ) :
            GuiButton(anvilTechnique.ordinal, x, y, width, height, "") {
        val resourceLocation = ResourceLocation(SmithcraftingMod.MODID, "textures/gui/custom_button.png")

        /**
         * Draws this button to the screen.
         */
        override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int, partialTicks: Float) {
            if (visible) {
                hovered = (mouseX in x..x + width) && (mouseY in y..y + height)
                mc.textureManager.bindTexture(resourceLocation)

                if (hovered) {
                    GlStateManager.disableDepth()
                    this.drawTexturedModalRect(x, y, buttonTextureX, buttonTextureY, width, height)
                    GlStateManager.enableDepth()
                }
            }
        }
    }
}