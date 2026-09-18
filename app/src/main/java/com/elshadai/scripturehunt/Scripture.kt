package com.elshadai.scripturehunt

/**
 * Sample verses for the hunt. Text is public-domain King James Version (KJV).
 */
data class Scripture(
    val id: String,
    val reference: String,
    val text: String,
    val theme: String,
)

object ScriptureCatalog {
    val all: List<Scripture> = listOf(
        Scripture(
            id = "jn3_16",
            reference = "John 3:16",
            text = "For God so loved the world, that he gave his only begotten Son, " +
                "that whosoever believeth in him should not perish, but have everlasting life.",
            theme = "Love",
        ),
        Scripture(
            id = "ps23_1",
            reference = "Psalm 23:1",
            text = "The Lord is my shepherd; I shall not want.",
            theme = "Provision",
        ),
        Scripture(
            id = "phil4_13",
            reference = "Philippians 4:13",
            text = "I can do all things through Christ which strengtheneth me.",
            theme = "Strength",
        ),
        Scripture(
            id = "prov3_5",
            reference = "Proverbs 3:5",
            text = "Trust in the Lord with all thine heart; and lean not unto thine own understanding.",
            theme = "Trust",
        ),
        Scripture(
            id = "matt5_14",
            reference = "Matthew 5:14",
            text = "Ye are the light of the world. A city that is set on an hill cannot be hid.",
            theme = "Witness",
        ),
        Scripture(
            id = "isa40_31",
            reference = "Isaiah 40:31",
            text = "But they that wait upon the Lord shall renew their strength; " +
                "they shall mount up with wings as eagles; they shall run, and not be weary; " +
                "and they shall walk, and not faint.",
            theme = "Hope",
        ),
        Scripture(
            id = "josh1_9",
            reference = "Joshua 1:9",
            text = "Have not I commanded thee? Be strong and of a good courage; " +
                "be not afraid, neither be thou dismayed: for the Lord thy God is with thee whithersoever thou goest.",
            theme = "Courage",
        ),
        Scripture(
            id = "rom8_28",
            reference = "Romans 8:28",
            text = "And we know that all things work together for good to them that love God, " +
                "to them who are the called according to his purpose.",
            theme = "Purpose",
        ),
        Scripture(
            id = "ps46_10",
            reference = "Psalm 46:10",
            text = "Be still, and know that I am God: I will be exalted among the heathen, " +
                "I will be exalted in the earth.",
            theme = "Peace",
        ),
        Scripture(
            id = "mic6_8",
            reference = "Micah 6:8",
            text = "He hath shewed thee, O man, what is good; and what doth the Lord require of thee, " +
                "but to do justly, and to love mercy, and to walk humbly with thy God?",
            theme = "Mercy",
        ),
        Scripture(
            id = "heb11_1",
            reference = "Hebrews 11:1",
            text = "Now faith is the substance of things hoped for, the evidence of things not seen.",
            theme = "Faith",
        ),
        Scripture(
            id = "jer29_11",
            reference = "Jeremiah 29:11",
            text = "For I know the thoughts that I think toward you, saith the Lord, " +
                "thoughts of peace, and not of evil, to give you an expected end.",
            theme = "Promise",
        ),
    )

    fun byId(id: String): Scripture? = all.find { it.id == id }
}
