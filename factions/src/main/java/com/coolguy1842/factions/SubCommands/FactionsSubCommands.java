package com.coolguy1842.factions.SubCommands;

import java.util.List;

import com.coolguy1842.factions.SubCommands.Faction.All.FactionsHelpCommand;
import com.coolguy1842.factions.SubCommands.Faction.NoFaction.FactionAcceptCommand;
import com.coolguy1842.factions.SubCommands.Faction.NoFaction.FactionCreateCommand;
import com.coolguy1842.factions.SubCommands.Faction.NoFaction.FactionRejectCommand;
import com.coolguy1842.factions.SubCommands.Faction.InFaction.FactionHomeCommand;
import com.coolguy1842.factions.SubCommands.Faction.InFaction.FactionLeaveCommand;
import com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.FactionDelHomeCommand;
import com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.FactionInviteCommand;
import com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.FactionKickCommand;
import com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.FactionSetHomeCommand;
import com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Leader.FactionDisbandCommand;
import com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Rank.FactionRankCommand;
import com.coolguy1842.factions.interfaces.Subcommand;

public class FactionsSubCommands {
    public enum SubcommandIndex {
        HELP,
        CREATE,
        DISBAND,
        INVITE,
        ACCEPT,
        REJECT,
        LEAVE,
        KICK,
        RANK,
        SET_HOME,
        DEL_HOME,
        HOME
    }

    public static final List<Subcommand> subcommands = List.of(
        new FactionsHelpCommand  (),
        new FactionCreateCommand (),
        new FactionDisbandCommand(),
        new FactionInviteCommand (),
        new FactionAcceptCommand (),
        new FactionRejectCommand (),
        new FactionLeaveCommand  (),
        new FactionKickCommand   (),
        new FactionRankCommand   (),
        new FactionSetHomeCommand(),
        new FactionDelHomeCommand(),
        new FactionHomeCommand   ()
    );
}
