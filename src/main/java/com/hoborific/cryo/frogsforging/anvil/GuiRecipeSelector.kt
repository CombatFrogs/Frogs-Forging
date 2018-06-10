package com.hoborific.cryo.frogsforging.anvil

import com.hoborific.cryo.frogsforging.FrogsForgingMod
import com.hoborific.cryo.frogsforging.registry.SmithingRegistry
import com.hoborific.cryo.frogsforging.smithing.WorkingTemplate
import com.teamwizardry.librarianlib.features.container.ContainerBase
import com.teamwizardry.librarianlib.features.container.GuiHandler
import com.teamwizardry.librarianlib.features.gui.components.ComponentGrid
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite
import com.teamwizardry.librarianlib.features.gui.components.ComponentStack
import com.teamwizardry.librarianlib.features.guicontainer.GuiContainerBase
import com.teamwizardry.librarianlib.features.sprite.Texture
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import java.util.HashMap

class ContainerRecipeSelector(player: EntityPlayer, internal val anvilTile: TileEntityForgeAnvil) :
    ContainerBase(player) {
    companion object {
        val NAME = ResourceLocation("frogsforging:plan_selection_gui")

        init {
            GuiHandler.registerBasicContainer(
                ContainerRecipeSelector.NAME,
                { player, _, tile ->
                    ContainerRecipeSelector(
                        player,
                        tile as TileEntityForgeAnvil
                    )
                },
                { _, container -> GuiRecipeSelector(container) })
        }
    }
}

/**
 * GUI to allow users to select a specific output item that is attainable by working the item currently in the input
 * slot. The steps necessary to craft the item will be displayed in the Anvil GUI. This ultimately helps users to
 * determine the sequence of steps needed to craft their items as well as preventing users from working the input into
 * an item they did not intend to craft.
 */
class GuiRecipeSelector(container: ContainerRecipeSelector) : GuiContainerBase(container, WIDTH, HEIGHT) {
    init {
        val componentBackground = ComponentSprite(
            background, 0, 0,
            WIDTH,
            HEIGHT
        )

        mainComponents.add(componentBackground)

        val itemStackWorkable = container.anvilTile.getStackInSlot(0)

        val smithingRegistry = SmithingRegistry.getInstance()

        val outputItems = smithingRegistry.itemRecipeMap[Pair(itemStackWorkable.item, itemStackWorkable.metadata)]
        if (outputItems != null && !outputItems.isEmpty()) {
            addButtonsForWorkableItems(outputItems)
        }
    }

    private fun addButtonsForWorkableItems(outputItems: HashMap<WorkingTemplate, ItemStack>) {
        val gridContainer = ComponentGrid(0, 0, PADDED_ITEM_DIMENS, PADDED_ITEM_DIMENS, WIDTH / PADDED_ITEM_DIMENS)

        outputItems.forEach { _, outputStack ->
            val stackComponent = ComponentStack(0, 0)
            stackComponent.stack.func { outputStack }

            gridContainer.add(stackComponent)
        }

        mainComponents.add(gridContainer)
    }

    companion object {
        private const val WIDTH = 180
        private const val HEIGHT = 208

        private val spriteSheet =
            Texture(ResourceLocation(FrogsForgingMod.MODID, "textures/gui/recipe_selection.png"))
        private val background = spriteSheet.getSprite(
            "background",
            WIDTH,
            HEIGHT
        )

        private const val PADDED_ITEM_DIMENS = 18
    }
}