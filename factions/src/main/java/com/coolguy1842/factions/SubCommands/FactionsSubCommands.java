package com.coolguy1842.factions.SubCommands;

import java.util.List;

import com.coolguy1842.factions.SubCommands.Faction.All.FactionsHelpCommand;
import com.coolguy1842.factions.SubCommands.Faction.NoFaction.FactionCreateCommand;
import com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Leader.FactionDisbandCommand;
import com.coolguy1842.factions.interfaces.Subcommand;

public class FactionsSubCommands {
    public enum SubcommandIndex {
        HELP,
        CREATE,
        DISBAND
    }

    public static final List<Subcommand> subcommands = List.of(
        new FactionsHelpCommand  (),
        new FactionCreateCommand (),
        new FactionDisbandCommand()
    );
}
