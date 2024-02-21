package com.coolguy1842.factions.Requirements.Faction;

import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.context.CommandContext;

import com.coolguy1842.factions.Requirements.Faction.FactionRequirement.Interface;

import net.kyori.adventure.text.Component;

public class DefaultFactionRequirement implements Interface {
    public Map<String, Component> getErrorMessages() {
        return Map.ofEntries(
            Map.entry("notPlayer", Component.text("Only players can use this!")),
            Map.entry("error", Component.text("Error"))
        );
    }

    @Override
    public @NonNull Component errorMessage(final @NonNull CommandContext<CommandSender> ctx) {
        if(!(ctx.sender() instanceof Player)) {
            return getErrorMessages().get("notPlayer");
        }

        return getErrorMessages().get("error");
    }

    @Override
    public boolean evaluateRequirement(final @NonNull CommandContext<CommandSender> ctx) {
        return ctx.sender() instanceof Player;
    }
}
