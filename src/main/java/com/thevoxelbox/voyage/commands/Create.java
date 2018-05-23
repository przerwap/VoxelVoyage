package com.thevoxelbox.voyage.commands;

import com.google.inject.Inject;
import com.thevoxelbox.voyage.PrzlabsDragon;
import com.thevoxelbox.voyage.services.VoyageService;
import com.thevoxelbox.voyage.utils.Response;
import com.thevoxelbox.voyage.utils.registrar.Cmd;
import com.thevoxelbox.voyage.utils.registrar.Description;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@Cmd(name = "vv", enabled = true, usage = "Creates a voyage", aliases = {}, permission = "voxelvoyage.create", label = "")

public class Create extends CommandHandler {
    @Inject
    VoyageService voyageService;

    Options options = new Options()
            .addOption("pw", "password", true, "Sets password to use path.")
            .addOption("d", "disabled", false, "disables path.");

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (args.length >= 1) {
            CommandLine cmdLine = null;
            try {
                cmdLine = parser.parse(options, args);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Player player = (Player) commandSender;
            Response response = voyageService.createEntry(args[0], player.getUniqueId(), cmdLine.hasOption("pw"), cmdLine.getOptionValue("pw"), cmdLine.hasOption("d"));

            switch (response) {
                case SUCCESS:
                    ItemStack item = new ItemStack(Material.SPECTRAL_ARROW);

                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName("VoxelVoyage Pointer");
                    item.setItemMeta(meta);
                    player.getInventory().setItemInMainHand(item);
                    PrzlabsDragon dragon = new PrzlabsDragon(((CraftWorld) player.getWorld()).getHandle(), true, player.getLocation(), 3);
                case INVALID_FORMAT:
                    player.sendMessage("§4The name you have submitted is invalid.");
                    break;
                case NAME_IN_USE:
                    player.sendMessage("§4" + args[0] + " is already in use.");

            }
        }
        return true;
    }
}
