package com.thevoxelbox.voyage;

public enum VoyagerType {
    DRAGON("Dragon"),
    BLAZE("Blaze"),
    SQUID("Squid");

    private String name;

    VoyagerType(String name) {
        this.name = name;
    }

    public String getType() {
        return name;
    }
}
