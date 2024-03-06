package com.coolguy1842.factions.Parsers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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

public class FactionPlayerParser<C> implements ArgumentParser<C, FactionPlayer>, BlockingSuggestionProvider<C> {
    public static enum ParserType {
        INCLUDES_SELF,
        IN_FACTION,
        IN_SAME_FACTION,
        NOT_IN_FACTION,
        HAS_INVITE,
        HAS_NO_INVITE,
        NOT_LEADER,
        INCLUDES_OFFLINE
    }

    private List<ParserType> _types;
    public List<ParserType> getType() { return _types; }

    private FactionPlayerParser(ParserType... types) {
        if(types != null) _types = Arrays.asList(types);
        else _types = List.of();
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull FactionPlayer> parse(@NonNull CommandContext<C> ctx, @NonNull CommandInput cmdInput) {
        if(!(ctx.sender() instanceof Player)) {
            return ArgumentParseResult.failure(new FactionPlayerParseException(ParserCaptions.Keys.NOT_PLAYER, "", ctx));
        }

        Player sender = (Player)ctx.sender();
        FactionPlayer senderFactionPlayer = PlayerUtil.getFactionPlayer(sender.getUniqueId());
        
        String input = cmdInput.readString();
        OfflinePlayer player = Bukkit.getOfflinePlayer(input);

        if(input == null || input.isEmpty() || player == null || ((!_types.contains(ParserType.INCLUDES_OFFLINE) && !player.isOnline()) || (_types.contains(ParserType.INCLUDES_OFFLINE) && !player.isOnline() && !player.hasPlayedBefore()))) {
            return ArgumentParseResult.failure(new FactionPlayerParseException(ParserCaptions.Keys.FactionPlayer.INVALID, input, ctx));
        }

        if(!_types.contains(ParserType.INCLUDES_SELF) && player.getUniqueId().equals(sender.getUniqueId())) {
            return ArgumentParseResult.failure(new FactionPlayerParseException(ParserCaptions.Keys.FactionPlayer.SELF, input, ctx));
        }
        

        FactionPlayer factionPlayer = PlayerUtil.getFactionPlayer(player.getUniqueId());
        Boolean inFaction = factionPlayer.getFaction() != null;
        if(_types.contains(ParserType.IN_FACTION) && !inFaction) {
            return ArgumentParseResult.failure(new FactionPlayerParseException(ParserCaptions.Keys.FactionPlayer.NOT_IN_FACTION, input, ctx));
        }

        if(_types.contains(ParserType.IN_SAME_FACTION) && (!inFaction || !factionPlayer.getFaction().equals(senderFactionPlayer.getFaction()))) {
            return ArgumentParseResult.failure(new FactionPlayerParseException(ParserCaptions.Keys.FactionPlayer.NOT_IN_SAME_FACTION, input, ctx));
        }


        if(_types.contains(ParserType.NOT_IN_FACTION) && inFaction) {
            return ArgumentParseResult.failure(new FactionPlayerParseException(ParserCaptions.Keys.FactionPlayer.IN_FACTION, input, ctx));
        }
        

        if(senderFactionPlayer.getFaction() != null) {
            Boolean hasInvite = Factions.getFactionsCommon().inviteManager.getInvite(senderFactionPlayer.getFaction(), player.getUniqueId()).isPresent();
            if(_types.contains(ParserType.HAS_INVITE) && !hasInvite) {
                return ArgumentParseResult.failure(new FactionPlayerParseException(ParserCaptions.Keys.FactionPlayer.HAS_NO_INVITE, input, ctx));
            }

            if(_types.contains(ParserType.HAS_NO_INVITE) && hasInvite) {
                return ArgumentParseResult.failure(new FactionPlayerParseException(ParserCaptions.Keys.FactionPlayer.HAS_INVITE, input, ctx));
            }
        }

        if(_types.contains(ParserType.NOT_LEADER) && inFaction) {
            Faction faction = Factions.getFactionsCommon().factionManager.getFaction(factionPlayer.getFaction()).get();
            if(faction.getLeader().equals(player.getUniqueId())) {
                return ArgumentParseResult.failure(new FactionPlayerParseException(ParserCaptions.Keys.FactionPlayer.BAD_PLAYER, input, ctx));
            }
        }


        return ArgumentParseResult.success(factionPlayer);
    }

    @Override
    public @NonNull Iterable<@NonNull Suggestion> suggestions(@NonNull CommandContext<C> ctx, @NonNull CommandInput input) {
        if(!(ctx.sender() instanceof Player)) {
            return List.of();
        }
        
        List<Suggestion> out = new ArrayList<>();

        Player sender = (Player)ctx.sender();
        FactionPlayer senderFactionPlayer = PlayerUtil.getFactionPlayer(sender.getUniqueId());
        
        List<OfflinePlayer> players = new ArrayList<>(
            Bukkit.getOnlinePlayers()
                .stream()
                .map(x -> Bukkit.getOfflinePlayer(x.getUniqueId()))
                .toList()
        );

        if(_types.contains(ParserType.INCLUDES_OFFLINE)) {
            for(OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                if(players.contains(offlinePlayer)) continue;
                players.add(offlinePlayer);
            }
        }

        for(OfflinePlayer player : players) {
            if(!_types.contains(ParserType.INCLUDES_SELF) && player.getUniqueId().equals(sender.getUniqueId())) continue;

            FactionPlayer factionPlayer = PlayerUtil.getFactionPlayer(player.getUniqueId());
            Boolean inFaction = factionPlayer.getFaction() != null;

            if(_types.contains(ParserType.IN_FACTION) && !inFaction) continue;
            if(_types.contains(ParserType.IN_SAME_FACTION) && (!inFaction || !factionPlayer.getFaction().equals(senderFactionPlayer.getFaction()))) continue;
            if(_types.contains(ParserType.NOT_IN_FACTION) && inFaction) continue;
            
            if(senderFactionPlayer.getFaction() != null) {
                Boolean hasInvite = Factions.getFactionsCommon().inviteManager.getInvite(senderFactionPlayer.getFaction(), player.getUniqueId()).isPresent();
                if(_types.contains(ParserType.HAS_INVITE) && !hasInvite) continue;
                if(_types.contains(ParserType.HAS_NO_INVITE) && hasInvite) continue;
            }

            if(_types.contains(ParserType.NOT_LEADER) && inFaction) {
                Faction faction = Factions.getFactionsCommon().factionManager.getFaction(factionPlayer.getFaction()).get();
                if(faction.getLeader().equals(player.getUniqueId())) continue;
            }

            out.add(Suggestion.simple(player.getName()));
        }

        return out;
    }

    public static<C> @NonNull ParserDescriptor<C, FactionPlayer> withOptions(ParserType... options) {
        return ParserDescriptor.of(new FactionPlayerParser<>(options), FactionPlayer.class);
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
