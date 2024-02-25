package com.coolguy1842.factions.Suggestions;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.suggestion.BlockingSuggestionProvider;
import org.incendo.cloud.suggestion.Suggestion;
import org.incendo.cloud.suggestion.SuggestionProvider;

import com.coolguy1842.factions.Factions;
import com.coolguy1842.factions.Util.PlayerUtil;
import com.coolguy1842.factionscommon.Classes.FactionPlayer;
import com.coolguy1842.factionscommon.Classes.Home;

public class HomeNameSuggestion<C> implements BlockingSuggestionProvider<C> {
    public static enum ParserType {
        FACTION,
        PLAYER
    }

    private ParserType _type;
    public ParserType getType() { return _type; }

    private HomeNameSuggestion(ParserType type) {
        _type = type;
    }

    
    public static<C> @NonNull SuggestionProvider<C> homeNameSuggestion(ParserType type) {
        return new HomeNameSuggestion<>(type);
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
}
