package com.coolguy1842.factions;

import java.nio.file.Path;

import io.papermc.lib.PaperLib;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.caption.CaptionProvider;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.PaperCommandManager;

import com.coolguy1842.factions.Commands.BalanceCommand;
import com.coolguy1842.factions.Commands.ExecuteCommand;
import com.coolguy1842.factions.Commands.FactionCommand;
import com.coolguy1842.factions.Commands.QueryCommand;
import com.coolguy1842.factions.Commands.Sell.SellCommand;
import com.coolguy1842.factions.Events.Inventory.OnInventoryClose;
import com.coolguy1842.factions.Events.Inventory.OnInventoryInteract;
import com.coolguy1842.factions.Events.Player.OnPlayerChat;
import com.coolguy1842.factions.Events.Player.OnPlayerJoin;
import com.coolguy1842.factions.Events.Player.OnPlayerLeave;
import com.coolguy1842.factions.Events.Player.OnPlayerMove;
import com.coolguy1842.factions.Parsers.ParserCaptions;
import com.coolguy1842.factions.Requirements.Faction.FactionRequirement;
import com.coolguy1842.factions.Util.MessageUtil;
import com.coolguy1842.factions.Util.PlayerUtil;
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

        for(Player player : this.getServer().getOnlinePlayers()) {
            PlayerUtil.updatePlayerPermissions(player);
        }
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

        registerCaptions();

        commandManager.registerCommandPostProcessor(FactionRequirement.postprocessor);
        FactionCommand.register(commandManager);
        
        QueryCommand.register(commandManager);
        ExecuteCommand.register(commandManager);

        BalanceCommand.register(commandManager);
        SellCommand.register(commandManager);
    }

    private void registerCaptions() {        
        commandManager.captionRegistry()
            .registerProvider(CaptionProvider.forCaption(ParserCaptions.Keys.NOT_PLAYER, ParserCaptions.Providers.getProvider(ParserCaptions.Keys.NOT_PLAYER)))


            .registerProvider(CaptionProvider.forCaption(ParserCaptions.Keys.FactionPlayer.INVALID, ParserCaptions.Providers.FactionPlayer.getProvider(ParserCaptions.Keys.FactionPlayer.INVALID)))
            .registerProvider(CaptionProvider.forCaption(ParserCaptions.Keys.FactionPlayer.SELF, ParserCaptions.Providers.FactionPlayer.getProvider(ParserCaptions.Keys.FactionPlayer.SELF)))
        
            .registerProvider(CaptionProvider.forCaption(ParserCaptions.Keys.FactionPlayer.NOT_IN_FACTION, ParserCaptions.Providers.FactionPlayer.getProvider(ParserCaptions.Keys.FactionPlayer.NOT_IN_FACTION)))
            .registerProvider(CaptionProvider.forCaption(ParserCaptions.Keys.FactionPlayer.NOT_IN_SAME_FACTION, ParserCaptions.Providers.FactionPlayer.getProvider(ParserCaptions.Keys.FactionPlayer.NOT_IN_SAME_FACTION)))
            .registerProvider(CaptionProvider.forCaption(ParserCaptions.Keys.FactionPlayer.IN_FACTION, ParserCaptions.Providers.FactionPlayer.getProvider(ParserCaptions.Keys.FactionPlayer.IN_FACTION)))

            .registerProvider(CaptionProvider.forCaption(ParserCaptions.Keys.FactionPlayer.HAS_INVITE, ParserCaptions.Providers.FactionPlayer.getProvider(ParserCaptions.Keys.FactionPlayer.HAS_INVITE)))
            .registerProvider(CaptionProvider.forCaption(ParserCaptions.Keys.FactionPlayer.HAS_NO_INVITE, ParserCaptions.Providers.FactionPlayer.getProvider(ParserCaptions.Keys.FactionPlayer.HAS_NO_INVITE)))

            .registerProvider(CaptionProvider.forCaption(ParserCaptions.Keys.FactionPlayer.BAD_PLAYER, ParserCaptions.Providers.FactionPlayer.getProvider(ParserCaptions.Keys.FactionPlayer.BAD_PLAYER)))

        
            .registerProvider(CaptionProvider.forCaption(ParserCaptions.Keys.Faction.INVALID, ParserCaptions.Providers.Faction.getProvider(ParserCaptions.Keys.Faction.INVALID)))
            .registerProvider(CaptionProvider.forCaption(ParserCaptions.Keys.Faction.NO_INVITE, ParserCaptions.Providers.Faction.getProvider(ParserCaptions.Keys.Faction.NO_INVITE)))
            .registerProvider(CaptionProvider.forCaption(ParserCaptions.Keys.Faction.NOT_OWN, ParserCaptions.Providers.Faction.getProvider(ParserCaptions.Keys.Faction.NOT_OWN)))


            .registerProvider(CaptionProvider.forCaption(ParserCaptions.Keys.Rank.INVALID, ParserCaptions.Providers.Rank.getProvider(ParserCaptions.Keys.Rank.INVALID)))
            .registerProvider(CaptionProvider.forCaption(ParserCaptions.Keys.Rank.NO_FACTION, ParserCaptions.Providers.Rank.getProvider(ParserCaptions.Keys.Rank.NO_FACTION)))


            .registerProvider(CaptionProvider.forCaption(ParserCaptions.Keys.Rank.Permission.INVALID, ParserCaptions.Providers.Rank.Permission.getProvider(ParserCaptions.Keys.Rank.Permission.INVALID)))


            .registerProvider(CaptionProvider.forCaption(ParserCaptions.Keys.Home.INVALID, ParserCaptions.Providers.Home.getProvider(ParserCaptions.Keys.Home.INVALID)))
            .registerProvider(CaptionProvider.forCaption(ParserCaptions.Keys.Home.NO_FACTION, ParserCaptions.Providers.Home.getProvider(ParserCaptions.Keys.Home.NO_FACTION)))

            
            .registerProvider(CaptionProvider.forCaption(ParserCaptions.Keys.Vault.INVALID, ParserCaptions.Providers.Vault.getProvider(ParserCaptions.Keys.Vault.INVALID)))
            .registerProvider(CaptionProvider.forCaption(ParserCaptions.Keys.Vault.NO_FACTION, ParserCaptions.Providers.Vault.getProvider(ParserCaptions.Keys.Vault.NO_FACTION)))


            .registerProvider(CaptionProvider.forCaption(ParserCaptions.Keys.Balance.INVALID, ParserCaptions.Providers.Balance.getProvider(ParserCaptions.Keys.Balance.INVALID)))
            .registerProvider(CaptionProvider.forCaption(ParserCaptions.Keys.Balance.NO_FACTION, ParserCaptions.Providers.Balance.getProvider(ParserCaptions.Keys.Balance.NO_FACTION)))


            .registerProvider(CaptionProvider.forCaption(ParserCaptions.Keys.Database.INVALID, ParserCaptions.Providers.Database.getProvider(ParserCaptions.Keys.Database.INVALID)));
    }

    private void initEvents() {
        this.getServer().getPluginManager().registerEvents(new OnPlayerJoin(), this);
        this.getServer().getPluginManager().registerEvents(new OnPlayerLeave(), this);
        this.getServer().getPluginManager().registerEvents(new OnPlayerChat(), this);
        this.getServer().getPluginManager().registerEvents(new OnPlayerMove(), this);
        
        this.getServer().getPluginManager().registerEvents(new OnInventoryInteract(), this);
        this.getServer().getPluginManager().registerEvents(new OnInventoryClose(), this);
    }


    @Override
    public void onDisable() {
        getFactionsCommon().close();

        commandManager = null;
        plugin = null;
    }


    public static void initFactionsCommon(Path path) {
        common = new FactionsCommon(path);
    }

    public static FactionsCommon getFactionsCommon() { return common; }
    
    public static JavaPlugin getPlugin() { return plugin; }
    public static Component getPrefix() {
        return MessageUtil.combine(
            Component.text("[")       .color(TextColor.color(160, 160, 160)),
            Component.text("Factions").color(TextColor.color(26,  161, 201)),
            Component.text("]")       .color(TextColor.color(160, 160, 160))
        );
    }
}