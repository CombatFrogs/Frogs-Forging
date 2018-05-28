package com.hoborific.cryo.smithcrafting.gui

import com.hoborific.cryo.smithcrafting.smithing.WorkingTemplate
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.renderer.GlStateManager

internal class CustomAnvilButton(
    anvilTechnique: WorkingTemplate.WorkingTechnique,
    private val buttonTextureX: Int,
    private val buttonTextureY: Int,
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : GuiButton(anvilTechnique.ordinal, x, y, width, height, "") {

    override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int, partialTicks: Float) {
        if (visible) {
            hovered = (mouseX in x..x + width) && (mouseY in y..y + height)
            mc.textureManager.bindTexture(GuiForgeAnvil.buttonOverlay)

            if (hovered) {
                GlStateManager.disableDepth()
                this.drawTexturedModalRect(x, y, buttonTextureX, buttonTextureY, width, height)
                GlStateManager.enableDepth()
            }
        }
    }
}