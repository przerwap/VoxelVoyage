package com.thevoxelbox.voyage;

public class BeziCurve {
    public static BeziPoint getBezi(double t, BeziPoint[] curve) {
        if (curve.length == 2) {
            if (t >= 1.0D) {
                return curve[1];
            }
            return new BeziPoint(curve[0].x + t * (curve[1].x - curve[0].x), curve[0].y + t * (curve[1].y - curve[0].y), curve[0].z + t * (curve[1].z - curve[0].z));
        }

        BeziPoint[] next = new BeziPoint[curve.length - 1];
        for (int i = 1; i < curve.length; i++) {
            next[(i - 1)] = getBezi(t, new BeziPoint[]{curve[(i - 1)], curve[i]});
        }

        return getBezi(t, next);
    }
}
