package com.hoborific.cryo.frogsforging.util

import com.hoborific.cryo.frogsforging.FrogsForgingMod
import com.hoborific.cryo.frogsforging.items.ItemSmithedPickaxe
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

fun setSmithedToolQuality(itemStack: ItemStack, quality: Int): Boolean {
    if (itemStack.item !is ItemSmithedPickaxe) {
        FrogsForgingMod.logger?.warn("Attempted to set tool quality on an unsupported ItemStack.")
        return false
    }

    val nbtData = itemStack.tagCompound ?: NBTTagCompound()
    nbtData.setInteger(ItemSmithedPickaxe.TOOL_QUALITY_TAG, minOf(250, quality))
    itemStack.tagCompound = nbtData

    return true
}

fun getSmithedToolQuality(itemStack: ItemStack): Int {
    val nbtData = itemStack.tagCompound ?: return 0
    return nbtData.getInteger(ItemSmithedPickaxe.TOOL_QUALITY_TAG)
}

fun getStackFromConfigString(itemStackString: String): ItemStack? {
    val i = itemStackString.lastIndexOf(':')

    val itemName: String
    val itemMetadata: Int
    if (i == itemStackString.indexOf(':')) {
        itemName = itemStackString
        itemMetadata = 0
    } else {
        itemName = itemStackString.substring(0, i)
        itemMetadata = itemStackString.substring(i + 1).toIntOrNull() ?: return null
    }

    val item = Item.getByNameOrId(itemName) ?: return null
    return ItemStack(item, 1, itemMetadata)
}