package com.coolguy1842.factions.Parsers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
import com.coolguy1842.factions.Parsers.FactionPlayerParser.FactionPlayerParseException;
import com.coolguy1842.factions.Util.PlayerUtil;
import com.coolguy1842.factionscommon.Classes.Faction;
import com.coolguy1842.factionscommon.Classes.FactionPlayer;

public class FactionParser<C> implements ArgumentParser<C, Faction>, BlockingSuggestionProvider<C> {
    public static enum ParserType {
        NOT_INCLUDES_OWN,
        HAS_INVITE,
        ANY
    }

    private List<ParserType> _types;
    public List<ParserType> getType() { return _types; }

    private FactionParser(ParserType... types) {
        if(types != null) _types = Arrays.asList(types);
        else _types = List.of();
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull Faction> parse(@NonNull CommandContext<C> ctx, @NonNull CommandInput cmdInput) {
        if(!(ctx.sender() instanceof Player)) {
            return ArgumentParseResult.failure(new FactionPlayerParseException(ParserCaptions.Keys.NOT_PLAYER, "", ctx));
        }

        Player player = (Player)ctx.sender();
        FactionPlayer factionPlayer = PlayerUtil.getFactionPlayer(player.getUniqueId());
        
        String input = cmdInput.readString();
        Optional<Faction> factionOptional = Factions.getFactionsCommon().factionManager.getFaction(input);
        if(!factionOptional.isPresent()) {
            return ArgumentParseResult.failure(new FactionParseException(ParserCaptions.Keys.Faction.INVALID, input, ctx));
        }

        Faction faction = factionOptional.get();
        if(_types.contains(ParserType.NOT_INCLUDES_OWN) && factionPlayer.getFaction() != null && factionPlayer.getFaction().equals(faction.getID())) {
            return ArgumentParseResult.failure(new FactionParseException(ParserCaptions.Keys.Faction.NOT_OWN, input, ctx));
        }
        
        if(_types.contains(ParserType.HAS_INVITE) && !Factions.getFactionsCommon().inviteManager.getInvite(faction.getID(), player.getUniqueId()).isPresent()) {
            return ArgumentParseResult.failure(new FactionParseException(ParserCaptions.Keys.Faction.NO_INVITE, input, ctx));
        }

        return ArgumentParseResult.success(faction);
    }

    @Override
    public @NonNull Iterable<@NonNull Suggestion> suggestions(@NonNull CommandContext<C> ctx, @NonNull CommandInput input) {
        if(!(ctx.sender() instanceof Player)) {
            return List.of();
        }

        List<Suggestion> out = new ArrayList<>();
        
        Player player = (Player)ctx.sender();
        FactionPlayer factionPlayer = PlayerUtil.getFactionPlayer(player.getUniqueId());

        for(Faction faction : Factions.getFactionsCommon().factionManager.getFactions()) {
            if(_types.contains(ParserType.NOT_INCLUDES_OWN) && factionPlayer.getFaction() != null && factionPlayer.getFaction().equals(faction.getID())) {
                continue;
            }

            if(_types.contains(ParserType.HAS_INVITE) && !Factions.getFactionsCommon().inviteManager.getInvite(faction.getID(), player.getUniqueId()).isPresent()) {
                continue;
            }
            
            out.add(Suggestion.simple(faction.getName()));
        }

        return out;
    }


    public static<C> @NonNull ParserDescriptor<C, Faction> anyFaction() {
        return ParserDescriptor.of(new FactionParser<>(ParserType.ANY), Faction.class);
    }
    
    public static<C> @NonNull ParserDescriptor<C, Faction> invitingFaction() {
        return ParserDescriptor.of(new FactionParser<>(ParserType.HAS_INVITE), Faction.class);
    }
    
    public static<C> @NonNull ParserDescriptor<C, Faction> notOwnFaction() {
        return ParserDescriptor.of(new FactionParser<>(ParserType.NOT_INCLUDES_OWN), Faction.class);
    }

    
    public static final class FactionParseException extends ParserException {
        private final Caption reason;
        private final String input;

        public FactionParseException(final @NonNull Caption reason, final @NonNull String input, final @NonNull CommandContext<?> context) {
            super(
                FactionParser.class,
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
