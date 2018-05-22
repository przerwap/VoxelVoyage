package com.thevoxelbox.voyage.repositories;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class TransitRepositoryImpl implements TransitRepository {
    private Cache<UUID, String> inTransiCache = CacheBuilder.newBuilder()
            .concurrencyLevel(4)
            .expireAfterWrite(15, TimeUnit.MINUTES)
            .build();

    @Override
    public void addNewInTransit(UUID uuid, String name) {
        inTransiCache.put(uuid, name);
    }

    @Override
    public String getInTransit(UUID uuid) {
        return inTransiCache.getIfPresent(uuid);
    }
    @Override
    public final ImmutableMap<UUID, String> getinTransitList() {
        return ImmutableMap.copyOf(inTransiCache.asMap());
    }

    @Override
    public void invalidateEntry(UUID uuid) {
        inTransiCache.invalidate(uuid);
    }
}
