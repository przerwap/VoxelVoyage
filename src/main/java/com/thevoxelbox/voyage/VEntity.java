package com.thevoxelbox.voyage;

import java.util.logging.Logger;

import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityListener;


public class VEntity extends EntityListener {
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if ((((CraftEntity) event.getEntity()).getHandle() instanceof PrzlabsEntity)) {
            VoxelVoyage.log.info("[VoxelVoyage] Spawning VoyageEntity ID " + event.getEntity().getEntityId());
            event.setCancelled(false);
        } else if ((((CraftEntity) event.getEntity()).getHandle() instanceof PrzlabsCrystal)) {
            event.setCancelled(false);
        }
        else if (!VoxelVoyage.SPAWN_ENTITIES) {
            event.setCancelled(true);
        }
    }
}
