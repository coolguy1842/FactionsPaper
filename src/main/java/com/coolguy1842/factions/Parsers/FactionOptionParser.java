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

import com.coolguy1842.factionscommon.Classes.Faction.Option;

public class FactionOptionParser<C> implements ArgumentParser<C, Option>, BlockingSuggestionProvider<C> {
    @Override
    public @NonNull ArgumentParseResult<@NonNull Option> parse(@NonNull CommandContext<C> ctx, @NonNull CommandInput cmdInput) {
        if(!(ctx.sender() instanceof Player)) {
            return ArgumentParseResult.failure(new FactionOptionParseException(ParserCaptions.Keys.NOT_PLAYER, "", ctx));
        }

        String optionName = cmdInput.readString();

        try {
            Option option = Option.valueOf(optionName.toUpperCase());
            return ArgumentParseResult.success(option);
        }
        catch(IllegalArgumentException e) {
            return ArgumentParseResult.failure(new FactionOptionParseException(ParserCaptions.Keys.Faction.Option.INVALID, optionName, ctx));
        }
    }

    @Override
    public @NonNull Iterable<@NonNull Suggestion> suggestions(@NonNull CommandContext<C> ctx, @NonNull CommandInput input) {
        if(!(ctx.sender() instanceof Player)) {
            return List.of();
        }

        return new ArrayList<Suggestion>(
            Stream.of(Option.values())
                .map(x -> Suggestion.simple(x.name().toLowerCase()))
                .toList()
        );
    }


    public static<C> @NonNull ParserDescriptor<C, Option> factionOptionParser() {
        return ParserDescriptor.of(new FactionOptionParser<>(), Option.class);
    }

    
    public static final class FactionOptionParseException extends ParserException {
        private final Caption reason;
        private final String input;

        public FactionOptionParseException(final @NonNull Caption reason, final @NonNull String input, final @NonNull CommandContext<?> context) {
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
