package com.coolguy1842.factions;

import java.nio.file.Path;

import io.papermc.lib.PaperLib;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.PaperCommandManager;

import com.coolguy1842.factions.Commands.FactionCommand;
import com.coolguy1842.factions.Events.Player.OnPlayerJoin;
import com.coolguy1842.factions.Events.Player.OnPlayerLeave;
import com.coolguy1842.factions.Requirements.Faction.FactionRequirement;
import com.coolguy1842.factions.Util.MessageUtil;
import com.coolguy1842.factionscommon.FactionsCommon;

public class Factions extends JavaPlugin {
	private static FactionsCommon common;
	private static JavaPlugin plugin;

    private CommandManager<CommandSender> commandManager;

    @Override
    public void onEnable() {
        plugin = this;
        PaperLib.suggestPaper(this);

        initFactionsCommon(this.getDataFolder().toPath());
        initCommands();
        initEvents();
    }

    private void initCommands() {
        commandManager = new PaperCommandManager<>(
            this,
            ExecutionCoordinator.simpleCoordinator(),
            SenderMapper.identity()
        );

        if(commandManager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
            ((PaperCommandManager<CommandSender>)commandManager).registerBrigadier();
        }
        else if (commandManager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            ((PaperCommandManager<CommandSender>)commandManager).registerAsynchronousCompletions();
        }

        commandManager.registerCommandPostProcessor(FactionRequirement.postprocessor);
        FactionCommand.register(commandManager);
    }

    private void initEvents() {
        this.getServer().getPluginManager().registerEvents(new OnPlayerJoin(), this);
        this.getServer().getPluginManager().registerEvents(new OnPlayerLeave(), this);
    }


    @Override
    public void onDisable() {
        getFactionsCommon().close();
        plugin = null;
    }


    public static void initFactionsCommon(Path path) {
        common = new FactionsCommon(path);
    }

    public static FactionsCommon getFactionsCommon() { return common; }
    
    public static JavaPlugin getPlugin() { return plugin; }
    public static Component getPrefix() {
        return MessageUtil.combine(
            Component.text("[")       .color(TextColor.color(120, 120, 120)),
            Component.text("Factions").color(TextColor.color(26,  161, 201)),
            Component.text("]")       .color(TextColor.color(120, 120, 120))
        );
    }
}