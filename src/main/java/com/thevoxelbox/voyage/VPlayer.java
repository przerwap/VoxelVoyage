package com.thevoxelbox.voyage;

import java.util.TreeSet;

import net.minecraft.server.Entity;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.ItemStack;


public class VPlayer
        extends PlayerListener {
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        switch (((CraftEntity) event.getRightClicked()).getHandle().getAirTicks()) {
            case 12345:
                ((CraftEntity) event.getRightClicked()).getHandle().b(((CraftEntity) event.getPlayer()).getHandle(), 999999);
                break;

            case 12346:
                if (!VoxelVoyage.isPermitted(event.getPlayer())) {
                    event.getPlayer().sendMessage(ChatColor.GOLD + "You are not permitted to do this. Please input the password or login with an OP account.");
                } else {
                    /* 38 */
                    ((CraftEntity) event.getRightClicked()).getHandle().b(((CraftEntity) event.getPlayer()).getHandle(), 999999);
                }


                break;
        }

    }


    public void onPlayerInteract(PlayerInteractEvent event) {
        if ((event.hasItem()) && ((event.getAction().equals(Action.RIGHT_CLICK_AIR)) || (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)))) {
            if (event.getItem().getTypeId() == VoxelVoyage.voyageItem) {
                Player p = event.getPlayer();
                Entity closest = VoxelVoyage.getEntity(p);

                if (closest != null) {
                    if (VoxelVoyage.flying.contains(p.getName())) {
                        p.sendMessage(ChatColor.RED + "You may only travel on one entity at a time!");
                        return;
                    }
                    closest.b(((CraftPlayer) p).getHandle(), 3);
                } else {
                    p.sendMessage(ChatColor.RED + "Your offering is rejected.");
                }
            } else if ((event.getItem().getType().equals(Material.INK_SACK)) && (event.getItem().getDurability() == 15)) {
                double distance = 0.7D;
                Location player_loc = event.getPlayer().getLocation();
                double rot_x = (player_loc.getYaw() + 90.0F) % 360.0F;
                double rot_y = player_loc.getPitch() * -1.0F;
                double rot_ycos = Math.cos(Math.toRadians(rot_y));
                double rot_ysin = Math.sin(Math.toRadians(rot_y));
                double rot_xcos = Math.cos(Math.toRadians(rot_x));
                double rot_xsin = Math.sin(Math.toRadians(rot_x));
                double h_length = distance * rot_ycos;
                double y_offset = distance * rot_ysin;
                double x_offset = h_length * rot_xcos;
                double z_offset = h_length * rot_xsin;
                double target_x = x_offset + player_loc.getX();
                double target_y = y_offset + player_loc.getY() + 1.65D;
                double target_z = z_offset + player_loc.getZ();
                event.getPlayer().getWorld().playEffect(new Location(player_loc.getWorld(), target_x, target_y, target_z), Effect.SMOKE, 4);
            }
        }
    }
}
