package com.hoborific.cryo.smithcrafting.smithing

class WorkingTemplate constructor(
    val identifier: String,
    private val firstTech: WorkingTechnique?,
    private val secondTech: WorkingTechnique?,
    private val thirdTech: WorkingTechnique?
) {
    enum class WorkingTechnique(val recipeModifier: Int) {

        LIGHT_HIT(3),
        MEDIUM_HIT(6),
        HEAVY_HIT(9),
        DRAW(15),
        PUNCH(-2),
        BEND(-7),
        UPSET(-13),
        SHRINK(-16),
        HIT(0);

        fun matches(technique: WorkingTechnique?): Boolean {
            return this == technique
                    || (this == HIT && arrayOf(LIGHT_HIT, MEDIUM_HIT, HEAVY_HIT).contains(technique))
        }
    }

    override fun toString(): String {
        return "TECHNIQUE ID: %s\tN: %s, N-1: %s, N-2: %s".format(identifier, thirdTech, secondTech, firstTech)
    }

    fun matchesPerformedWork(
        firstTech: WorkingTechnique?,
        secondTech: WorkingTechnique?,
        thirdTech: WorkingTechnique?
    ): Boolean {
        if (this.firstTech != null && !this.firstTech.matches(firstTech)) return false
        if (this.secondTech != null && !this.secondTech.matches(secondTech)) return false
        if (this.thirdTech != null && !this.thirdTech.matches(thirdTech)) return false

        return true
    }
}