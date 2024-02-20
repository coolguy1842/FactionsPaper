package com.coolguy1842.factions.Parsers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

import com.coolguy1842.factions.Factions;
import com.coolguy1842.factions.Util.PlayerUtil;
import com.coolguy1842.factionscommon.Classes.FactionPlayer;

public class FactionPlayerParser<C> implements ArgumentParser<C, Player>, BlockingSuggestionProvider<C> {
    public static enum ParserType {
        INCLUDES_SELF,
        IN_FACTION,
        NOT_IN_FACTION,
        HAS_INVITE,
        HAS_NO_INVITE
    }

    private List<ParserType> _types;
    public List<ParserType> getType() { return _types; }

    private FactionPlayerParser(ParserType... types) {
        if(types != null) _types = Arrays.asList(types);
        else _types = List.of();
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull Player> parse(@NonNull CommandContext<C> ctx, @NonNull CommandInput cmdInput) {
        if(!(ctx.sender() instanceof Player)) {
            return ArgumentParseResult.failure(new FactionPlayerParseException(ParserCaptions.Keys.NOT_PLAYER, "", ctx));
        }

        Player sender = (Player)ctx.sender();
        
        String input = cmdInput.readString();
        Player player = Bukkit.getPlayerExact(input);
        
        if(player == null || !player.isOnline() || !sender.canSee(player)) {
            return ArgumentParseResult.failure(new FactionPlayerParseException(ParserCaptions.Keys.FactionPlayer.INVALID, input, ctx));
        }

        if(!_types.contains(ParserType.INCLUDES_SELF) && player.getUniqueId().equals(sender.getUniqueId())) {
            return ArgumentParseResult.failure(new FactionPlayerParseException(ParserCaptions.Keys.FactionPlayer.SELF, input, ctx));
        }
        
        Boolean inFaction = PlayerUtil.getFactionPlayer(player.getUniqueId()).getFaction() != null;
        if(_types.contains(ParserType.IN_FACTION) && !inFaction) {
            return ArgumentParseResult.failure(new FactionPlayerParseException(ParserCaptions.Keys.FactionPlayer.NOT_IN_FACTION, input, ctx));
        }

        if(_types.contains(ParserType.NOT_IN_FACTION) && inFaction) {
            return ArgumentParseResult.failure(new FactionPlayerParseException(ParserCaptions.Keys.FactionPlayer.IN_FACTION, input, ctx));
        }
        
        FactionPlayer senderFactionPlayer = PlayerUtil.getFactionPlayer(sender.getUniqueId());
        Boolean hasInvite = Factions.getFactionsCommon().inviteManager.getInvite(senderFactionPlayer.getFaction(), player.getUniqueId()).isPresent();
        if(_types.contains(ParserType.HAS_INVITE) && !hasInvite) {
            return ArgumentParseResult.failure(new FactionPlayerParseException(ParserCaptions.Keys.FactionPlayer.HAS_NO_INVITE, input, ctx));
        }

        if(_types.contains(ParserType.HAS_NO_INVITE) && hasInvite) {
            return ArgumentParseResult.failure(new FactionPlayerParseException(ParserCaptions.Keys.FactionPlayer.HAS_INVITE, input, ctx));
        }

        return ArgumentParseResult.success(player);
    }

    @Override
    public @NonNull Iterable<@NonNull Suggestion> suggestions(@NonNull CommandContext<C> ctx, @NonNull CommandInput input) {
        if(!(ctx.sender() instanceof Player)) {
            return List.of();
        }
        
        List<Suggestion> out = new ArrayList<>();

        
        Player sender = (Player)ctx.sender();
        FactionPlayer senderFactionPlayer = PlayerUtil.getFactionPlayer(sender.getUniqueId());

        for(Player player : Bukkit.getOnlinePlayers()) {
            if(!_types.contains(ParserType.INCLUDES_SELF) && player.getUniqueId().equals(sender.getUniqueId())) continue;
            if(!sender.canSee(player)) continue;

            Boolean inFaction = PlayerUtil.getFactionPlayer(player.getUniqueId()).getFaction() != null;
            if(_types.contains(ParserType.IN_FACTION) && !inFaction) continue;
            if(_types.contains(ParserType.NOT_IN_FACTION) && inFaction) continue;
            
            Boolean hasInvite = Factions.getFactionsCommon().inviteManager.getInvite(senderFactionPlayer.getFaction(), player.getUniqueId()).isPresent();
            if(_types.contains(ParserType.HAS_INVITE) && !hasInvite) continue;
            if(_types.contains(ParserType.HAS_NO_INVITE) && hasInvite) continue;

            out.add(Suggestion.simple(player.getName()));
        }

        return out;
    }


    public static<C> @NonNull ParserDescriptor<C, Player> notInFaction() {
        return ParserDescriptor.of(new FactionPlayerParser<>(ParserType.NOT_IN_FACTION), Player.class);
    }

    public static<C> @NonNull ParserDescriptor<C, Player> notInFactionHasNoInvite() {
        return ParserDescriptor.of(new FactionPlayerParser<>(ParserType.NOT_IN_FACTION, ParserType.HAS_NO_INVITE), Player.class);
    }

    public static<C> @NonNull ParserDescriptor<C, Player> notInFactionHasInvite() {
        return ParserDescriptor.of(new FactionPlayerParser<>(ParserType.NOT_IN_FACTION, ParserType.HAS_INVITE), Player.class);
    }

    
    public static<C> @NonNull ParserDescriptor<C, Player> inFaction() {
        return ParserDescriptor.of(new FactionPlayerParser<>(ParserType.IN_FACTION), Player.class);
    }
    

    public static final class FactionPlayerParseException extends ParserException {
        private final Caption reason;
        private final String input;

        public FactionPlayerParseException(final @NonNull Caption reason, final @NonNull String input, final @NonNull CommandContext<?> context) {
            super(
                FactionPlayerParser.class,
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
