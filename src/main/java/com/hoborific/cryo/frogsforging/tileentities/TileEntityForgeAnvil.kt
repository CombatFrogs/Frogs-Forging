package com.hoborific.cryo.frogsforging.tileentities

import com.hoborific.cryo.frogsforging.smithing.WorkingTemplate.WorkingTechnique
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.ItemStackHandler

class TileEntityForgeAnvil : TileEntity() {
    internal var techniqueList: ByteArray = ByteArray(3, { _ -> -1 })
    internal var itemWorkingProgress: Int = 0

    // This item handler will hold our nine inventory slots
    val itemStackHandler = object : ItemStackHandler(SIZE) {
        override fun onContentsChanged(slot: Int) {
            // We need to tell the tile entity that something has changed so
            // that the chest contents is persisted
            this@TileEntityForgeAnvil.markDirty()
        }
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        if (compound.hasKey("items")) {
            itemStackHandler.deserializeNBT(compound.getTag("items") as NBTTagCompound)
        }
        if (compound.hasKey("working")) {
            itemWorkingProgress = compound.getInteger("working")
        }
        if (compound.hasKey("techniques")) {
            techniqueList = compound.getByteArray("techniques")
        }
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(compound)
        compound.setTag("items", itemStackHandler.serializeNBT())
        compound.setInteger("working", itemWorkingProgress)
        compound.setByteArray("techniques", techniqueList)

        return compound
    }

    fun canInteractWith(playerIn: EntityPlayer): Boolean {
        // If we are too far away from this tile entity you cannot use it
        return !isInvalid && playerIn.getDistanceSq(pos.add(0.5, 0.5, 0.5)) <= 64.0
    }

    internal fun handleTechniqueUsed(technique: Int, recipeModifier: Int) {
        val techniqueByte: Byte = technique.toByte()
        techniqueList[2] = techniqueList[1]
        techniqueList[1] = techniqueList[0]
        techniqueList[0] = techniqueByte

        itemWorkingProgress -= recipeModifier

        this@TileEntityForgeAnvil.markDirty()
    }

    internal fun handleTechniqueUsed(technique: WorkingTechnique) {
        handleTechniqueUsed(technique.ordinal, technique.recipeModifier)
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return if (capability === CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            true
        } else super.hasCapability(capability, facing)
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return if (capability === CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast<T>(itemStackHandler)
        } else super.getCapability(capability, facing)
    }

    companion object {
        val SIZE = 3
    }

    internal fun clearItemProgress() {
        techniqueList = ByteArray(3, { _ -> -1 })
        itemWorkingProgress = 0

        markDirty()
    }
}