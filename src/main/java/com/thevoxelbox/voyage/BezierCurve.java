package com.thevoxelbox.voyage;

public class BezierCurve {
    public static BezierPoint getBezier(double t, BezierPoint[] curve) {
        if (curve.length == 2) {
            if (t >= 1) {
                return curve[1];
            }
            return new BezierPoint(curve[0].x + t * (curve[1].x - curve[0].x), curve[0].y + t * (curve[1].y - curve[0].y), curve[0].z + t * (curve[1].z - curve[0].z));
        }

        BezierPoint[] next = new BezierPoint[curve.length - 1];
        for (int i = 1; i < curve.length; i++) {
            next[i - 1] = getBezier(t, new BezierPoint[]{curve[i - 1], curve[i]});
        }

        return getBezier(t, next);
    }
}
