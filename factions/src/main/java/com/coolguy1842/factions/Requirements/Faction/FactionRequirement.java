package com.coolguy1842.factions.Requirements.Faction;

import java.util.Map;

import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.key.CloudKey;
import org.incendo.cloud.processors.requirements.Requirement;
import org.incendo.cloud.processors.requirements.RequirementFailureHandler;
import org.incendo.cloud.processors.requirements.RequirementPostprocessor;
import org.incendo.cloud.processors.requirements.Requirements;

import com.coolguy1842.factions.Factions;
import com.coolguy1842.factions.Util.MessageUtil;

import io.leangen.geantyref.TypeToken;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class FactionRequirement {
    public static interface Interface extends Requirement<CommandSender, Interface> {
        Map<String, Component> getErrorMessages();
        @NonNull Component errorMessage(final @NonNull CommandContext<CommandSender> ctx);
    }

    public static final class ErrorHandler implements RequirementFailureHandler<CommandSender, Interface> {
        @Override
        public void handleFailure(final @NonNull CommandContext<CommandSender> ctx, final Interface requirement) {
            ctx.sender().sendMessage(
                MessageUtil.format(
                    Component.text("{} {}"),
                    Factions.getPrefix(),
                    requirement.errorMessage(ctx).color(TextColor.color(191, 63, 54))
                )
            );
        }
    }


    public static final CloudKey<Requirements<CommandSender, Interface>>
        REQUIREMENT_KEY = CloudKey.of(
            "requirements",
            new TypeToken<Requirements<CommandSender, Interface>>(){}
    );

    public static final RequirementPostprocessor<CommandSender, Interface> postprocessor = RequirementPostprocessor.of(
        REQUIREMENT_KEY,
        new FactionRequirement.ErrorHandler()
    );
}
