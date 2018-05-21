 package com.thevoxelbox.voyage;
 import org.bukkit.Location;
 public class BeziPoint
 {
   public double x;
   public double y;
   public double z;
   
   public BeziPoint(double bx, double by, double bz)
   {
   this.x = bx;
   this.y = by;
   this.z = bz;
   }
   
   public BeziPoint(Location l) {
    this.x = l.getX();
    this.y = l.getY();
    this.z = l.getZ();
   }
 }
