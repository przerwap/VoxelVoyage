/*    */ package com.thevoxelbox.voyage;
/*    */ 
/*    */ import java.util.TreeSet;
/*    */ import net.minecraft.server.Entity;
/*    */ import org.bukkit.ChatColor;
/*    */ import org.bukkit.Effect;
/*    */ import org.bukkit.Location;
/*    */ import org.bukkit.Material;
/*    */ import org.bukkit.World;
/*    */ import org.bukkit.craftbukkit.entity.CraftEntity;
/*    */ import org.bukkit.craftbukkit.entity.CraftPlayer;
/*    */ import org.bukkit.entity.Player;
/*    */ import org.bukkit.event.block.Action;
/*    */ import org.bukkit.event.player.PlayerInteractEntityEvent;
/*    */ import org.bukkit.event.player.PlayerInteractEvent;
/*    */ import org.bukkit.event.player.PlayerListener;
/*    */ import org.bukkit.inventory.ItemStack;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class VPlayer
/*    */   extends PlayerListener
/*    */ {
/*    */   public void onPlayerInteractEntity(PlayerInteractEntityEvent event)
/*    */   {
/* 28 */     switch (((CraftEntity)event.getRightClicked()).getHandle().getAirTicks()) {
/*    */     case 12345: 
/* 30 */       ((CraftEntity)event.getRightClicked()).getHandle().b(((CraftEntity)event.getPlayer()).getHandle(), 999999);
/* 31 */       break;
/*    */     
/*    */     case 12346: 
/* 34 */       if (!VoxelVoyage.isPermitted(event.getPlayer())) {
/* 35 */         event.getPlayer().sendMessage(ChatColor.GOLD + "You are not permitted to do this. Please input the password or login with an OP account.");
/*    */       }
/*    */       else {
/* 38 */         ((CraftEntity)event.getRightClicked()).getHandle().b(((CraftEntity)event.getPlayer()).getHandle(), 999999);
/*    */       }
/*    */       
/*    */ 
/*    */ 
/*    */ 
/*    */       break;
/*    */     }
/*    */     
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public void onPlayerInteract(PlayerInteractEvent event)
/*    */   {
/* 54 */     if ((event.hasItem()) && ((event.getAction().equals(Action.RIGHT_CLICK_AIR)) || (event.getAction().equals(Action.RIGHT_CLICK_BLOCK))))
/*    */     {
/* 56 */       if (event.getItem().getTypeId() == VoxelVoyage.voyageItem) {
/* 57 */         Player p = event.getPlayer();
/* 58 */         Entity closest = VoxelVoyage.getEntity(p);
/*    */         
/* 60 */         if (closest != null) {
/* 61 */           if (VoxelVoyage.flying.contains(p.getName())) {
/* 62 */             p.sendMessage(ChatColor.RED + "You may only travel on one entity at a time!");
/* 63 */             return;
/*    */           }
/* 65 */           closest.b(((CraftPlayer)p).getHandle(), 3);
/*    */         } else {
/* 67 */           p.sendMessage(ChatColor.RED + "Your offering is rejected.");
/*    */         }
/* 69 */       } else if ((event.getItem().getType().equals(Material.INK_SACK)) && (event.getItem().getDurability() == 15))
/*    */       {
/* 71 */         double distance = 0.7D;
/* 72 */         Location player_loc = event.getPlayer().getLocation();
/* 73 */         double rot_x = (player_loc.getYaw() + 90.0F) % 360.0F;
/* 74 */         double rot_y = player_loc.getPitch() * -1.0F;
/* 75 */         double rot_ycos = Math.cos(Math.toRadians(rot_y));
/* 76 */         double rot_ysin = Math.sin(Math.toRadians(rot_y));
/* 77 */         double rot_xcos = Math.cos(Math.toRadians(rot_x));
/* 78 */         double rot_xsin = Math.sin(Math.toRadians(rot_x));
/*    */         
/* 80 */         double h_length = distance * rot_ycos;
/* 81 */         double y_offset = distance * rot_ysin;
/* 82 */         double x_offset = h_length * rot_xcos;
/* 83 */         double z_offset = h_length * rot_xsin;
/*    */         
/* 85 */         double target_x = x_offset + player_loc.getX();
/* 86 */         double target_y = y_offset + player_loc.getY() + 1.65D;
/* 87 */         double target_z = z_offset + player_loc.getZ();
/*    */         
/* 89 */         event.getPlayer().getWorld().playEffect(new Location(player_loc.getWorld(), target_x, target_y, target_z), Effect.SMOKE, 4);
/*    */       }
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\intellij\VoxelVoyage\VoxelVoyage.jar!\com\thevoxelbox\voyage\VPlayer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */