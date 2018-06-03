package com.przlabs.voyage.listener;

import com.przlabs.voyage.domain.DragonAction;
import com.przlabs.voyage.application.VoxelVoyage;
import com.przlabs.voyage.entity.PrzlabsEntity;
import net.minecraft.server.v1_12_R1.Entity;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Entity entity = ((CraftEntity) event.getRightClicked()).getHandle();
        switch (entity.getAirTicks()) {
            case 12345:
                rightClickEntity(event.getPlayer(), DragonAction.FOCUS_PLAYER, entity);
                break;

            case 12346:
                if (!VoxelVoyage.isPermitted(event.getPlayer())) {
                    event.getPlayer().sendMessage(ChatColor.GOLD + "You are not permitted to do this. Please input the password or login with an OP account.");
                    break;
                }
                rightClickEntity(event.getPlayer(), DragonAction.FOCUS_PLAYER, entity);
                break;

            default:
                break;
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.hasItem()
                && (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
            if (event.getItem().getTypeId() == VoxelVoyage.voyageItem) {
                Player p = event.getPlayer();
                Entity closest = VoxelVoyage.getNearestEntity(p);

                if (closest != null) {
                    if (VoxelVoyage.flying.contains(p.getName())) {
                        p.sendMessage(ChatColor.RED + "You may only travel on one entity at a time!");
                        return;
                    }
                    rightClickEntity(p, DragonAction.BEGIN_VOYAGE, closest);
                } else {
                    p.sendMessage(ChatColor.RED + "Your offering is rejected.");
                }
            } else if (event.getItem().getType().equals(Material.INK_SACK) && event.getItem().getDurability() == 15) {
                if (event.getPlayer().isSneaking()) {
                    double distance = 0.7;
                    Location player_loc = event.getPlayer().getLocation();
                    double rot_x = (player_loc.getYaw() + 90) % 360;
                    double rot_y = player_loc.getPitch() * -1;
                    double rot_ycos = Math.cos(Math.toRadians(rot_y));
                    double rot_ysin = Math.sin(Math.toRadians(rot_y));
                    double rot_xcos = Math.cos(Math.toRadians(rot_x));
                    double rot_xsin = Math.sin(Math.toRadians(rot_x));

                    double h_length = (distance * rot_ycos);
                    double y_offset = (distance * rot_ysin);
                    double x_offset = (h_length * rot_xcos);
                    double z_offset = (h_length * rot_xsin);

                    double target_x = x_offset + player_loc.getX();
                    double target_y = y_offset + player_loc.getY() + 1.65;
                    double target_z = z_offset + player_loc.getZ();

                    event.getPlayer().getWorld().playEffect(new Location(player_loc.getWorld(), target_x, target_y, target_z), Effect.SMOKE, getSmokeDir(player_loc.getYaw()));
                } else {
                    double distance = 0.7;
                    Location player_loc = event.getPlayer().getLocation();
                    double rot_x = (player_loc.getYaw() + 90) % 360;
                    double rot_y = player_loc.getPitch() * -1;
                    double rot_ycos = Math.cos(Math.toRadians(rot_y));
                    double rot_ysin = Math.sin(Math.toRadians(rot_y));
                    double rot_xcos = Math.cos(Math.toRadians(rot_x));
                    double rot_xsin = Math.sin(Math.toRadians(rot_x));

                    double h_length = (distance * rot_ycos);
                    double y_offset = (distance * rot_ysin);
                    double x_offset = (h_length * rot_xcos);
                    double z_offset = (h_length * rot_xsin);

                    double target_x = x_offset + player_loc.getX();
                    double target_y = y_offset + player_loc.getY() + 1.65;
                    double target_z = z_offset + player_loc.getZ();

                    event.getPlayer().getWorld().playEffect(new Location(player_loc.getWorld(), target_x, target_y, target_z), Effect.SMOKE, 4);
                }
            }
        }
    }

    private void rightClickEntity(Player player, int action, Entity entity) {
        if (entity != null && entity instanceof PrzlabsEntity) {
            ((PrzlabsEntity) entity).rightClick(player, action);
        }
    }

    private int getSmokeDir(float yaw) { // 0 - west   90 - north   180 - east   270 - south
        if (yaw < 0) {
            yaw += 360;
        }
        if (yaw >= 0) {
            if (yaw <= 22.5) {
                return 7;
            }
            if (yaw <= 77.5) {
                return 6;
            }
            if (yaw <= 112.5) {
                return 3;
            }
            if (yaw <= 157.5) {
                return 0;
            }
            if (yaw <= 202.5) {
                return 1;
            }
            if (yaw <= 247.5) {
                return 2;
            }
            if (yaw <= 292.5) {
                return 5;
            }
            if (yaw <= 337.5) {
                return 8;
            } else {
                return 7;
            }
        }
        return 4;
    }
}
