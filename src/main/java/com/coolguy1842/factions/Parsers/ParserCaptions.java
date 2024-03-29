package com.coolguy1842.factions.Parsers;

import java.util.function.Function;

import org.bukkit.command.CommandSender;
import org.incendo.cloud.caption.Caption;

public class ParserCaptions {
    public static final class Keys {
        public static final String NOT_PLAYER_KEY = "argument.parse.failure.not.player";
        public static final Caption NOT_PLAYER = Caption.of(NOT_PLAYER_KEY);
        
        public static final String NO_FACTION_KEY = "argument.parse.failure.no.faction";
        public static final Caption NO_FACTION = Caption.of(NO_FACTION_KEY);

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
            public static final class Option {    
                public static final class Value {
                    public static final String INVALID_KEY = "argument.parse.failure.faction.option.value.invalid";
                    public static final Caption INVALID = Caption.of(INVALID_KEY);
                    
                    public static final String INVALID_COLOUR_KEY = "argument.parse.failure.faction.option.value.invalid.colour";
                    public static final Caption INVALID_COLOUR = Caption.of(INVALID_COLOUR_KEY);
                    
                    public static final String INVALID_RANK_KEY = "argument.parse.failure.faction.option.value.invalid.rank";
                    public static final Caption INVALID_RANK = Caption.of(INVALID_RANK_KEY);
                }
                
                public static final String INVALID_KEY = "argument.parse.failure.faction.option.invalid";
                public static final Caption INVALID = Caption.of(INVALID_KEY);
            }

            public static final String INVALID_KEY = "argument.parse.failure.faction.invalid";
            public static final Caption INVALID = Caption.of(INVALID_KEY);
            
            public static final String NOT_OWN_KEY = "argument.parse.failure.faction.not.own";
            public static final Caption NOT_OWN = Caption.of(NOT_OWN_KEY);
            
            public static final String NO_INVITE_KEY = "argument.parse.failure.faction.no.invite";
            public static final Caption NO_INVITE = Caption.of(NO_INVITE_KEY);
        }
        
        public static final class Rank {
            public static final class Permission {
                public static final String INVALID_KEY = "argument.parse.failure.rank.permission.invalid";
                public static final Caption INVALID = Caption.of(INVALID_KEY);
            }

            public static final String INVALID_KEY = "argument.parse.failure.rank.invalid";
            public static final Caption INVALID = Caption.of(INVALID_KEY);
            
            public static final String NO_FACTION_KEY = "argument.parse.failure.rank.no.faction";
            public static final Caption NO_FACTION = Caption.of(NO_FACTION_KEY);
        }
        
        public static final class Home {
            public static final String INVALID_KEY = "argument.parse.failure.home.invalid";
            public static final Caption INVALID = Caption.of(INVALID_KEY);
            
            public static final String NO_FACTION_KEY = "argument.parse.failure.home.no.faction";
            public static final Caption NO_FACTION = Caption.of(NO_FACTION_KEY);
        }
        
        public static final class Vault {
            public static final String INVALID_KEY = "argument.parse.failure.vault.invalid";
            public static final Caption INVALID = Caption.of(INVALID_KEY);
            
            public static final String NO_FACTION_KEY = "argument.parse.failure.vault.no.faction";
            public static final Caption NO_FACTION = Caption.of(NO_FACTION_KEY);
        }
        
        public static final class Balance {
            public static final String INVALID_KEY = "argument.parse.failure.balance.invalid";
            public static final Caption INVALID = Caption.of(INVALID_KEY);
            
            public static final String NO_FACTION_KEY = "argument.parse.failure.balance.no.faction";
            public static final Caption NO_FACTION = Caption.of(NO_FACTION_KEY);
        }
        
        public static final class Item {
            public static final class Amount {
                public static final String INVALID_KEY = "argument.parse.failure.item.amount.invalid";
                public static final Caption INVALID = Caption.of(INVALID_KEY);
            }
        }


        public static final class TPA {
            public static final class Player {
                public static final String INVALID_KEY = "argument.parse.failure.tpa.player.invalid";
                public static final Caption INVALID = Caption.of(INVALID_KEY);
                
                public static final String SELF_KEY = "argument.parse.failure.tpa.player.self";
                public static final Caption SELF = Caption.of(SELF_KEY);
                
                public static final String HAS_REQUEST_KEY = "argument.parse.failure.tpa.player.has.request";
                public static final Caption HAS_REQUEST = Caption.of(HAS_REQUEST_KEY);

                public static final String NO_REQUEST_KEY = "argument.parse.failure.tpa.player.no.request";
                public static final Caption NO_REQUEST = Caption.of(NO_REQUEST_KEY);
            }
        }
    
