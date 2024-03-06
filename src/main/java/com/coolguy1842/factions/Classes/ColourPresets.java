package com.coolguy1842.factions.Classes;

import java.util.HashMap;
import java.util.Map;

import net.kyori.adventure.text.format.TextColor;

public class ColourPresets {
    public static final Map<String, TextColor> colours = new HashMap<>(
        Map.ofEntries(
            Map.entry("black", TextColor.fromHexString("#000000")),
            Map.entry("dark_blue", TextColor.fromHexString("#0000AA")),
            Map.entry("dark_green", TextColor.fromHexString("#00AA00")),
            Map.entry("dark_aqua", TextColor.fromHexString("#00AAAA")),
            Map.entry("dark_red", TextColor.fromHexString("#AA0000")),
            Map.entry("dark_purple", TextColor.fromHexString("#AA00AA")),
            Map.entry("gold", TextColor.fromHexString("#FFAA00")),
            Map.entry("gray", TextColor.fromHexString("#AAAAAA")),
            Map.entry("dark_gray", TextColor.fromHexString("#555555")),
            Map.entry("blue", TextColor.fromHexString("#5555FF")),
            Map.entry("green", TextColor.fromHexString("#55FF55")),
            Map.entry("aqua", TextColor.fromHexString("#55FFFF")),
            Map.entry("red", TextColor.fromHexString("#FF5555")),
            Map.entry("light_purple", TextColor.fromHexString("#FF55FF")),
            Map.entry("yellow", TextColor.fromHexString("#FFFF55")),
            Map.entry("white", TextColor.fromHexString("#FFFFFF"))
        )
    );
}
