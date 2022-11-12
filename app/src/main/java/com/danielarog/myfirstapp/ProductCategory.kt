package com.danielarog.myfirstapp

enum class ProductCategory(val value: String) {
    SHOES("shoes"),
    SKIRTS("skirts"),
    PANTS("pants"),
    SHIRTS("shirts"),
    DRESSES("dresses"),
    ACCESSORIES("accessories"),
    JEWELERY("jewelery"),
    JACKETS("jackets"),
    SWIM("swim");


    enum class SubCategory (val value: String) {
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

        HATS("hats"),
        SCARVES("scarves"),
        BAGS("bags"),

        NECKLACES("necklaces"),
        BRACELETS("bracelets"),
        RINGS("rings"),
        WATCHES("watches"),

    }
}