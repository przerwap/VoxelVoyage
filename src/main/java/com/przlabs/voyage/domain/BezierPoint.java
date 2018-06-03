package com.przlabs.voyage.domain;

import org.bukkit.Location;

public class BezierPoint {
    public double x;
    public double y;
    public double z;

    public BezierPoint(double bx, double by, double bz) {
        x = bx;
        y = by;
        z = bz;
    }

    public BezierPoint(Location l) {
        x = l.getX();
        y = l.getY();
        z = l.getZ();
    }
}
