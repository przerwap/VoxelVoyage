package com.thevoxelbox.voyage.repositories;

import com.google.common.collect.ImmutableMap;

import java.util.UUID;

public interface TransitRepository {
    void addNewInTransit(UUID uuid, String name);

    String getInTransit(UUID uuid);

    ImmutableMap<UUID, String> getinTransitList();

    void invalidateEntry(UUID uuid);
}
