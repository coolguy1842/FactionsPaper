package com.coolguy1842.factions.Parsers;

import java.util.ArrayList;
import java.util.List;

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

public class BalanceParser<C> implements ArgumentParser<C, Long>, BlockingSuggestionProvider<C> {
    public static enum ParserType {
        FACTION,
        PLAYER
    }

    private ParserType _type;
    public ParserType getType() { return _type; }

    private BalanceParser(ParserType type) {
        _type = type;
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull Long> parse(@NonNull CommandContext<C> ctx, @NonNull CommandInput cmdInput) {
        if(!(ctx.sender() instanceof Player)) {
            return ArgumentParseResult.failure(new BalanceParseException(ParserCaptions.Keys.NOT_PLAYER, "", ctx));
        }

        Player player = (Player)ctx.sender();
        FactionPlayer factionPlayer = PlayerUtil.getFactionPlayer(player.getUniqueId());
        if(_type == ParserType.FACTION && factionPlayer.getFaction() == null) {
            return ArgumentParseResult.failure(new BalanceParseException(ParserCaptions.Keys.Balance.NO_FACTION, "", ctx));
        }

        Long amount;
        try {
            amount = Long.parseLong(cmdInput.readString());
        }
        catch(NumberFormatException e) {
            return ArgumentParseResult.failure(new BalanceParseException(ParserCaptions.Keys.Balance.INVALID, "", ctx));
        }

        if(amount <= 0) {
            return ArgumentParseResult.failure(new BalanceParseException(ParserCaptions.Keys.Balance.INVALID, "", ctx));
        }

        switch (_type) {
        case FACTION:
            Faction faction = Factions.getFactionsCommon().factionManager.getFaction(factionPlayer.getFaction()).get();
            
            if(faction.getBalance() < amount) {
                return ArgumentParseResult.failure(new BalanceParseException(ParserCaptions.Keys.Balance.INVALID, "", ctx));
            }

            break;
        default:
            if(factionPlayer.getBalance() < amount) {
                return ArgumentParseResult.failure(new BalanceParseException(ParserCaptions.Keys.Balance.INVALID, "", ctx));
            }

            break;
        }

        return ArgumentParseResult.success(amount);
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

        switch (_type) {
            case FACTION:
                Faction faction = Factions.getFactionsCommon().factionManager.getFaction(factionPlayer.getFaction()).get();
                for(Long i = 0L; i < faction.getBalance(); i++) {
                    out.add(Suggestion.simple(i.toString()));
                }

                break;
            case PLAYER:
                for(Long i = 0L; i < factionPlayer.getBalance(); i++) {
                    out.add(Suggestion.simple(i.toString()));
                }
                
                break;
            default: return List.of();
        }

        return out;
    }


    public static<C> @NonNull ParserDescriptor<C, Long> balanceParser(ParserType type) {
        return ParserDescriptor.of(new BalanceParser<>(type), Long.class);
    }

    
    public static final class BalanceParseException extends ParserException {
        private final Caption reason;
        private final String input;

        public BalanceParseException(final @NonNull Caption reason, final @NonNull String input, final @NonNull CommandContext<?> context) {
            super(
                BalanceParser.class,
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
