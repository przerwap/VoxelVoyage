/*    */ package com.thevoxelbox.voyage;
/*    */ 
/*    */ import java.util.logging.Logger;
/*    */ import org.bukkit.craftbukkit.entity.CraftEntity;
/*    */ import org.bukkit.entity.Entity;
/*    */ import org.bukkit.event.entity.CreatureSpawnEvent;
/*    */ import org.bukkit.event.entity.EntityListener;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class VEntity
/*    */   extends EntityListener
/*    */ {
/*    */   public void onCreatureSpawn(CreatureSpawnEvent event)
/*    */   {
/* 19 */     if ((((CraftEntity)event.getEntity()).getHandle() instanceof PrzlabsEntity)) {
/* 20 */       VoxelVoyage.log.info("[VoxelVoyage] Spawning VoyageEntity ID " + event.getEntity().getEntityId());
/* 21 */       event.setCancelled(false);
/* 22 */     } else if ((((CraftEntity)event.getEntity()).getHandle() instanceof PrzlabsCrystal)) {
/* 23 */       event.setCancelled(false);
/*    */     }
/* 25 */     else if (!VoxelVoyage.SPAWN_ENTITIES) {
/* 26 */       event.setCancelled(true);
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\intellij\VoxelVoyage\VoxelVoyage.jar!\com\thevoxelbox\voyage\VEntity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */