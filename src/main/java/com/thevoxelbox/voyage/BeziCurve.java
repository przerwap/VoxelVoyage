/*    */ package com.thevoxelbox.voyage;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class BeziCurve
/*    */ {
/*    */   public static BeziPoint getBezi(double t, BeziPoint[] curve)
/*    */   {
/* 15 */     if (curve.length == 2) {
/* 16 */       if (t >= 1.0D) {
/* 17 */         return curve[1];
/*    */       }
/* 19 */       return new BeziPoint(curve[0].x + t * (curve[1].x - curve[0].x), curve[0].y + t * (curve[1].y - curve[0].y), curve[0].z + t * (curve[1].z - curve[0].z));
/*    */     }
/*    */     
/* 22 */     BeziPoint[] next = new BeziPoint[curve.length - 1];
/* 23 */     for (int i = 1; i < curve.length; i++) {
/* 24 */       next[(i - 1)] = getBezi(t, new BeziPoint[] { curve[(i - 1)], curve[i] });
/*    */     }
/*    */     
/* 27 */     return getBezi(t, next);
/*    */   }
/*    */ }


/* Location:              C:\intellij\VoxelVoyage\VoxelVoyage.jar!\com\thevoxelbox\voyage\BeziCurve.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */