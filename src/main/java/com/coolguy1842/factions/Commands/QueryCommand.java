package com.coolguy1842.factions.Commands;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.sql.rowset.CachedRowSet;

import org.bukkit.command.CommandSender;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.permission.Permission;

import com.coolguy1842.factions.Parsers.DatabaseParser;
import com.coolguy1842.factions.Util.MessageUtil;
import com.coolguy1842.factionscommon.Databases.DatabaseHandler;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;

public class QueryCommand {
    public static void register(CommandManager<CommandSender> commandManager) {
        commandManager.command(
            commandManager.commandBuilder("query", "querydb")
                .permission(Permission.of("factions.query"))
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
            CachedRowSet rows = databaseHandler.getDatabase().query(statement);
            if(rows == null || rows.size() <= 0) {
                sender.sendMessage("Nothing returned!");
                return;
            }

            sender.sendMessage(
                MessageUtil.format(
                    "{} Query Database {}",
                    Component.text("    ").decorate(TextDecoration.STRIKETHROUGH),
                    Component.text("    ").decorate(TextDecoration.STRIKETHROUGH)
                )
            );

            Integer row = 1;
            while(rows.next()) {
                sender.sendMessage(String.format("  Row %d", row++));
                ResultSetMetaData meta = rows.getMetaData();

                for(int col = 1; col <= meta.getColumnCount(); col++) {
                    Object value = rows.getObject(col);

                    if(value == null) {
                        sender.sendMessage(MessageUtil.format("    {}: null", Component.text(meta.getColumnName(col))));
                        continue;
                    }

                    sender.sendMessage(MessageUtil.format("    {}: {}", Component.text(meta.getColumnName(col)), Component.text(value.toString())));
                }
            }
            
            sender.sendMessage(Component.text("                            ").decorate(TextDecoration.STRIKETHROUGH));
        } catch (SQLException e) {
            e.printStackTrace();
            sender.sendMessage("Error querying db! Check console.");
            
            return;
        }
    }
}
