package com.thevoxelbox.voyage;

import net.minecraft.server.v1_12_R1.World;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VoyageData {
    public static void saveVoyagerPath(ArrayList<BezierPoint> path, double x, double y, double z, double speed, World world, VoyagerType type) {
        File f = new File("plugins/VoxelVoyage/VoyagerBackups/" + world.getWorld().getName() + "/" + type.getType() + "/" + x + "_" + y + "_" + z);
        f.getParentFile().mkdirs();

        try {
            f.createNewFile();

            DataOutputStream out = new DataOutputStream(new FileOutputStream(f));

            out.writeDouble(speed);
            for (BezierPoint bp : path) {
                out.writeDouble(bp.x);
                out.writeDouble(bp.y);
                out.writeDouble(bp.z);
            }

            out.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(VoyageData.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(VoyageData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void removeVoyagerPath(ArrayList<BezierPoint> path, double x, double y, double z, double speed, World world, VoyagerType type) {
        File f = new File("plugins/VoxelVoyage/VoyagerBackups/" + world.getWorld().getName() + "/" + type.getType() + "/" + x + "_" + y + "_" + z);
        if (f.exists()) {
            f.delete();
        }
    }

    public static void loadVoyagersFromBackup(World world) {
        if (world == null) {
            for (org.bukkit.World w : Bukkit.getWorlds()) {
                loadVoyagersForWorld(((CraftWorld) w).getHandle());
            }
        } else {
            loadVoyagersForWorld(world);
        }
    }

    private static void loadVoyagersForWorld(World world) {
        File f = new File("plugins/VoxelVoyage/VoyagerBackups/" + world.getWorld().getName() + "/");
        if (!f.exists()) {
            return;
        }
        if (f.isDirectory()) {
            File[] voyagers = f.listFiles();
            for (File foldervoy : voyagers) {
                if (foldervoy.isDirectory()) {
                    File[] paths = foldervoy.listFiles();
                    for (File path : paths) {
                        String[] coords = path.getName().split("_");
                        if (coords != null && coords.length == 3) {
                            double x = Double.parseDouble(coords[0]);
                            double y = Double.parseDouble(coords[1]);
                            double z = Double.parseDouble(coords[2]);
                            Location loc = new Location(world.getWorld(), x, y, z);
                            Chunk c = world.getWorld().getChunkAt(loc);
                            for (org.bukkit.entity.Entity e : c.getEntities()) {
                                switch (((CraftEntity) e).getHandle().getAirTicks()) {
                                    case 12347:
                                    case 12348:
                                    case 12349:
                                    case 12350:
                                        Location eloc = e.getLocation();
                                        if (Math.floor(x) == Math.floor(eloc.getX())
                                                && Math.floor(y) == Math.floor(eloc.getY())
                                                && Math.floor(z) == Math.floor(eloc.getZ())) {
                                            ((CraftEntity) e).getHandle().die();
                                        }
                                        break;

                                    default:
                                        break;
                                }
                            }
                            ArrayList<BezierPoint> bezierPoints = new ArrayList<>();
                            try {
                                DataInputStream in = new DataInputStream(new FileInputStream(path));

                                double speed = -1;
                                try {
                                    speed = in.readDouble();
                                    while (true) {
                                        BezierPoint bz = new BezierPoint(in.readDouble(), in.readDouble(), in.readDouble());
                                        bezierPoints.add(bz);
                                    }
                                } catch (EOFException ignored) {
                                } catch (IOException exo) {
                                    Logger.getLogger(VoyageData.class.getName()).log(Level.SEVERE, null, exo);
                                }
                                try {
                                    in.close();
                                } catch (IOException ex) {
                                    Logger.getLogger(VoyageData.class.getName()).log(Level.SEVERE, null, ex);
                                }

                                if (VoyagerType.DRAGON.getType().equals(foldervoy.getName())) {
                                    PrzlabsDragon voyager = new PrzlabsDragon(world, true, loc, (speed == -1 ? 0.75 : speed), bezierPoints);
                                    voyager.setPath(bezierPoints);
                                    world.addEntity(voyager, SpawnReason.CUSTOM);
                                }
                            } catch (FileNotFoundException ex) {
                                Logger.getLogger(VoyageData.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                }
            }
        }
    }
}
