package com.coolguy1842.factions.Util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;

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
    
    public static Component format(String format, ComponentLike... args) {
        return format(Component.text(format), args);
    }


    public static Component getAcceptDeny(ClickEvent acceptClickEvent, HoverEvent<?> acceptHoverEvent, ClickEvent rejectClickEvent, HoverEvent<?> rejectHoverEvent) {
        // add color
        return 
            MessageUtil.combine(
                Component.text("[").color(TextColor.color(160, 160, 160)).hoverEvent(acceptHoverEvent).clickEvent(acceptClickEvent).append(
                Component.text("Accept").color(TextColor.color(85, 255, 85))).append(
                Component.text("]").color(TextColor.color(160, 160, 160))),
                
                Component.text(" "),

                Component.text("[").color(TextColor.color(160, 160, 160)).hoverEvent(rejectHoverEvent).clickEvent(rejectClickEvent).append(
                Component.text("Reject").color(TextColor.color(251, 84, 84))).append(
                Component.text("]").color(TextColor.color(160, 160, 160)))
                
            );
    }
}
