package com.thevoxelbox.voyage.entity;

import javafx.geometry.Point3D;
import org.bukkit.Location;

public class BezierPoint extends Point3D {
    public BezierPoint(double bx, double by, double bz) {
        super(bx,by,bz);
    }

    public BezierPoint(Location l) {
        super(l.getX(), l.getY(), l.getZ());
    }
}
