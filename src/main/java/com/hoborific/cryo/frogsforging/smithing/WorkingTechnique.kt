package com.hoborific.cryo.frogsforging.smithing

enum class WorkingTechnique(
    val recipeModifier: Int,
    val spriteName: String?
) {

    LIGHT_HIT(3, "light_hit"),
    MEDIUM_HIT(6, "medium_hit"),
    HEAVY_HIT(9, "heavy_hit"),
    DRAW(15, "draw"),
    PUNCH(-2, "punch"),
    BEND(-7, "bend"),
    UPSET(-13, "upset"),
    SHRINK(-16, "shrink"),
    HIT(0, "light_hit"),
    ANY(0, null);

    fun matches(technique: WorkingTechnique?): Boolean {
        return this == technique ||
            (this == HIT && arrayOf(
                LIGHT_HIT,
                MEDIUM_HIT,
                HEAVY_HIT
            ).contains(technique)) ||
            this == ANY
    }

    companion object {
        internal fun fromByte(nbtValue: Byte): WorkingTechnique? {
            if (nbtValue !in 0..WorkingTechnique.values().size) return ANY
            return WorkingTechnique.values()[nbtValue.toInt()]
        }
    }
}