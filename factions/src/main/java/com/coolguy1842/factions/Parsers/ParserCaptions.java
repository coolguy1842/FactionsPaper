package com.coolguy1842.factions.Parsers;

import java.util.function.Function;

import org.bukkit.command.CommandSender;
import org.incendo.cloud.caption.Caption;

public class ParserCaptions {
    public static final class Keys {
        public static final String NOT_PLAYER_KEY = "argument.parse.failure.not.player";
        public static final Caption NOT_PLAYER = Caption.of(NOT_PLAYER_KEY);

        public static final class FactionPlayer {
            public static final String INVALID_KEY = "argument.parse.failure.faction.player.invalid";
            public static final Caption INVALID = Caption.of(INVALID_KEY);

            public static final String SELF_KEY = "argument.parse.failure.faction.player.self";
            public static final Caption SELF = Caption.of(SELF_KEY);
            
            public static final String IN_FACTION_KEY = "argument.parse.failure.faction.player.in.faction";
            public static final Caption IN_FACTION = Caption.of(IN_FACTION_KEY);
            
            public static final String NOT_IN_FACTION_KEY = "argument.parse.failure.faction.player.not.in.faction";
            public static final Caption NOT_IN_FACTION = Caption.of(NOT_IN_FACTION_KEY);
            
            public static final String NOT_IN_SAME_FACTION_KEY = "argument.parse.failure.faction.player.not.in.same.faction";
            public static final Caption NOT_IN_SAME_FACTION = Caption.of(NOT_IN_FACTION_KEY);
            
            public static final String HAS_INVITE_KEY = "argument.parse.failure.faction.player.has.invite";
            public static final Caption HAS_INVITE = Caption.of(HAS_INVITE_KEY);
            
            public static final String HAS_NO_INVITE_KEY = "argument.parse.failure.faction.player.has.no.invite";
            public static final Caption HAS_NO_INVITE = Caption.of(HAS_NO_INVITE_KEY);
            
            public static final String BAD_PLAYER_KEY = "argument.parse.failure.faction.player.bad.player";
            public static final Caption BAD_PLAYER = Caption.of(BAD_PLAYER_KEY);
        }

        public static final class Faction {
            public static final String INVALID_KEY = "argument.parse.failure.faction.invalid";
            public static final Caption INVALID = Caption.of(INVALID_KEY);
            
            public static final String NOT_OWN_KEY = "argument.parse.failure.faction.not.own";
            public static final Caption NOT_OWN = Caption.of(NOT_OWN_KEY);
            
            public static final String NO_INVITE_KEY = "argument.parse.failure.faction.no.invite";
            public static final Caption NO_INVITE = Caption.of(NO_INVITE_KEY);
        }
    }

    public static final class Providers {
        public static final Function<CommandSender, String> getProvider(Caption caption) {
            switch(caption.key()) {
                default: return (CommandSender sender) -> { return "Only players can use this"; };
            }
        }

        public static final class FactionPlayer {
            public static final Function<CommandSender, String> getProvider(Caption caption) {
                switch(caption.key()) {
                    case ParserCaptions.Keys.FactionPlayer.SELF_KEY: return (CommandSender sender) -> { return "You cannot do that to yourself"; };
                    case ParserCaptions.Keys.FactionPlayer.IN_FACTION_KEY: return (CommandSender sender) -> { return "<input> is in a faction"; };
                    case ParserCaptions.Keys.FactionPlayer.NOT_IN_FACTION_KEY: return (CommandSender sender) -> { return "<input> is not in a faction"; };
                    case ParserCaptions.Keys.FactionPlayer.NOT_IN_SAME_FACTION_KEY: return (CommandSender sender) -> { return "<input> is not in your faction"; };
                    case ParserCaptions.Keys.FactionPlayer.HAS_INVITE_KEY: return (CommandSender sender) -> { return "<input> already has an invite"; };
                    case ParserCaptions.Keys.FactionPlayer.HAS_NO_INVITE_KEY: return (CommandSender sender) -> { return "You don't have an invite from <input>"; };
                    case ParserCaptions.Keys.FactionPlayer.BAD_PLAYER_KEY: return (CommandSender sender) -> { return "You cannot do that to <input>"; };
                    default: return (CommandSender sender) -> { return "No player found named <input>"; };
                }
            }
        }

        public static final class Faction {
            public static final Function<CommandSender, String> getProvider(Caption caption) {
                switch(caption.key()) {
                    case ParserCaptions.Keys.Faction.NOT_OWN_KEY: return (CommandSender sender) -> { return "You can't specify your own faction"; };
                    case ParserCaptions.Keys.Faction.NO_INVITE_KEY: return (CommandSender sender) -> { return "You don't have an invite from <input>"; };
                    default: return (CommandSender sender) -> { return "No faction named <input>"; };
                }
            }
        }
    }
}
