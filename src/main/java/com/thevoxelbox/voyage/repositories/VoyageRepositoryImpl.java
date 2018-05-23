package com.thevoxelbox.voyage.repositories;

import com.thevoxelbox.voyage.entity.Voyage;

import java.util.HashMap;
import java.util.Map;

public class VoyageRepositoryImpl implements VoyageRepository {
    Map<String, Voyage> voyageMap = new HashMap<>();

    @Override
    public boolean hasVoyage(String voyage) {
        return voyageMap.containsKey(voyage);
    }
}
