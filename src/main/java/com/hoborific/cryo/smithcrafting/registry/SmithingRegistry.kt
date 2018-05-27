package com.hoborific.cryo.smithcrafting.registry

import com.hoborific.cryo.smithcrafting.SmithcraftingMod
import com.hoborific.cryo.smithcrafting.smithing.WorkingTemplate
import com.hoborific.cryo.smithcrafting.smithing.WorkingTemplate.WorkingTechnique
import com.hoborific.cryo.smithcrafting.util.getStackFromConfigString
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

class SmithingRegistry {
    val workingTemplateMap: HashMap<String, WorkingTemplate> = HashMap()
    val itemRecipeMap: HashMap<Pair<Item, Int>, HashMap<WorkingTemplate, ItemStack>> = HashMap()

    fun registerTemplate(template: WorkingTemplate) {
        workingTemplateMap.put(template.identifier, template)
    }

    fun registerTemplate(
        identifier: String,
        firstTech: WorkingTechnique?,
        secondTech: WorkingTechnique?,
        thirdTech: WorkingTechnique?
    ) {
        var workingTemplate = WorkingTemplate(identifier, firstTech, secondTech, thirdTech)
        registerTemplate(workingTemplate)
    }

    fun registerTemplate(
        identifier: String,
        firstTech: String?,
        secondTech: String?,
        thirdTech: String?
    ) {
        registerTemplate(
            identifier,
            if (firstTech != null) techniqueFromString(firstTech) else null,
            if (secondTech != null) techniqueFromString(secondTech) else null,
            if (thirdTech != null) techniqueFromString(thirdTech) else null
        )
    }

    private fun techniqueFromString(technique: String?): WorkingTechnique? {
        if (technique == null) return null

        return try {
            WorkingTechnique.valueOf(technique.toUpperCase())
        } catch (e: IllegalArgumentException) {
            null
        }
    }


    fun getTemplateWithIdentifier(identifier: String): WorkingTemplate? {
        return workingTemplateMap.getOrDefault(identifier, null)
    }

    fun registerDeformation(rawItem: String, deformedItem: String) {
        SmithcraftingMod.logger?.debug("%s will become %s when processed".format(rawItem, deformedItem))
    }

    fun hasRecipesForInput(item: Item, metadata: Int): Boolean {
        return itemRecipeMap.containsKey(Pair(item, metadata))
    }

    fun hasRecipesForInput(itemStack: ItemStack): Boolean {
        return hasRecipesForInput(itemStack.item, itemStack.metadata)
    }

    private fun getRecipesForInput(item: Item, metadata: Int): HashMap<WorkingTemplate, ItemStack>? {
        return itemRecipeMap.get(Pair(item, metadata))
    }

    private fun getRecipesForInput(itemStack: ItemStack): HashMap<WorkingTemplate, ItemStack>? {
        return getRecipesForInput(itemStack.item, itemStack.metadata)
    }

    fun getOutputForConfigurationOrNull(
        itemStack: ItemStack,
        firstTech: WorkingTechnique?,
        secondTech: WorkingTechnique?,
        thirdTech: WorkingTechnique?
    ): ItemStack? {
        val recipes = getRecipesForInput(itemStack) ?: return null
        recipes.entries.forEach { recipe ->
            if (recipe.key.matchesPerformedWork(firstTech, secondTech, thirdTech)) {
                return recipe.value
            }
        }

        return null
    }

    fun addRecipe(inputStackStr: String, outputStackStr: String, workTechs: WorkingTemplate) {
        println(
            "Registering recipe to craft %s using %s as input. Process: %s".format(
                outputStackStr,
                inputStackStr,
                workTechs.toString()
            )
        )

        val inputItemStack = getStackFromConfigString(inputStackStr) ?: return
        val outputItemStack = getStackFromConfigString(outputStackStr) ?: return

        val inputPair = Pair(inputItemStack.item, inputItemStack.metadata)

        if (!itemRecipeMap.containsKey(inputPair)) {
            itemRecipeMap[inputPair] = HashMap()
        }

        val recipeMapForInput = itemRecipeMap[inputPair] ?: HashMap()
        itemRecipeMap[inputPair] = recipeMapForInput

        recipeMapForInput[workTechs] = outputItemStack

        println("Successfully added recipe to craft %s".format(outputStackStr))
    }

    companion object {
        fun getInstance(): SmithingRegistry {
            return SmithcraftingMod.smithingRegistry
        }
    }
}