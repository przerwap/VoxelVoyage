package com.thevoxelbox.voyage.entities;

import javafx.geometry.Point3D;

import java.util.Map;

public class Voyage {
    private String name;
    private String linked;
    private boolean isLinked;
    private Map<Integer, BeziPoint> point3DMap;

    public Voyage(String name, String linked, boolean isLinked, Map<Integer, BeziPoint> point3DMap) {
        this.name = name;
        this.linked = linked;
        this.isLinked = isLinked;
        this.point3DMap = point3DMap;
    }

    public String getName() {
        return name;
    }

    public Voyage setName(String name) {
        this.name = name;
        return this;
    }

    public String getLinked() {
        return linked;
    }

    public Voyage setLinked(String linked) {
        this.linked = linked;
        return this;
    }

    public boolean isLinked() {
        return isLinked;
    }

    public Voyage setLinked(boolean linked) {
        isLinked = linked;
        return this;
    }

    public Map<Integer, BeziPoint> getPoint3DMap() {
        return point3DMap;
    }

    public Voyage setPoint3DMap(Map<Integer, BeziPoint> point3DMap) {
        this.point3DMap = point3DMap;
        return this;
    }
}
