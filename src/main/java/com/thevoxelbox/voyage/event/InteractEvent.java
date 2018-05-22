package com.thevoxelbox.voyage.event;

import com.thevoxelbox.voyage.VoxelVoyage;
import com.thevoxelbox.voyage.services.VoyageServices;
import net.minecraft.server.v1_12_R1.Entity;
import net.minecraft.server.v1_12_R1.EnumHand;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractEvent {
    @Inject
    private VoyageServices voyageServices;

    public void onPlayerInteract(PlayerInteractEvent event) {
           if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                if (null != event.getItem() && event.getItem().getType().equals(Material.BLAZE_ROD)) { //TODO CHANGE
                    Player player = event.getPlayer();
                    Entity closest =  voyageServices.getEntity();
                }
           }
        }

        ///Old
        if ((event.hasItem()) && ((event.getAction().equals(Action.RIGHT_CLICK_AIR)) || (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)))) {
            if (event.getItem().getTypeId() == VoxelVoyage.voyageItem) {
                Player p = event.getPlayer();
                Entity closest = VoxelVoyage.getEntity(p);

                if (closest != null) {
                    if (VoxelVoyage.flying.contains(p.getName())) {
                        p.sendMessage(ChatColor.RED + "You may only travel on one entity at a time!");
                        return;
                    }
                    //TODO Check if this is correct, original value is 3 however has changed due to 1.12
                    closest.b(((CraftPlayer) p).getHandle(), EnumHand.MAIN_HAND);
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
