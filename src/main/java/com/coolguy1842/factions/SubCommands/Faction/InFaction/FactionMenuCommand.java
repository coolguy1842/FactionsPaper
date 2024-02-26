package com.coolguy1842.factions.SubCommands.Faction.InFaction;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command.Builder;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.permission.Permission;
import org.incendo.cloud.processors.requirements.Requirements;
import org.incendo.interfaces.core.click.ClickHandler;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.element.ItemStackElement;
import org.incendo.interfaces.paper.transform.PaperTransform;
import org.incendo.interfaces.paper.type.ChestInterface;

import com.coolguy1842.factions.Interfaces.Subcommand;
import com.coolguy1842.factions.Requirements.Faction.DefaultFactionRequirement;
import com.coolguy1842.factions.Requirements.Faction.FactionRequirement;
import com.coolguy1842.factions.Transforms.Faction.FactionMenuTransform;
import com.coolguy1842.factions.Util.ItemUtil;
import com.coolguy1842.factions.Util.PlayerUtil.PlayerPermissions;

import net.kyori.adventure.text.Component;

public class FactionMenuCommand implements Subcommand {
    private static ChestInterface menuInterface =
        ChestInterface.builder()
            .title(Component.text("Faction Menu"))
            .rows(6)
            .cancelClicksInPlayerInventory(true)
            .updates(true, 20)
            .clickHandler(ClickHandler.cancel())
            .addTransform(PaperTransform.chestFill(ItemStackElement.of(ItemUtil.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, Component.empty()))))
            .addTransform(new FactionMenuTransform())
            .build();


    @Override public String getName() { return "menu"; }
    @Override public String getDescription() { return "Opens the faction menu!"; }
    @Override public Permission getPermission() { return PlayerPermissions.inFaction; }

    @Override
    public List<Builder<CommandSender>> getCommands(Builder<CommandSender> baseCommand) {
        return List.of(
            baseCommand.literal(getName())
                .meta(FactionRequirement.REQUIREMENT_KEY, Requirements.of(new DefaultFactionRequirement()))
                .permission(getPermission())
                .handler(ctx -> runCommand(ctx))
        );
    }

    @Override
    public void runCommand(CommandContext<CommandSender> ctx) {
        menuInterface.open(PlayerViewer.of((Player)ctx.sender()));
    }
}
