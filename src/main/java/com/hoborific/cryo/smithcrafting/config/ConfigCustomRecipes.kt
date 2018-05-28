package com.hoborific.cryo.smithcrafting.config

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.hoborific.cryo.smithcrafting.SmithcraftingMod
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import java.io.File
import java.io.FileReader

class ConfigCustomRecipes private constructor() {

    private object Holder {
        val INSTANCE = ConfigCustomRecipes()
    }

    companion object {
        val instance: ConfigCustomRecipes by lazy { Holder.INSTANCE }
    }

    fun initializeRecipes(e: FMLPreInitializationEvent) {
        val configurationFile = File(e.modConfigurationDirectory, "smithcrafting_recipes.json")
        if (!configurationFile.exists()) configurationFile.createNewFile()

        val configRoot = JsonParser().parse(FileReader(configurationFile)) as? JsonObject ?: return

        val configTemplates = configRoot["templates"]
        if (configTemplates is JsonObject) {
            configTemplates.entrySet().forEach processTemplate@{
                val identifier = it.key
                val workTechs = it.value as? JsonArray ?: return@processTemplate

                SmithcraftingMod.smithingRegistry.registerTemplate(
                    identifier,
                    workTechs[0].asString,
                    workTechs[1].asString,
                    workTechs[2].asString
                )
            }
        }

        val configRecipes = configRoot["recipes"]
        if (configRecipes is JsonArray) {
            configRecipes.forEach processRecipe@{
                val recipe = it as? JsonObject ?: return@processRecipe
                val inputStackStr = recipe["input"]?.asString ?: return@processRecipe
                val outputStackStr = recipe["output"]?.asString ?: return@processRecipe
                val auxStackStr = recipe["auxiliary"]?.asString ?: return@processRecipe
                val template = recipe["process"]?.asString ?: return@processRecipe
                val workTechs =
                    SmithcraftingMod.smithingRegistry.getTemplateWithIdentifier(template) ?: return@processRecipe

                SmithcraftingMod.smithingRegistry.addRecipe(inputStackStr, outputStackStr, workTechs)
            }
        }

        val deformationMap = configRoot["deformation"]
        if (deformationMap is JsonObject) {
            deformationMap.entrySet().forEach processDeformation@{
                if (it.key !is String) return@processDeformation
                if (it.value?.asString !is String) return@processDeformation
                SmithcraftingMod.smithingRegistry.registerDeformation(it.key, it.value.asString)
            }
        }
    }
}
/*
{
    "recipes": [
        {
            "input": "minecraft:iron_ingot",
            "output": "smithcraft:iron_pickaxe_head",
            "process": "pickaxe_head",
            "auxiliary": "smithcraft:flux_dust"
        },
        {
            "input": "minecraft:steel_ingot",
            "output": "smithcraft:steel_pickaxe_head",
            "process": "pickaxe_head",
            "auxiliary": "smithcraft:flux_dust"
        },
        {
            "input": "minecraft:iron_ingot",
            "output": "minecraft:bucket",
            "process": ["hit", null, null],
            "auxiliary": "smithcraft:flux_dust"
        }
    ],
    "templates": {
        "pickaxe_head": ["hit", "hit", "punch"]
    },
    "deformation": {
        "minecraft:iron_ingot": "smithcraft:deformed_iron_ingot",
        "minecraft:steel_ingot": "smithcraft:deformed_steel_ingot"
    }
}
 */