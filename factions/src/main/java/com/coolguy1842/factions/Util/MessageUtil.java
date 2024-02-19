package com.coolguy1842.factions.Util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextReplacementConfig;

public class MessageUtil {
    public static Component combine(Component... components) {
        Component out = Component.empty();

        for(Component component : components) {
            out = out.append(component);
        }

        return out;
    }

    public static Component format(Component format, ComponentLike... args) {
        Component out = format;

        for(ComponentLike arg : args) {
            out = out.replaceText(
                TextReplacementConfig.builder()
                    .matchLiteral("{}")
                    .replacement(arg)
                    .times(1)
                    .build()
            );
        }

        return out;
    }
}
