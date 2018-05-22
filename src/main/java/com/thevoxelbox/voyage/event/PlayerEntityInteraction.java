package com.thevoxelbox.voyage.event;

import com.thevoxelbox.voyage.VoxelVoyage;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.event.player.PlayerInteractEntityEvent;

//TODO UPDATE THIS
public class PlayerEntityInteraction {
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
}
