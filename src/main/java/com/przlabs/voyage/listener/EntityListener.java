package com.przlabs.voyage.listener;

import com.przlabs.voyage.application.VoxelVoyage;
import com.przlabs.voyage.entity.PrzlabsCrystal;
import com.przlabs.voyage.entity.PrzlabsEntity;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class EntityListener implements Listener {
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (((CraftEntity) event.getEntity()).getHandle() instanceof PrzlabsEntity) {
            VoxelVoyage.LOGGER.info("[VoxelVoyage] Spawning VoyageEntity ID " + event.getEntity().getEntityId());
            event.setCancelled(false);
        } else if (((CraftEntity) event.getEntity()).getHandle() instanceof PrzlabsCrystal) {
            event.setCancelled(false);
        }
    }
}
