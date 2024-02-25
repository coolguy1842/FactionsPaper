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

import com.coolguy1842.factions.Util.PlayerUtil;

public class ItemAmountParser<C> implements ArgumentParser<C, Integer>, BlockingSuggestionProvider<C> {
    @Override
    public @NonNull ArgumentParseResult<@NonNull Integer> parse(@NonNull CommandContext<C> ctx, @NonNull CommandInput cmdInput) {
        if(!(ctx.sender() instanceof Player)) {
            return ArgumentParseResult.failure(new ItemAmountParseException(ParserCaptions.Keys.NOT_PLAYER, "", ctx));
        }

        Integer amount;
        try {
            amount = Integer.parseInt(cmdInput.readString());
        }
        catch(NumberFormatException e) {
            return ArgumentParseResult.failure(new ItemAmountParseException(ParserCaptions.Keys.Item.Amount.INVALID, "", ctx));
        }

        Player sender = (Player)ctx.sender();
        if(amount <= 0 || amount > PlayerUtil.getAmountItem(sender, sender.getInventory().getItemInMainHand().getType())) {
            return ArgumentParseResult.failure(new ItemAmountParseException(ParserCaptions.Keys.Item.Amount.INVALID, "", ctx));
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
        for(Integer i = 1; i <= PlayerUtil.getAmountItem(player, player.getInventory().getItemInMainHand().getType()); i++) {
            out.add(Suggestion.simple(i.toString()));
        }

        return out;
    }


    public static<C> @NonNull ParserDescriptor<C, Integer> itemAmountParser() {
        return ParserDescriptor.of(new ItemAmountParser<>(), Integer.class);
    }

    
    public static final class ItemAmountParseException extends ParserException {
        private final Caption reason;
        private final String input;

        public ItemAmountParseException(final @NonNull Caption reason, final @NonNull String input, final @NonNull CommandContext<?> context) {
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
