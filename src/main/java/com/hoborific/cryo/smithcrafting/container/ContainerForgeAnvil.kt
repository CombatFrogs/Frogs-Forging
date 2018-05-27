package com.hoborific.cryo.smithcrafting.container

import com.hoborific.cryo.smithcrafting.registry.SmithingRegistry
import com.hoborific.cryo.smithcrafting.smithing.WorkingTemplate.WorkingTechnique
import com.hoborific.cryo.smithcrafting.tileentities.TileEntityForgeAnvil
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.SlotItemHandler
import kotlin.math.min

class ContainerForgeAnvil(playerInventory: IInventory, private val te: TileEntityForgeAnvil) : Container() {

    private val playerSlotsVerticalOffset = 120
    private val toolbarAdditionalVerticalOffset = 58
    private val playerSlotsHorizontalOffset = 10

    private val itemTextureDimensionWithSeparator = 18

    private val inputItemSlotCoords = Pair(48, 30)
    private val toolsItemSlotCoords = Pair(9, 98)
    private val fluxItemSlotCoords = Pair(155, 98)

    private val anvilTechniqueQueue = ArrayList<WorkingTechnique>()
    private var anvilWorkingValue = 0

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
        val itemHandler = this.te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)
        val slotCoordinates = arrayOf(inputItemSlotCoords, toolsItemSlotCoords, fluxItemSlotCoords)
        val numSlotsToRender = min(slotCoordinates.size, itemHandler!!.slots)

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
        return te.canInteractWith(playerIn)
    }

    fun handleAnvilButtonPressed(buttonId: Int) {
        val itemStackHandler = this.te.itemStackHandler

        val itemBeingWorked = itemStackHandler.getStackInSlot(0)
        if (itemBeingWorked.isEmpty) return

        val smithingRegistry = SmithingRegistry.getInstance()
        if (!smithingRegistry.hasRecipesForInput(itemBeingWorked)) return

        if (buttonId !in 0..WorkingTechnique.values().size) return
        val technique = WorkingTechnique.values()[buttonId]

        anvilTechniqueQueue.add(technique)
        while (anvilTechniqueQueue.size > 3) {
            anvilTechniqueQueue.removeAt(0)
        }

        anvilWorkingValue -= technique.recipeModifier

        println("Working value: %d, Last technique: %s".format(anvilWorkingValue, technique.toString()))

        if (anvilWorkingValue !in 0..100) {
            itemStackHandler.setStackInSlot(0, ItemStack.EMPTY)
            return
        }

        // TODO: return if anvilWorkingValue is not in range of target value i.e. in 50 - tolerance .. 50 + tolerance

        val replacementItemStack: ItemStack? = smithingRegistry.getOutputForConfigurationOrNull(
            itemBeingWorked,
            anvilTechniqueQueue[0],
            if (anvilTechniqueQueue.size > 1) anvilTechniqueQueue[1] else null,
            if (anvilTechniqueQueue.size > 2) anvilTechniqueQueue[2] else null
        )
        if (replacementItemStack != null) {
            itemStackHandler.setStackInSlot(0, replacementItemStack.copy())
            anvilTechniqueQueue.clear()
            anvilWorkingValue = 0
        }
    }
}