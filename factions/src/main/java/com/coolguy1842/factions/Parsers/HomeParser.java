package com.coolguy1842.factions.Parsers;

import java.util.ArrayList;
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
import com.coolguy1842.factions.Util.PlayerUtil;
import com.coolguy1842.factionscommon.Classes.Faction;
import com.coolguy1842.factionscommon.Classes.FactionPlayer;
import com.coolguy1842.factionscommon.Classes.Home;

public class HomeParser<C> implements ArgumentParser<C, Home>, BlockingSuggestionProvider<C> {
    public static enum ParserType {
        FACTION,
        PLAYER
    }

    private ParserType _type;
    public ParserType getType() { return _type; }

    private HomeParser(ParserType type) {
        _type = type;
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull Home> parse(@NonNull CommandContext<C> ctx, @NonNull CommandInput cmdInput) {
        if(!(ctx.sender() instanceof Player)) {
            return ArgumentParseResult.failure(new HomeParseException(ParserCaptions.Keys.NOT_PLAYER, "", ctx));
        }

        Player sender = (Player)ctx.sender();
        FactionPlayer factionPlayer = PlayerUtil.getFactionPlayer(sender.getUniqueId());
        
        String homeName = cmdInput.readString();

        if(_type == ParserType.FACTION) {
            Optional<Faction> factionOptional = Factions.getFactionsCommon().factionManager.getFaction(factionPlayer.getFaction());

            if(factionPlayer.getFaction() == null || !factionOptional.isPresent()) {
                return ArgumentParseResult.failure(new HomeParseException(ParserCaptions.Keys.Home.NO_FACTION, "", ctx));    
            }

            Faction faction = factionOptional.get();
            Optional<Home> home = Factions.getFactionsCommon().homeManager.getHome(faction.getID(), homeName);
            if(!home.isPresent()) {
                return ArgumentParseResult.failure(new HomeParseException(ParserCaptions.Keys.Home.INVALID, homeName, ctx));
            }

            return ArgumentParseResult.success(home.get());
        }
        

        Optional<Home> home = Factions.getFactionsCommon().homeManager.getHome(sender.getUniqueId(), homeName);
        if(!home.isPresent()) {
            return ArgumentParseResult.failure(new HomeParseException(ParserCaptions.Keys.Home.INVALID, homeName, ctx));
        }

        return ArgumentParseResult.success(home.get());
    }

    @Override
    public @NonNull Iterable<@NonNull Suggestion> suggestions(@NonNull CommandContext<C> ctx, @NonNull CommandInput input) {
        if(!(ctx.sender() instanceof Player)) {
            return List.of();
        }

        List<Suggestion> out = new ArrayList<>();
        
        Player player = (Player)ctx.sender();
        FactionPlayer factionPlayer = PlayerUtil.getFactionPlayer(player.getUniqueId());
        if(_type == ParserType.FACTION && factionPlayer.getFaction() == null) return out;

        List<Home> homes;

        switch (_type) {
            case FACTION: homes = Factions.getFactionsCommon().homeManager.getHomesWithOwner(factionPlayer.getFaction()); break;
            case PLAYER:  homes = Factions.getFactionsCommon().homeManager.getHomesWithOwner(factionPlayer.getID());  break;   
            default: return List.of();
        }

        for(Home home : homes) {
            out.add(Suggestion.simple(home.getName()));
        }

        return out;
    }


    public static<C> @NonNull ParserDescriptor<C, Home> homeParser(ParserType type) {
        return ParserDescriptor.of(new HomeParser<>(type), Home.class);
    }

    
    public static final class HomeParseException extends ParserException {
        private final Caption reason;
        private final String input;

        public HomeParseException(final @NonNull Caption reason, final @NonNull String input, final @NonNull CommandContext<?> context) {
            super(
                HomeParser.class,
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
