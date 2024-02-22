package com.coolguy1842.factions.Commands;

import java.sql.SQLException;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.permission.Permission;

import com.coolguy1842.factions.Factions;
import com.coolguy1842.factions.Parsers.DatabaseParser;
import com.coolguy1842.factions.Util.PlayerUtil;
import com.coolguy1842.factionscommon.Databases.DatabaseHandler;

public class ExecuteCommand {
    public static void register(CommandManager<CommandSender> commandManager) {
        commandManager.command(
            commandManager.commandBuilder("executedb")
                .permission(Permission.of("factions.execute"))
                .required("database", DatabaseParser.databaseParser())
                    .required("statement", StringParser.greedyStringParser())
                        .handler(ctx -> runCommand(ctx))
        );
    }

    public static void runCommand(CommandContext<CommandSender> ctx) {
        CommandSender sender = ctx.sender();

        DatabaseHandler databaseHandler = ctx.get("database");
        String statement = ctx.get("statement");

        try {
            databaseHandler.getDatabase().execute(statement);

            switch (databaseHandler.getName()) {
                case "factions": Factions.getFactionsCommon().factionManager.reload(); break;
                case "players":
                    Factions.getFactionsCommon().playerManager.reload();
                    
                    if(ctx.sender() instanceof Player) {
                        PlayerUtil.updatePlayerPermissions((Player)ctx.sender());
                    }

                    break;
                case "ranks": Factions.getFactionsCommon().rankManager.reload(); break;
                case "invites": Factions.getFactionsCommon().inviteManager.reload(); break;
                case "homes": Factions.getFactionsCommon().homeManager.reload(); break;
                case "vaults": Factions.getFactionsCommon().vaultManager.reload(); break;
                default: break;
            }
            
            sender.sendMessage("Executed statement successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            sender.sendMessage("Error querying db! Check console.");
            
            return;
        }
    }
}
