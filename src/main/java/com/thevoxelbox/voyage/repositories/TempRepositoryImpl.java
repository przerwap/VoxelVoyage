package com.thevoxelbox.voyage.repositories;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.thevoxelbox.voyage.BezierCurve;
import com.thevoxelbox.voyage.entity.Voyage;
import com.thevoxelbox.voyage.utils.Tuple2;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class TempRepositoryImpl implements TempRepository {
    private Cache<UUID, Voyage> toolStorage = CacheBuilder.newBuilder()
            .concurrencyLevel(4)
            .expireAfterWrite(15, TimeUnit.MINUTES)
            .build();

    @Override
    public void addToolUser(UUID uuid, Voyage voyage) {
        toolStorage.put(uuid, voyage);
    }

    @Override
    public boolean addPoint(UUID uuid, BezierCurve bezierCurve) {
        return toolStorage.getIfPresent(uuid).getBezierCurves().add(bezierCurve);
    }

    @Override
    public Voyage getVoyage(UUID uuid) {
        return toolStorage.getIfPresent(uuid);
    }
}
