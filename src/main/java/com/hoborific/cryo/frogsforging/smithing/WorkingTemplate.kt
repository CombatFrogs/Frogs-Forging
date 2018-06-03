package com.hoborific.cryo.frogsforging.smithing

class WorkingTemplate constructor(
    val identifier: String,
    private val firstTech: WorkingTechnique?,
    private val secondTech: WorkingTechnique?,
    private val thirdTech: WorkingTechnique?
) {

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