package com.thevoxelbox.voyage.entity;

import com.thevoxelbox.voyage.BezierCurve;

import java.util.List;

public class Voyage {
    private String name;
    private List<BezierPoint> bezierPoint;
    private String linked;

    public Voyage(String name, List<BezierPoint> bezierPoint, String linked) {
        this.name = name;
        this.bezierPoint = bezierPoint;
        this.linked = linked;
    }

    public String getName() {
        return name;
    }

    public Voyage setName(String name) {
        this.name = name;
        return this;
    }

    public List<BezierPoint> getBezierPoint() {
        return bezierPoint;
    }

    public Voyage setBezierCurves(List<BezierPoint> bezierCurves) {
        this.bezierPoint = bezierCurves;
        return this;
    }

    public String getLinked() {
        return linked;
    }

    public Voyage setLinked(String linked) {
        this.linked = linked;
        return this;
    }
}
