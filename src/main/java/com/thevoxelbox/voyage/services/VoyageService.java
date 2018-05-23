package com.thevoxelbox.voyage.services;

import com.google.inject.Inject;
import com.thevoxelbox.voyage.BezierCurve;
import com.thevoxelbox.voyage.VoxelVoyage;
import com.thevoxelbox.voyage.entity.BezierPoint;
import com.thevoxelbox.voyage.entity.Voyage;
import com.thevoxelbox.voyage.repositories.TempRepository;
import com.thevoxelbox.voyage.repositories.VoyageRepository;
import com.thevoxelbox.voyage.utils.Response;
import javafx.geometry.Point3D;
import org.bukkit.Location;

import javax.crypto.Cipher;
import java.util.ArrayList;
import java.util.UUID;

public class VoyageService {
    @Inject
    TempRepository tempRepository;
    @Inject
    VoyageRepository voyageRepository;

    public Response createEntry(String name, UUID uuid, boolean hasPassword, String pw1, boolean d) {
        if (!voyageRepository.hasVoyage(name)) {
            if (name.matches("^(?=.{5,20}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$")) {
                Voyage voyage = new Voyage(name, new ArrayList<>(), null);
                tempRepository.addToolUser(uuid, voyage);
                return Response.SUCCESS;
            }
            return Response.INVALID_FORMAT;
        } else {
            return Response.NAME_IN_USE;
        }
    }

    public Response createToolHandler(UUID uuid, Location location) {
        Voyage voxelVoyage = tempRepository.getVoyage(uuid);

        if (null != voxelVoyage) {
            //checks to see if the location distance exceeds the limit.
            int size = voxelVoyage.getBezierPoint().size() -1;
            BezierPoint bezierPoint = new BezierPoint(location);
            if ( voxelVoyage.getBezierPoint().get(size).distance(bezierPoint) > 50) {//TODO REMOVE THIS FROM HARDCODING.

            } else {
                return Response.EXCEEDS_MAX_LIMIT;
            }
        }
    }
}
r