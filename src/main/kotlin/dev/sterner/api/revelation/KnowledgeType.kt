package dev.sterner.api.revelation

enum class KnowledgeType {
    WEEPING_WELL,
    END,
    NETHER,
    GRIMCULT,
    ICHOR;

    companion object {
        val prerequisites = mapOf(
            NETHER to setOf(WEEPING_WELL),
            END to setOf(WEEPING_WELL),
            GRIMCULT to setOf(END, NETHER),
            ICHOR to setOf(GRIMCULT)
        )
    }
}
