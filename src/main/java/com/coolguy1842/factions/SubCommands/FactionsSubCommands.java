package com.coolguy1842.factions.SubCommands;

import java.util.List;

import com.coolguy1842.factions.Interfaces.Subcommand;
import com.coolguy1842.factions.SubCommands.Faction.All.FactionHelpCommand;
import com.coolguy1842.factions.SubCommands.Faction.All.FactionLeaderboardCommand;
import com.coolguy1842.factions.SubCommands.Faction.NoFaction.FactionAcceptCommand;
import com.coolguy1842.factions.SubCommands.Faction.NoFaction.FactionCreateCommand;
import com.coolguy1842.factions.SubCommands.Faction.NoFaction.FactionRejectCommand;
import com.coolguy1842.factions.SubCommands.Faction.InFaction.FactionBalanceCommand;
import com.coolguy1842.factions.SubCommands.Faction.InFaction.FactionHomeCommand;
import com.coolguy1842.factions.SubCommands.Faction.InFaction.FactionInfoCommand;
import com.coolguy1842.factions.SubCommands.Faction.InFaction.FactionLeaveCommand;
import com.coolguy1842.factions.SubCommands.Faction.InFaction.FactionMenuCommand;
import com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.FactionInviteCommand;
import com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.FactionKickCommand;
import com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Bank.FactionBankCommand;
import com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Claim.FactionClaimCommand;
import com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Claim.FactionUnclaimCommand;
import com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Home.FactionDelHomeCommand;
import com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Home.FactionSetHomeCommand;
import com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Leader.FactionDisbandCommand;
import com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Leader.FactionRenameCommand;
import com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Leader.FactionTransferCommand;
import com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Option.FactionGetCommand;
import com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Option.FactionSetCommand;
import com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Rank.FactionRankCommand;
import com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Vault.FactionVaultCommand;

public class FactionsSubCommands {
    public static final List<Subcommand> subcommands = List.of(
        new FactionHelpCommand       (),
        new FactionLeaderboardCommand(),
        new FactionCreateCommand     (),
        new FactionDisbandCommand    (),
        new FactionInviteCommand     (),
        new FactionAcceptCommand     (),
        new FactionRejectCommand     (),
        new FactionLeaveCommand      (),
        new FactionKickCommand       (),
        new FactionRankCommand       (),
        new FactionSetHomeCommand    (),
        new FactionDelHomeCommand    (),
        new FactionHomeCommand       (),
        new FactionVaultCommand      (),
        new FactionClaimCommand      (),
        new FactionUnclaimCommand    (),
        new FactionBalanceCommand    (),
        new FactionBankCommand       (),
        new FactionMenuCommand       (),
        new FactionSetCommand        (),
        new FactionGetCommand        (),
        new FactionRenameCommand     (),
        new FactionInfoCommand       (),
        new FactionTransferCommand   ()
    );
}
