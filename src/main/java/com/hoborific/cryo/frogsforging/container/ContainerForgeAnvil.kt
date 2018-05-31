package com.hoborific.cryo.frogsforging.container

import com.hoborific.cryo.frogsforging.registry.SmithingRegistry
import com.hoborific.cryo.frogsforging.smithing.WorkingTemplate.WorkingTechnique
import com.hoborific.cryo.frogsforging.tileentities.TileEntityForgeAnvil
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.IContainerListener
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.ItemStackHandler
import net.minecraftforge.items.SlotItemHandler

class ContainerForgeAnvil(playerInventory: IInventory, private val anvilTileEntity: TileEntityForgeAnvil) :
    Container() {
    companion object {
        private const val playerSlotsVerticalOffset = 126
        private const val toolbarAdditionalVerticalOffset = 58
        private const val playerSlotsHorizontalOffset = 10

        private const val itemTextureDimensionWithSeparator = 18

        private const val CURRENT_WORKING_VALUE_ID = 0
        private const val LAST_TECHNIQUE_USED_ID = 1

        private val inputItemSlotCoords = Pair(48, 34)
        private val toolsItemSlotCoords = Pair(9, 104)
        private val fluxItemSlotCoords = Pair(155, 104)
    }

    init {
        addInternalInventorySlots()
        addPlayerInventorySlots(playerInventory)
    }

    private fun addPlayerInventorySlots(playerInventory: IInventory) {
        for (row in 0..2) {
            for (col in 0..8) {
                val x = playerSlotsHorizontalOffset + col * itemTextureDimensionWithSeparator
                val y = row * itemTextureDimensionWithSeparator + playerSlotsVerticalOffset
                this.addSlotToContainer(Slot(playerInventory, col + row * 9 + 9, x, y))
            }
        }

        for (row in 0..8) {
            val x = playerSlotsHorizontalOffset + row * itemTextureDimensionWithSeparator
            val y = toolbarAdditionalVerticalOffset + playerSlotsVerticalOffset
            this.addSlotToContainer(Slot(playerInventory, row, x, y))
        }
    }

    private fun addInternalInventorySlots() {
        val itemHandler = this.anvilTileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)
        val slotCoordinates = arrayOf(
            inputItemSlotCoords,
            toolsItemSlotCoords,
            fluxItemSlotCoords
        )
        val numSlotsToRender = minOf(slotCoordinates.size, itemHandler!!.slots)

        for (itemIndex in 0 until numSlotsToRender) {
            addSlotToContainer(
                SlotItemHandler(
                    itemHandler,
                    itemIndex,
                    slotCoordinates[itemIndex].first,
                    slotCoordinates[itemIndex].second
                )
            )
        }
    }

    override fun transferStackInSlot(playerIn: EntityPlayer?, index: Int): ItemStack? {
        var itemStack: ItemStack? = ItemStack.EMPTY
        val slot = this.inventorySlots[index]

        if (slot != null && slot.hasStack) {
            val itemStackInSlot = slot.stack
            itemStack = itemStackInSlot.copy()

            if (index < TileEntityForgeAnvil.SIZE) {
                if (!this.mergeItemStack(itemStackInSlot, TileEntityForgeAnvil.SIZE, this.inventorySlots.size, true)) {
                    return ItemStack.EMPTY
                }
            } else if (!this.mergeItemStack(itemStackInSlot, 0, TileEntityForgeAnvil.SIZE, false)) {
                return ItemStack.EMPTY
            }

            if (itemStackInSlot.isEmpty) {
                slot.putStack(ItemStack.EMPTY)
            } else {
                slot.onSlotChanged()
            }
        }

        return itemStack
    }

    override fun canInteractWith(playerIn: EntityPlayer): Boolean {
        return anvilTileEntity.canInteractWith(playerIn)
    }

    fun handleAnvilButtonPressed(buttonId: Int) {
        val itemStackHandler = this.anvilTileEntity.itemStackHandler

        val itemBeingWorked = itemStackHandler.getStackInSlot(0)
        if (itemBeingWorked.isEmpty) return

        val smithingRegistry = SmithingRegistry.getInstance()
        if (!smithingRegistry.hasRecipesForInput(itemBeingWorked)) return

        if (buttonId !in 0..WorkingTechnique.values().size) return
        val technique = WorkingTechnique.values()[buttonId]

        anvilTileEntity.handleTechniqueUsed(technique)

        listeners.forEach {
            it.sendWindowProperty(
                this,
                CURRENT_WORKING_VALUE_ID, anvilTileEntity.itemWorkingProgress
            )
            it.sendWindowProperty(
                this,
                LAST_TECHNIQUE_USED_ID, buttonId
            )
        }

        println(
            "Working value: %d, Last technique: %s".format(
                anvilTileEntity.itemWorkingProgress,
                technique.toString()
            )
        )

        if (anvilTileEntity.itemWorkingProgress !in 0..100) {
            replacePrimaryItemAndClearProgress(itemStackHandler, ItemStack.EMPTY)
            return
        }

        // TODO: return if anvilWorkingValue is not in range of target value i.e. in 50 - tolerance .. 50 + tolerance
        if (anvilTileEntity.itemWorkingProgress != 50) return

        val replacementItemStack: ItemStack? = smithingRegistry.getOutputForConfigurationOrNull(
            itemBeingWorked,
            anvilTileEntity.techniqueList[2],
            anvilTileEntity.techniqueList[1],
            anvilTileEntity.techniqueList[0]
        )

        if (replacementItemStack != null) {
            replacePrimaryItemAndClearProgress(itemStackHandler, replacementItemStack)
        }
    }

    private fun replacePrimaryItemAndClearProgress(
        itemStackHandler: ItemStackHandler,
        replacementItemStack: ItemStack
    ) {
        itemStackHandler.setStackInSlot(0, replacementItemStack.copy())
        anvilTileEntity.clearItemProgress()
        listeners.forEach { resetItemProgressVisual(it) }
    }

    fun shouldRenderProgressBar(): Boolean {
        val itemStackHandler = this.anvilTileEntity.itemStackHandler
        val itemBeingWorked = itemStackHandler.getStackInSlot(0)
        return !itemBeingWorked.isEmpty
    }

    @SideOnly(Side.CLIENT)
    override fun updateProgressBar(id: Int, data: Int) {
        if (id == CURRENT_WORKING_VALUE_ID) {
            anvilTileEntity.itemWorkingProgress = data
            anvilTileEntity.markDirty()
        } else if (id == LAST_TECHNIQUE_USED_ID) {
            anvilTileEntity.handleTechniqueUsed(data, 0)
        }
    }

    override fun addListener(listener: IContainerListener?) {
        listener ?: return

        super.addListener(listener)
        resetItemProgressVisual(listener)
    }

    private fun resetItemProgressVisual(listener: IContainerListener) {
        listener.sendWindowProperty(
            this,
            CURRENT_WORKING_VALUE_ID, anvilTileEntity.itemWorkingProgress
        )
        for (i in 1..anvilTileEntity.techniqueList.size) {
            listener.sendWindowProperty(
                this, LAST_TECHNIQUE_USED_ID,
                anvilTileEntity.techniqueList[anvilTileEntity.techniqueList.size - i].toInt()
            )
        }
    }
}