        public static final class Database {
            public static final String INVALID_KEY = "argument.parse.failure.database.invalid";
            public static final Caption INVALID = Caption.of(INVALID_KEY);
        }
    }

    public static final class Providers {
        public static final Function<CommandSender, String> getProvider(Caption caption) {
            switch(caption.key()) {
                case ParserCaptions.Keys.NO_FACTION_KEY: return (CommandSender sender) -> { return "You must be in a faction"; };
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
            public static final class Option {
                public static final class Value {
                    public static final Function<CommandSender, String> getProvider(Caption caption) {
                        switch(caption.key()) {
                            case ParserCaptions.Keys.Faction.Option.Value.INVALID_COLOUR_KEY: return (CommandSender sender) -> { return "Invalid colour: <input>. e.g \"FFAAFF\""; };
                            case ParserCaptions.Keys.Faction.Option.Value.INVALID_RANK_KEY: return (CommandSender sender) -> { return "Invalid rank: <input>"; };
                            default: return (CommandSender sender) -> { return "Invalid option value: <input>"; };
                        }
                    }
                }

                public static final Function<CommandSender, String> getProvider(Caption caption) {
                    switch(caption.key()) {
                        default: return (CommandSender sender) -> { return "No faction option named <input>"; };
                    }
                }
            }

            public static final Function<CommandSender, String> getProvider(Caption caption) {
                switch(caption.key()) {
                    case ParserCaptions.Keys.Faction.NOT_OWN_KEY: return (CommandSender sender) -> { return "You can't specify your own faction"; };
                    case ParserCaptions.Keys.Faction.NO_INVITE_KEY: return (CommandSender sender) -> { return "You don't have an invite from <input>"; };
                    default: return (CommandSender sender) -> { return "No faction named <input>"; };
                }
            }
        }
    
        public static final class Rank {    
            public static final class Permission {
                public static final Function<CommandSender, String> getProvider(Caption caption) {
                    switch(caption.key()) {
                        default: return (CommandSender sender) -> { return "No rank permission named <input>"; };
                    }
                }
            }

            public static final Function<CommandSender, String> getProvider(Caption caption) {
                switch(caption.key()) {
                    case ParserCaptions.Keys.Rank.NO_FACTION_KEY: return (CommandSender sender) -> { return "You must be in a faction"; };
                    default: return (CommandSender sender) -> { return "No rank named <input>"; };
                }
            }
        }

        public static final class Home {
            public static final Function<CommandSender, String> getProvider(Caption caption) {
                switch(caption.key()) {
                    case ParserCaptions.Keys.Home.NO_FACTION_KEY: return (CommandSender sender) -> { return "You must be in a faction"; };
                    default: return (CommandSender sender) -> { return "No home named <input>"; };
                }
            }
        }

        public static final class Vault {
            public static final Function<CommandSender, String> getProvider(Caption caption) {
                switch(caption.key()) {
                    case ParserCaptions.Keys.Home.NO_FACTION_KEY: return (CommandSender sender) -> { return "You must be in a faction"; };
                    default: return (CommandSender sender) -> { return "No vault named <input>"; };
                }
            }
        }

        public static final class Balance {
            public static final Function<CommandSender, String> getProvider(Caption caption) {
                switch(caption.key()) {
                    case ParserCaptions.Keys.Balance.NO_FACTION_KEY: return (CommandSender sender) -> { return "You must be in a faction"; };
                    default: return (CommandSender sender) -> { return "Invalid amount"; };
                }
            }
        }
        
        public static final class Item {
            public static final class Amount {
                public static final Function<CommandSender, String> getProvider(Caption caption) {
                    switch(caption.key()) {
                        default: return (CommandSender sender) -> { return "Invalid amount"; };
                    }
                }
            }
        }

        
        
        public static final class TPA {
            public static final class Player {
                public static final Function<CommandSender, String> getProvider(Caption caption) {
                    switch(caption.key()) {
                        case Keys.TPA.Player.SELF_KEY: return (CommandSender sender) -> { return "You cannot tpa to yourself"; };
                        case Keys.TPA.Player.HAS_REQUEST_KEY: return (CommandSender sender) -> { return "<input> already has a request"; };
                        case Keys.TPA.Player.NO_REQUEST_KEY: return (CommandSender sender) -> { return "You have no request from <input>"; };
                        default: return (CommandSender sender) -> { return "No player named <input>"; };
                    }
                }
            }
        }

        public static final class Database {
            public static final Function<CommandSender, String> getProvider(Caption caption) {
                switch(caption.key()) {
                    default: return (CommandSender sender) -> { return "No database named <input>"; };
                }
            }
        }
    }
}
