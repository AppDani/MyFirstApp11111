package com.danielarog.myfirstapp.models

enum class ProductCategory(val value: String) {
    SHOES("shoes"),
    SKIRTS("skirts"),
    PANTS("pants"),
    SHIRTS("shirts"),
    DRESSES("dresses"),
    JACKETS("jackets"),
    ACCESSORIES("accessories"),
    JEWELERY("jewelery"),
    SWIM("swim");


    fun getSubCategories() : List<SubCategory> {
        return when(this) {
            SHOES -> listOf(
                SubCategory.SANDALS ,
                SubCategory.SNICKERS,
                SubCategory.BOOTS,
                SubCategory.PLATFORM
            )
            SHIRTS -> listOf(
                SubCategory.TANK_TOP,
                SubCategory.CROP,
                SubCategory.SHORT_SLEEVE,
                SubCategory.LONG_SLEEVE,
                SubCategory.BUTTON_DOWN
            )
            SKIRTS -> listOf(
                SubCategory.MINI,
                SubCategory.MIDI,
                SubCategory.MAXI
            )
            DRESSES -> listOf(
                SubCategory.MINI,
                SubCategory.MIDI,
                SubCategory.MAXI
            )
            PANTS -> listOf(
                SubCategory.SHORTS,
                SubCategory.JEANS,
                SubCategory.CASUAL,
                SubCategory.FORMAL,
                SubCategory.SKORT
            )
            JACKETS -> listOf(
                SubCategory.COATS,
                SubCategory.JACKETS,
                SubCategory.BLAZERS
            )
            ACCESSORIES -> listOf(
                SubCategory.HATS,
                SubCategory.SCARVES,
                SubCategory.BAGS,
                SubCategory.BELTS,
                SubCategory.SUNGLASSES


                )
            JEWELERY -> listOf(
                SubCategory.NECKLACES,
                SubCategory.BRACELETS,
                SubCategory.RINGS,
                SubCategory.WATCHES
            )
            SWIM -> listOf(
                SubCategory.ONE_PIECE,
                SubCategory.TWO_PIECE
            )

            else -> listOf()
        }
    }

    enum class SubCategory (val value: String) {
        ALL("all"),
        MALE("male"),
        FEMALE("female"),

        SANDALS("sandals"),
        SNICKERS("snickers"),
        BOOTS("boots"),
        PLATFORM("platforms"),

        MINI("mini"),
        MIDI("midi"),
        MAXI("maxi"),

        SHORTS("shorts"),
        JEANS("jeans"),
        CASUAL("casual"),
        FORMAL("formal"),
        SKORT("skort"),

        TANK_TOP("tank top"),
        CROP("crop"),
        SHORT_SLEEVE("short sleeve"),
        LONG_SLEEVE("long sleeve"),
        BUTTON_DOWN("button down"),

        COATS("coats"),
        JACKETS("jackets"),
        BLAZERS("blazers"),


        HATS("hats"),
        SCARVES("scarves"),
        BAGS("bags"),
        BELTS("belts"),
        SUNGLASSES("sunglasses"),

        NECKLACES("necklaces"),
        BRACELETS("bracelets"),
        RINGS("rings"),
        WATCHES("watches"),

        TWO_PIECE("two piece"),
        ONE_PIECE("one piece"),
    }
}