 package com.thevoxelbox.voyage.entities;
 import javafx.geometry.Point3D;
 import org.bukkit.Location;

 public class BeziPoint extends Point3D
 {
   private String world;
   
   public BeziPoint(double bx, double by, double bz)
   {
       super(bx, by, bz);
   }
   
   public BeziPoint(Location l) {
       super(l.getX(), l.getY(), l.getZ());
   }

     public String getWorld() {
         return world;
     }
 }
