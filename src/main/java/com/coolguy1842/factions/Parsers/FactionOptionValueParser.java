package com.coolguy1842.factions.Parsers;

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
import com.coolguy1842.factions.Classes.ColourPresets;
import com.coolguy1842.factions.Util.PlayerUtil;
import com.coolguy1842.factionscommon.Classes.Faction;
import com.coolguy1842.factionscommon.Classes.FactionPlayer;
import com.coolguy1842.factionscommon.Classes.Rank;
import com.coolguy1842.factionscommon.Classes.Faction.Option;

import net.kyori.adventure.text.format.TextColor;

public class FactionOptionValueParser<C> implements ArgumentParser<C, Object>, BlockingSuggestionProvider<C> {
    private Option _option;

    public Option getOption() { return _option; }
    private FactionOptionValueParser(Option option) {
        _option = option;
    }


    @Override
    public @NonNull ArgumentParseResult<@NonNull Object> parse(@NonNull CommandContext<C> ctx, @NonNull CommandInput cmdInput) {
        if(!(ctx.sender() instanceof Player)) {
            return ArgumentParseResult.failure(new FactionOptionValueParseException(ParserCaptions.Keys.NOT_PLAYER, "", ctx));
        }

        Player sender = (Player)ctx.sender();
        FactionPlayer factionPlayer = PlayerUtil.getFactionPlayer(sender.getUniqueId());

        Optional<Faction> factionOptional = Factions.getFactionsCommon().factionManager.getFaction(factionPlayer.getFaction());
        if(!factionOptional.isPresent()) return ArgumentParseResult.failure(new FactionOptionValueParseException(ParserCaptions.Keys.NO_FACTION, "", ctx));

        Faction faction = factionOptional.get();
        String optionValue = cmdInput.readString();
        
        switch(getOption()) {
        case COLOUR: {
            TextColor value;
            if(ColourPresets.colours.containsKey(optionValue)) {
                value = ColourPresets.colours.get(optionValue);
            }
            else {
                value = TextColor.fromHexString("#" + optionValue);
            }

            if(value == null) {
                return ArgumentParseResult.failure(new FactionOptionValueParseException(ParserCaptions.Keys.Faction.Option.Value.INVALID_COLOUR, optionValue, ctx));
            }

            return ArgumentParseResult.success(value);
        }
        case DEFAULT_RANK:
            Optional<Rank> rankOptional = Factions.getFactionsCommon().rankManager.getRank(faction.getID(), optionValue);
            if(!rankOptional.isPresent()) {
                return ArgumentParseResult.failure(new FactionOptionValueParseException(ParserCaptions.Keys.Faction.Option.Value.INVALID_RANK, optionValue, ctx));
            }

            return ArgumentParseResult.success(rankOptional.get());
        default: return ArgumentParseResult.failure(new FactionOptionValueParseException(ParserCaptions.Keys.Faction.Option.Value.INVALID, optionValue, ctx));
        }
    }

    @Override
    public @NonNull Iterable<@NonNull Suggestion> suggestions(@NonNull CommandContext<C> ctx, @NonNull CommandInput input) {
        if(!(ctx.sender() instanceof Player)) {
            return List.of();
        }

        Player sender = (Player)ctx.sender();
        FactionPlayer factionPlayer = PlayerUtil.getFactionPlayer(sender.getUniqueId());

        Optional<Faction> factionOptional = Factions.getFactionsCommon().factionManager.getFaction(factionPlayer.getFaction());
        if(!factionOptional.isPresent()) return List.of();

        Faction faction = factionOptional.get();

        switch (getOption()) {
        case COLOUR:
             return 
                ColourPresets.colours.keySet()
                    .stream()
                    .map(x -> Suggestion.simple(x))
                    .toList();
        case DEFAULT_RANK:
            return 
                Factions.getFactionsCommon().rankManager.getRanksInFaction(faction.getID())
                    .stream()
                    .map(x -> Suggestion.simple(x.getName()))
                    .toList();
        default: return List.of();
        }
    }


    public static<C> @NonNull ParserDescriptor<C, Object> factionOptionValueParser(Option option) {
        return ParserDescriptor.of(new FactionOptionValueParser<>(option), Object.class);
    }

    
    public static final class FactionOptionValueParseException extends ParserException {
        private final Caption reason;
        private final String input;

        public FactionOptionValueParseException(final @NonNull Caption reason, final @NonNull String input, final @NonNull CommandContext<?> context) {
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
