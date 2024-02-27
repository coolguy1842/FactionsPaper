package com.coolguy1842.factions.Commands;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.processors.requirements.Requirements;
import org.incendo.interfaces.core.click.ClickHandler;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.element.ItemStackElement;
import org.incendo.interfaces.paper.transform.PaperTransform;
import org.incendo.interfaces.paper.type.ChestInterface;

import com.coolguy1842.factions.Requirements.Faction.DefaultFactionRequirement;
import com.coolguy1842.factions.Requirements.Faction.FactionRequirement;
import com.coolguy1842.factions.Transforms.Shop.SellModeTransform;
import com.coolguy1842.factions.Transforms.Shop.ShopMenuTransform;
import com.coolguy1842.factions.Util.ItemUtil;

import net.kyori.adventure.text.Component;

public class ShopCommand {
    private static ChestInterface shopInterface;

    public static void loadShopInterface() {
        shopInterface =
            ChestInterface.builder()
                .title(Component.text("Shop"))
                .rows(4)
                .updates(true, 20)
                .cancelClicksInPlayerInventory(true)
                .clickHandler(ClickHandler.cancel())
                .addTransform(PaperTransform.chestFill(ItemStackElement.of(ItemUtil.createItem(Material.GRAY_STAINED_GLASS_PANE, 1, Component.empty()))))
                .addTransform(new SellModeTransform())
                .addTransform(new ShopMenuTransform())
                .build();
    }


    public static void register(CommandManager<CommandSender> commandManager) {
        loadShopInterface();

        commandManager.command(
            commandManager.commandBuilder("shop")
                .meta(FactionRequirement.REQUIREMENT_KEY, Requirements.of(new DefaultFactionRequirement()))
                .handler(ctx -> runCommand(ctx))
        );
    }

    public static void runCommand(CommandContext<CommandSender> ctx) {
        shopInterface.open(PlayerViewer.of((Player)ctx.sender()));
    }
}
