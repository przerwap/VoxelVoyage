package com.thevoxelbox.voyage.repositories;

import com.thevoxelbox.voyage.BezierCurve;
import com.thevoxelbox.voyage.entity.Voyage;

import java.util.UUID;

public interface TempRepository {
    void addToolUser(UUID uuid, Voyage voyage);

    boolean addPoint(UUID uuid, BezierCurve bezierCurve);

    Voyage getVoyage(UUID uuid);
}
