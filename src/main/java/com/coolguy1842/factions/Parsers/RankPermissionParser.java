package com.coolguy1842.factions.Parsers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.caption.Caption;
import org.incendo.cloud.caption.CaptionVariable;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.exception.parsing.ParserException;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.suggestion.BlockingSuggestionProvider;
import org.incendo.cloud.suggestion.Suggestion;

import com.coolguy1842.factionscommon.Classes.Rank.RankPermission;

public class RankPermissionParser<C> implements ArgumentParser<C, RankPermission>, BlockingSuggestionProvider<C> {
    @Override
    public @NonNull ArgumentParseResult<@NonNull RankPermission> parse(@NonNull CommandContext<C> ctx, @NonNull CommandInput cmdInput) {
        if(!(ctx.sender() instanceof Player)) {
            return ArgumentParseResult.failure(new RankPermissionParseException(ParserCaptions.Keys.NOT_PLAYER, "", ctx));
        }

        String permissionName = cmdInput.readString();

        try {
            RankPermission permission = RankPermission.valueOf(permissionName.toUpperCase());
            return ArgumentParseResult.success(permission);
        }
        catch(IllegalArgumentException e) {
            return ArgumentParseResult.failure(new RankPermissionParseException(ParserCaptions.Keys.Rank.Permission.INVALID, permissionName, ctx));
        }
    }

    @Override
    public @NonNull Iterable<@NonNull Suggestion> suggestions(@NonNull CommandContext<C> ctx, @NonNull CommandInput input) {
        if(!(ctx.sender() instanceof Player)) {
            return List.of();
        }

        return new ArrayList<Suggestion>(
            Stream.of(RankPermission.values())
                .map(x -> Suggestion.simple(x.name().toLowerCase()))
                .toList()
        );
    }


    public static<C> @NonNull ParserDescriptor<C, RankPermission> rankPermissionParser() {
        return ParserDescriptor.of(new RankPermissionParser<>(), RankPermission.class);
    }

    
    public static final class RankPermissionParseException extends ParserException {
        private final Caption reason;
        private final String input;

        public RankPermissionParseException(final @NonNull Caption reason, final @NonNull String input, final @NonNull CommandContext<?> context) {
            super(
                RankParser.class,
                context,
                reason,
                CaptionVariable.of("input", input)
            );

            this.reason = reason;
            this.input = input;
        }

        public @NonNull Caption reason() {
            return this.reason;
        }

        public @NonNull String input() {
            return this.input;
        }
    }
}
