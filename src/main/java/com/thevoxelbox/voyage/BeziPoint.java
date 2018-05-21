/*    */ package com.thevoxelbox.voyage;
/*    */ 
/*    */ import org.bukkit.Location;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class BeziPoint
/*    */ {
/*    */   public double x;
/*    */   public double y;
/*    */   public double z;
/*    */   
/*    */   public BeziPoint(double bx, double by, double bz)
/*    */   {
/* 20 */     this.x = bx;
/* 21 */     this.y = by;
/* 22 */     this.z = bz;
/*    */   }
/*    */   
/*    */   public BeziPoint(Location l) {
/* 26 */     this.x = l.getX();
/* 27 */     this.y = l.getY();
/* 28 */     this.z = l.getZ();
/*    */   }
/*    */ }


/* Location:              C:\intellij\VoxelVoyage\VoxelVoyage.jar!\com\thevoxelbox\voyage\BeziPoint.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */