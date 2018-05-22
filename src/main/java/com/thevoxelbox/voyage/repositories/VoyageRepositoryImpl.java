package com.thevoxelbox.voyage.repositories;

import com.thevoxelbox.voyage.entities.Voyage;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class VoyageRepositoryImpl implements VoyageRepository {
    private Map<String, Voyage> voyageMap = new HashMap<>();

    @Override
    public void addVoyageMap(Voyage voyage) {
        voyageMap.put(voyage.getName(), voyage);
    }

    @Override
    public final Map<String, Voyage> getVoyages() {
        return voyageMap;
    }

    @Override
    public Voyage getVoyage(String voyage) {
        return voyageMap.get(voyage);
    }
}
