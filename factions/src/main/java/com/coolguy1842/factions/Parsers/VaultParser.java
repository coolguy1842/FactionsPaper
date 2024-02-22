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
import com.coolguy1842.factionscommon.Classes.Vault;

public class VaultParser<C> implements ArgumentParser<C, Vault>, BlockingSuggestionProvider<C> {
    @Override
    public @NonNull ArgumentParseResult<@NonNull Vault> parse(@NonNull CommandContext<C> ctx, @NonNull CommandInput cmdInput) {
        if(!(ctx.sender() instanceof Player)) {
            return ArgumentParseResult.failure(new VaultParseException(ParserCaptions.Keys.NOT_PLAYER, "", ctx));
        }

        Player sender = (Player)ctx.sender();
        FactionPlayer factionPlayer = PlayerUtil.getFactionPlayer(sender.getUniqueId());
        
        String vaultName = cmdInput.readString();

        Optional<Faction> factionOptional = Factions.getFactionsCommon().factionManager.getFaction(factionPlayer.getFaction());
        if(factionPlayer.getFaction() == null || !factionOptional.isPresent()) {
            return ArgumentParseResult.failure(new VaultParseException(ParserCaptions.Keys.Vault.NO_FACTION, "", ctx));    
        }

        Faction faction = factionOptional.get();
        Optional<Vault> vault = Factions.getFactionsCommon().vaultManager.getVault(faction.getID(), vaultName);
        if(!vault.isPresent()) {
            return ArgumentParseResult.failure(new VaultParseException(ParserCaptions.Keys.Vault.INVALID, vaultName, ctx));
        }

        return ArgumentParseResult.success(vault.get());
    }

    @Override
    public @NonNull Iterable<@NonNull Suggestion> suggestions(@NonNull CommandContext<C> ctx, @NonNull CommandInput input) {
        if(!(ctx.sender() instanceof Player)) {
            return List.of();
        }

        List<Suggestion> out = new ArrayList<>();
        
        Player player = (Player)ctx.sender();
        FactionPlayer factionPlayer = PlayerUtil.getFactionPlayer(player.getUniqueId());
        if(factionPlayer.getFaction() == null) return out;

        for(Vault vault : Factions.getFactionsCommon().vaultManager.getVaultsInFaction(factionPlayer.getFaction())) {
            out.add(Suggestion.simple(vault.getName()));
        }

        return out;
    }


    public static<C> @NonNull ParserDescriptor<C, Vault> vaultParser() {
        return ParserDescriptor.of(new VaultParser<>(), Vault.class);
    }

    
    public static final class VaultParseException extends ParserException {
        private final Caption reason;
        private final String input;

        public VaultParseException(final @NonNull Caption reason, final @NonNull String input, final @NonNull CommandContext<?> context) {
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
