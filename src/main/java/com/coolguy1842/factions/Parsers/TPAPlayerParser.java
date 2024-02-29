package com.coolguy1842.factions.Parsers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bukkit.Bukkit;
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

import com.coolguy1842.factions.Managers.TPAManager;
import com.coolguy1842.factions.Managers.TPAManager.TPARequest;
import com.coolguy1842.factions.Managers.TPAManager.TPARequestType;

public class TPAPlayerParser<C> implements ArgumentParser<C, Player>, BlockingSuggestionProvider<C> {
    private TPARequestType _type;
    public TPARequestType getType() { return _type; }

    private TPAPlayerParser(TPARequestType type) {
        _type = type;
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull Player> parse(@NonNull CommandContext<C> ctx, @NonNull CommandInput cmdInput) {
        if(!(ctx.sender() instanceof Player)) {
            return ArgumentParseResult.failure(new TPAPlayerParseException(ParserCaptions.Keys.NOT_PLAYER, "", ctx));
        }

        Player player = (Player)ctx.sender();

        String input = cmdInput.readString();
        Player requested = Bukkit.getPlayer(input);

        if(requested == null) {
            return ArgumentParseResult.failure(new TPAPlayerParseException(ParserCaptions.Keys.TPA.Player.INVALID, input, ctx));
        }

        if(requested.getUniqueId().equals(player.getUniqueId())) {
            return ArgumentParseResult.failure(new TPAPlayerParseException(ParserCaptions.Keys.TPA.Player.SELF, "", ctx));
        }

        Optional<TPARequest> request = TPAManager.getInstance().getRequest(requested.getUniqueId());
        if(request.isPresent()) {
            if(request.get().getSender().equals(player.getUniqueId()) && request.get().getType().equals(getType())) {
                return ArgumentParseResult.failure(new TPAPlayerParseException(ParserCaptions.Keys.TPA.Player.HAS_REQUEST, input, ctx));
            }
        }

        return ArgumentParseResult.success(requested);
    }

    @Override
    public @NonNull Iterable<@NonNull Suggestion> suggestions(@NonNull CommandContext<C> ctx, @NonNull CommandInput input) {
        if(!(ctx.sender() instanceof Player)) {
            return List.of();
        }

        List<Suggestion> out = new ArrayList<>();
        
        Player sender = (Player)ctx.sender();
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(player.getUniqueId().equals(sender.getUniqueId())) continue;

            Optional<TPARequest> request = TPAManager.getInstance().getRequest(player.getUniqueId());
            if(request.isPresent()) {
                if(request.get().getSender().equals(sender.getUniqueId()) && request.get().getType().equals(this.getType())) {
                    continue;
                }
            }

            out.add(Suggestion.simple(player.getName()));
        }

        return out;
    }


    public static<C> @NonNull ParserDescriptor<C, Player> tpaPlayerParser(TPARequestType type) {
        return ParserDescriptor.of(new TPAPlayerParser<>(type), Player.class);
    }

    
    public static final class TPAPlayerParseException extends ParserException {
        private final Caption reason;
        private final String input;

        public TPAPlayerParseException(final @NonNull Caption reason, final @NonNull String input, final @NonNull CommandContext<?> context) {
            super(
                TPAPlayerParser.class,
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
