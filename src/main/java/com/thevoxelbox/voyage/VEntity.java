package com.thevoxelbox.voyage;

import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class VEntity implements Listener {
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (((CraftEntity) event.getEntity()).getHandle() instanceof PrzlabsEntity) {
            VoxelVoyage.log.info("[VoxelVoyage] Spawning VoyageEntity ID " + event.getEntity().getEntityId());
            event.setCancelled(false);
        } else if (((CraftEntity) event.getEntity()).getHandle() instanceof PrzlabsCrystal) {
            event.setCancelled(false);
        } else {
            if (!VoxelVoyage.SPAWN_ENTITIES) {
                event.setCancelled(true);
            }
        }
    }
}
