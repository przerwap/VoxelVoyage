package com.thevoxelbox.voyage.repositories;

import com.thevoxelbox.voyage.entities.Voyage;

import java.util.Map;

/*
    This is the storage system that will hold all of the active voyage routes.
 */
public interface VoyageRepository {
    void addVoyageMap(Voyage voyage);

    Map<String, Voyage> getVoyages();

    Voyage getVoyage(String voyage);
}
