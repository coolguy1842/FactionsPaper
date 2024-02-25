package com.coolguy1842.factions.Parsers;

import java.util.List;

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

import com.coolguy1842.factions.Factions;
import com.coolguy1842.factionscommon.Databases.DatabaseHandler;

public class DatabaseParser<C> implements ArgumentParser<C, DatabaseHandler>, BlockingSuggestionProvider<C> {
    @Override
    public @NonNull ArgumentParseResult<@NonNull DatabaseHandler> parse(@NonNull CommandContext<C> ctx, @NonNull CommandInput cmdInput) {
        String input = cmdInput.readString();
        switch (input) {
        case "players": return ArgumentParseResult.success(Factions.getFactionsCommon().playerManager.database);
        case "factions": return ArgumentParseResult.success(Factions.getFactionsCommon().factionManager.database);
        case "ranks": return ArgumentParseResult.success(Factions.getFactionsCommon().rankManager.database);
        case "invites": return ArgumentParseResult.success(Factions.getFactionsCommon().inviteManager.database);
        case "homes": return ArgumentParseResult.success(Factions.getFactionsCommon().homeManager.database);
        case "vaults": return ArgumentParseResult.success(Factions.getFactionsCommon().vaultManager.database);
        case "claims": return ArgumentParseResult.success(Factions.getFactionsCommon().claimManager.database);
        default: return ArgumentParseResult.failure(new DatabaseParseException(ParserCaptions.Keys.Database.INVALID, input, ctx));
        }
    }

    @Override
    public @NonNull Iterable<@NonNull Suggestion> suggestions(@NonNull CommandContext<C> ctx, @NonNull CommandInput input) {
        return List.of(
            Suggestion.simple("players"),
            Suggestion.simple("factions"),
            Suggestion.simple("ranks"),
            Suggestion.simple("invites"),
            Suggestion.simple("homes"),
            Suggestion.simple("vaults"),
            Suggestion.simple("claims")
        );
    }


    public static<C> @NonNull ParserDescriptor<C, DatabaseHandler> databaseParser() {
        return ParserDescriptor.of(new DatabaseParser<>(), DatabaseHandler.class);
    }

    
    public static final class DatabaseParseException extends ParserException {
        private final Caption reason;
        private final String input;

        public DatabaseParseException(final @NonNull Caption reason, final @NonNull String input, final @NonNull CommandContext<?> context) {
            super(
                DatabaseParser.class,
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
