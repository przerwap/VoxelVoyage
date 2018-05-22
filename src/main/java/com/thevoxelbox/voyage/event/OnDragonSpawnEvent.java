package com.thevoxelbox.voyage.event;

import com.thevoxelbox.voyage.PrzlabsEntity;
import com.thevoxelbox.voyage.VoxelVoyage;
import com.thevoxelbox.voyage.entities.Crystal;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class OnDragonSpawnEvent  {

    @EventHandler
    public void onDragonSpawnEvent(CreatureSpawnEvent creatureSpawnEvent) {

    }

    /*
     public void onCreatureSpawn(CreatureSpawnEvent event) {
        if ((((CraftEntity) event.getEntity()).getHandle() instanceof PrzlabsEntity)) {
            VoxelVoyage.log.info("[VoxelVoyage] Spawning VoyageEntity ID " + event.getEntity().getEntityId());
            event.setCancelled(false);
        } else if ((((CraftEntity) event.getEntity()).getHandle() instanceof Crystal)) {
            event.setCancelled(false);
        }
        else if (!VoxelVoyage.SPAWN_ENTITIES) {
            event.setCancelled(true);
        }
    }
     */
}
