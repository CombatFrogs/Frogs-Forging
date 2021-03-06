package com.hoborific.cryo.frogsforging.anvil

import com.hoborific.cryo.frogsforging.FrogsForgingMod
import com.hoborific.cryo.frogsforging.registry.SmithingRegistry
import com.hoborific.cryo.frogsforging.smithing.WorkingTechnique
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite
import com.teamwizardry.librarianlib.features.guicontainer.ComponentSlot
import com.teamwizardry.librarianlib.features.guicontainer.GuiContainerBase
import com.teamwizardry.librarianlib.features.kotlin.width
import com.teamwizardry.librarianlib.features.math.Vec2d
import com.teamwizardry.librarianlib.features.network.PacketHandler
import com.teamwizardry.librarianlib.features.sprite.Sprite
import com.teamwizardry.librarianlib.features.sprite.Texture
import net.minecraft.util.ResourceLocation
import kotlin.math.ceil

const val forgeAnvilRegistryName = "forge_anvil"

/**
 * Gui variant of the Forge Anvil container, used to render the Anvil interface to the player. Displays the player's
 * inventory and hotbat, as well as the Anvil's three inventory slots, buttons to perform forging techniques and a
 * progress bar which indicates how close the player is to successfully forging the desired tool.
 */
class GuiContainerForgeAnvil(
    private val anvilContainer: ContainerForgeAnvil
) : GuiContainerBase(
    anvilContainer,
    WIDTH,
    HEIGHT
) {
    private val componentBackground: ComponentSprite
    private val componentProgressGradient: ComponentSprite
    private val componentStaticIndicator: ComponentSprite
    private val componentSlidingIndicator: ComponentSprite
    private val componentTechniqueButtons: ArrayList<ComponentSprite> = ArrayList()
    private val componentTechniqueHistory: Array<ComponentSprite>

    init {
        componentBackground = ComponentSprite(
            background, 0, 0,
            WIDTH,
            HEIGHT
        )

        val componentWorkingItemSlot = ComponentSlot(anvilContainer.invBlock.slots[0], 48, 34)
        val componentHammerItemSlot = ComponentSlot(anvilContainer.invBlock.slots[1], 9, 104)
        val componentFluxItemSlot = ComponentSlot(anvilContainer.invBlock.slots[2], 155, 104)

        componentProgressGradient = ComponentSprite(
            spriteSheet.getSprite("status_bar", 119, 9), 30, 108, 119, 9
        )
        componentStaticIndicator = ComponentSprite(
            spriteSheet.getSprite("static_indicator", 11, 10), 84, 112, 11, 10
        )
        componentSlidingIndicator = ComponentSprite(
            spriteSheet.getSprite("sliding_indicator", 11, 11), 24, 104, 11, 11
        )

        mainComponents.add(
            componentBackground,
            componentWorkingItemSlot,
            componentHammerItemSlot,
            componentFluxItemSlot,
            componentProgressGradient,
            componentStaticIndicator,
            componentSlidingIndicator
        )

        componentTechniqueHistory = Array(3, {
            ComponentSprite(
                null,
                techniqueHistoryHorizontalOffsets[it],
                techniqueHistoryVerticalOffset,
                BUTTON_WIDTH,
                BUTTON_HEIGHT
            )
        })

        addPlayerInventory(anvilContainer)
        addTechniqueButtons()
        componentTechniqueHistory.forEach { mainComponents.add(it) }
    }

    private fun addTechniqueButtons() {
        buttonMap.forEach { technique, techniqueInfo ->
            val techniqueSprite = ComponentSprite(null, techniqueInfo.x, techniqueInfo.y, BUTTON_WIDTH, BUTTON_HEIGHT)
            techniqueSprite.BUS.hook(GuiComponentEvents.MouseInEvent::class.java, { _ ->
                techniqueSprite.sprite = techniqueInfo.sprite
            })
            techniqueSprite.BUS.hook(GuiComponentEvents.MouseOutEvent::class.java, { _ ->
                // Set sprite reference to null (drawn as part of background)
                techniqueSprite.sprite = null
            })
            techniqueSprite.BUS.hook(GuiComponentEvents.MouseClickEvent::class.java, { _ ->
                // Forward event to the backing container instance.
                PacketHandler.NETWORK.sendToServer(
                    PacketAnvilTechnique(
                        technique.ordinal,
                        anvilContainer.anvilTile.pos
                    )
                )
            })

            componentTechniqueButtons.add(techniqueSprite)
            mainComponents.add(techniqueSprite)
        }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        updateSpritesForTechniqueHistory()
        updateWorkingStatusIndicators()

        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    private fun updateWorkingStatusIndicators() {
        val itemBeingWorked = anvilContainer.anvilTile.getStackInSlot(0)
        if (!itemBeingWorked.isEmpty) {
            val smithingRegistry = SmithingRegistry.getInstance()
            val craftingResultMap = smithingRegistry.itemRecipeMap[Pair(itemBeingWorked.item, itemBeingWorked.metadata)]
            if (craftingResultMap != null && craftingResultMap.isNotEmpty()) {
                componentProgressGradient.isVisible = true
                componentStaticIndicator.isVisible = true
                componentSlidingIndicator.isVisible = true

                val progressPercentage = (anvilContainer.anvilTile.anvilWorkingValue / 100f)
                val positionOfCenterMark = ceil(progressPercentage * componentProgressGradient.width)
                componentSlidingIndicator.transform.postTranslate = Vec2d(positionOfCenterMark.toDouble(), 0.0)

                return
            }
        }

        componentProgressGradient.isVisible = false
        componentStaticIndicator.isVisible = false
        componentSlidingIndicator.isVisible = false
    }

    private fun updateSpritesForTechniqueHistory() {
        val anvilHistory = anvilContainer.anvilTile.publicAnvilHistory
        for (spriteIndex in 0 until componentTechniqueHistory.size) {
            val historyIndex = componentTechniqueHistory.size - spriteIndex - 1
            if (anvilHistory[historyIndex] in WorkingTechnique.LIGHT_HIT.ordinal..WorkingTechnique.SHRINK.ordinal) {
                val techniqueForSlot = WorkingTechnique.values()[anvilHistory[historyIndex].toInt()]
                componentTechniqueHistory[spriteIndex].sprite = buttonMap[techniqueForSlot]?.sprite
            }
        }
    }

    private fun addPlayerInventory(container: ContainerForgeAnvil) {
        for (row in 0..2) {
            for (col in 0..8) {
                val x = playerSlotsHorizontalOffset + col * itemTextureDimensionWithSeparator
                val y = row * itemTextureDimensionWithSeparator + playerSlotsVerticalOffset
                mainComponents.add(ComponentSlot(container.invPlayer.slots[col + row * 9 + 9], x, y))
            }
        }

        for (row in 0..8) {
            val x = playerSlotsHorizontalOffset + row * itemTextureDimensionWithSeparator
            val y = toolbarAdditionalVerticalOffset + playerSlotsVerticalOffset
            mainComponents.add(ComponentSlot(container.invPlayer.slots[row], x, y))
        }
    }

    companion object {
        private const val WIDTH = 180
        private const val HEIGHT = 208
        private const val BUTTON_WIDTH = 16
        private const val BUTTON_HEIGHT = 16

        private val spriteSheet =
            Texture(ResourceLocation(FrogsForgingMod.MODID, "textures/gui/forge_anvil.png"))
        private val background = spriteSheet.getSprite(
            "background",
            WIDTH,
            HEIGHT
        )

        //
        val techniqueHistoryHorizontalOffsets: Array<Int> = arrayOf(93, 118, 143)
        const val techniqueHistoryVerticalOffset: Int = 35

        // Some magic numbers for positioning the player's inventory slots in the GUI.
        private const val playerSlotsVerticalOffset = 126
        private const val toolbarAdditionalVerticalOffset = 58
        private const val playerSlotsHorizontalOffset = 10
        private const val itemTextureDimensionWithSeparator = 18

        // Position of each technique's button relative to the background sprite.
        private val buttonMap = hashMapOf(
            WorkingTechnique.LIGHT_HIT to ButtonTechniqueRepresentation(88, 57, WorkingTechnique.LIGHT_HIT.spriteName),
            WorkingTechnique.MEDIUM_HIT to ButtonTechniqueRepresentation(
                106,
                57,
                WorkingTechnique.MEDIUM_HIT.spriteName
            ),
            WorkingTechnique.HEAVY_HIT to ButtonTechniqueRepresentation(89, 75, WorkingTechnique.HEAVY_HIT.spriteName),
            WorkingTechnique.DRAW to ButtonTechniqueRepresentation(107, 75, WorkingTechnique.DRAW.spriteName),
            WorkingTechnique.PUNCH to ButtonTechniqueRepresentation(130, 57, WorkingTechnique.PUNCH.spriteName),
            WorkingTechnique.BEND to ButtonTechniqueRepresentation(148, 57, WorkingTechnique.BEND.spriteName),
            WorkingTechnique.UPSET to ButtonTechniqueRepresentation(130, 75, WorkingTechnique.UPSET.spriteName),
            WorkingTechnique.SHRINK to ButtonTechniqueRepresentation(148, 75, WorkingTechnique.SHRINK.spriteName)
        )

        data class ButtonTechniqueRepresentation(val x: Int, val y: Int, private val spriteName: String?) {
            val sprite: Sprite? =
                if (spriteName != null) spriteSheet.getSprite(spriteName, BUTTON_WIDTH, BUTTON_HEIGHT) else null
        }
    }
}