package com.thevoxelbox.voyage;

import net.minecraft.server.v1_12_R1.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;

import static com.thevoxelbox.voyage.DragonAction.*;
import static com.thevoxelbox.voyage.VoxelVoyage.VOYAGE_ENTITIES;

public class PrzlabsDragon extends EntityEnderDragon implements PrzlabsEntity, NPC {
    private ArrayList<BezierPoint> path = new ArrayList<>();
    private BezierPoint[] currentCurve = new BezierPoint[0];
    private int currentPoint = 1;
    private double currT = 1.0;
    private double stepT = 0.1;
    private double stepSpeed = 0.75;
    private Player focused;
    private String focusName;
    private BezierPoint next;
    private double lastDist = 9999999;
    private boolean motherEntity = false;
    private boolean pathEnd = false;
    private boolean controlled = false;
    private boolean controllRot = false;
    private boolean controllPos = false;
    private double motherx;
    private double mothery;
    private double motherz;
    private PrzlabsCrystal[] crystalPath;
    private double distance = 12;
    private int lastSlot = 0;
    private boolean demo = false;
    private boolean sendDemos = false;

    public PrzlabsDragon(World world) {
        super(world);
    }

    public PrzlabsDragon(World world, boolean mother) {
        super(world);
        motherEntity = mother;
        storeVoyageEntity();
    }

    public PrzlabsDragon(World world, boolean mother, Location idle) {
        this(world, mother, idle, 0.75, new ArrayList<>());
    }

    public PrzlabsDragon(World world, boolean mother, Location idle, double speed) {
        this(world, mother, idle, speed, new ArrayList<>());
    }

    public PrzlabsDragon(World world, boolean mother, Location idle, double speed, ArrayList<BezierPoint> bezierPoints) {
        super(world);
        motherEntity = mother;
        setPositionRotation(idle.getX(), idle.getY(), idle.getZ(), idle.getYaw() + 180, idle.getPitch());
        motherx = idle.getX();
        mothery = idle.getY();
        motherz = idle.getZ();
        stepSpeed = speed;
        if (!bezierPoints.isEmpty()) {
            setPath(bezierPoints);
        }
        storeVoyageEntity();
        VoyageData.saveVoyagerPath(path, motherx, mothery, motherz, stepSpeed, world, VoyagerType.DRAGON);
    }

    public PrzlabsDragon(World world, ArrayList<BezierPoint> flightPath, Player pilot) {
        super(world);
        if (flightPath == null || flightPath.size() < 2) {
            VoxelVoyage.log.info("[VoxelVoyage] [5] Killing entity ID " + getUniqueID());
            die();
            return;
        }
        if (VoxelVoyage.flying.contains(pilot.getName())) {
            die();
            return;
        }
        focused = pilot;
        focusName = focused.getName();
        VoxelVoyage.flying.add(focusName);
        path = flightPath;
        setPosition(path.get(0).x, path.get(0).y, path.get(0).z);
        currentCurve = new BezierPoint[]{path.get(0), path.get(1)};
        getCC(path.get(0));
        getStep();
        next = BezierCurve.getBezier(stepT, currentCurve);
        this.yaw = getCorrectYaw(next.x, next.z);
    }

    public PrzlabsDragon(World world, ArrayList<BezierPoint> flightPath, boolean demomode) {
        super(world);
        if (flightPath == null || flightPath.size() < 2) {
            VoxelVoyage.log.info("[VoxelVoyage] [5] Killing entity ID " + getUniqueID());
            die();
            return;
        }
        demo = demomode;
        path = flightPath;
        setPosition(path.get(0).x, path.get(0).y, path.get(0).z);
        currentCurve = new BezierPoint[]{path.get(0), path.get(1)};
        getCC(path.get(0));
        getStep();
        next = BezierCurve.getBezier(stepT, currentCurve);
        this.yaw = getCorrectYaw(next.x, next.z);
    }

    private void storeVoyageEntity() {
        if (VOYAGE_ENTITIES.containsKey(getBukkitEntity().getWorld().getUID())) {
            VOYAGE_ENTITIES.get(getBukkitEntity().getWorld().getUID()).put(getUniqueID(), this);
        } else {
            VOYAGE_ENTITIES.put(getBukkitEntity().getWorld().getUID(), new TreeMap<>());
            VOYAGE_ENTITIES.get(getBukkitEntity().getWorld().getUID()).put(getUniqueID(), this);
        }
    }

    @Override
    public CraftEntity getBukkitEntity() {
        if (this.bukkitEntity == null) {
            this.bukkitEntity = new UselessDragon(world.getServer(), this);
        }
        return this.bukkitEntity;
    }

    private float getCorrectYaw(double targetx, double targetz) {
        if (locZ > targetz) {
            return (float) -Math.toDegrees(Math.atan((locX - targetx) / (locZ - targetz)));
        } else if (locZ < targetz) {
            return (float) -Math.toDegrees(Math.atan((locX - targetx) / (locZ - targetz))) + 180;
        } else {
            return this.yaw;
        }
    }

    private void getStep() {
        if (currentCurve.length > 2) {
            double cdist = 0;
            BezierPoint lastbezi = currentCurve[0];

            for (double tt = 0.0; tt < 1.0; tt += 0.01) {
                BezierPoint newbezi = BezierCurve.getBezier(tt, currentCurve);
                cdist += Math.pow(Math.pow(newbezi.x - lastbezi.x, 2) + Math.pow(newbezi.y - lastbezi.y, 2) + Math.pow(newbezi.z - lastbezi.z, 2), 0.5);
                lastbezi = newbezi;
            }
            stepT = stepSpeed / cdist;
            currT = stepT;
        }
    }

    private boolean switchPoint() {
        double currDist = Math.pow(Math.pow(locX - currentCurve[1].x, 2) + Math.pow(locY - currentCurve[1].y, 2) + Math.pow(locZ - currentCurve[1].z, 2), 0.5);
        if (currDist <= lastDist) {
            lastDist = currDist;
            return true;
        } else {
            lastDist = 9999999;
            return false;
        }
    }

    private void getCC(BezierPoint currentbp) {
        if (currentPoint + 1 < path.size()) {
            currentCurve = new BezierPoint[]{currentbp, path.get(currentPoint), path.get(currentPoint + 1)};
        } else {
            pathEnd = true;
        }
    }

    private long lastDemo = 0;

    @Override
    public void B_() {
//        VoxelVoyage.log.info("[VoxelVoyage] Tick " + getUniqueID());
        if (focused != null && !focused.isOnline()) {
            focused = null;
        }
        if (motherEntity && !demo) {
            if (sendDemos) {
                if (System.currentTimeMillis() - lastDemo > 6000) {
                    lastDemo = System.currentTimeMillis();
                    PrzlabsDragon dragon = new PrzlabsDragon(world, path, true);
                    world.addEntity(dragon, SpawnReason.CUSTOM);
                }
            }
            if (controlled) {
                changeDistance(focused);
                Location player_loc = focused.getLocation();
                double rot_x = (player_loc.getYaw() + 90) % 360;
                double rot_y = player_loc.getPitch() * -1;
                double rot_ycos = Math.cos(Math.toRadians(rot_y));
                double rot_ysin = Math.sin(Math.toRadians(rot_y));
                double rot_xcos = Math.cos(Math.toRadians(rot_x));
                double rot_xsin = Math.sin(Math.toRadians(rot_x));

                double h_length = (distance * rot_ycos);
                double y_offset = (distance * rot_ysin);
                double x_offset = (h_length * rot_xcos);
                double z_offset = (h_length * rot_xsin);

                double target_x = x_offset + player_loc.getX();
                double target_y = y_offset + player_loc.getY() + 1.65;
                double target_z = z_offset + player_loc.getZ();
                setPosition(target_x, target_y, target_z);
                this.yaw = getCorrectYaw(player_loc.getX(), player_loc.getZ());
                motherx = target_x;
                mothery = target_y;
                motherz = target_z;
            } else if (controllRot) {
                this.yaw = getCorrectYaw(focused.getLocation().getX(), focused.getLocation().getZ());
            } else if (controllPos) {
                changeDistance(focused);
                Location player_loc = focused.getLocation();
                double rot_x = (player_loc.getYaw() + 90) % 360;
                double rot_y = player_loc.getPitch() * -1;
                double rot_ycos = Math.cos(Math.toRadians(rot_y));
                double rot_ysin = Math.sin(Math.toRadians(rot_y));
                double rot_xcos = Math.cos(Math.toRadians(rot_x));
                double rot_xsin = Math.sin(Math.toRadians(rot_x));

                double h_length = (distance * rot_ycos);
                double y_offset = (distance * rot_ysin);
                double x_offset = (h_length * rot_xcos);
                double z_offset = (h_length * rot_xsin);

                double target_x = x_offset + player_loc.getX();
                double target_y = y_offset + player_loc.getY() + 1.65;
                double target_z = z_offset + player_loc.getZ();
                setPosition(target_x, target_y, target_z);
                motherx = target_x;
                mothery = target_y;
                motherz = target_z;
            } else {
                setPosition(motherx, mothery, motherz);
            }
        } else {
            if ((focused != null && focused.isOnline()) || demo) {
                if ((passengers == null || passengers.isEmpty()) && !demo) {
                    ((CraftPlayer) focused).getHandle().a(this, true);
                }
                if (path != null && !path.isEmpty() && path.size() > 1) {
                    setPosition(next.x, next.y, next.z);
                    if (switchPoint() || pathEnd) {
                        next = BezierCurve.getBezier(currT, currentCurve);
                        currT += stepT;
                        if (currT > 1.0) {
                            if (focusName != null) {
                                VoxelVoyage.flying.remove(focusName);
                            }
                            die();
                        }
                    } else {
                        getCC(next);
                        if (!pathEnd) {
                            getStep();
                            currentPoint++;
                            if (currentPoint == path.size()) {
                                currentPoint = 1;
                            }
                        } else {
                            next = BezierCurve.getBezier(currT, currentCurve);
                            currT += stepT;
                        }
                    }
                    this.yaw = getCorrectYaw(next.x, next.z);
                } else {
                    if (focusName != null) {
                        VoxelVoyage.flying.remove(focusName);
                    }
                    die();
                }
            } else {
                if (focusName != null) {
                    VoxelVoyage.flying.remove(focusName);
                }
                die();
            }
        }
    }

    private void changeDistance(Player user) {
        if (user == null) {
            return;
        }
        int currentSlot = user.getInventory().getHeldItemSlot();
        if (lastSlot == 0) {
            if (currentSlot != 0 && currentSlot > 5) {
                distance++;
            } else if (currentSlot != 0 && currentSlot < 6) {
                distance--;
            }
        } else if (lastSlot == 8) {
            if (currentSlot != 8 && currentSlot < 5) {
                distance--;
            } else if (currentSlot != 8 && currentSlot > 4) {
                distance++;
            }
        } else {
            if (currentSlot < lastSlot) {
                distance++;
            } else if (currentSlot > lastSlot) {
                distance--;
            }
        }
        lastSlot = currentSlot;
    }

    @Override
    public void a(NBTTagCompound in) {
        VoxelVoyage.log.info("[VoxelVoyage] Loading entity ID " + getUniqueID());
        motherEntity = in.getBoolean("isMother");
        if (motherEntity) {
            storeVoyageEntity();
            NBTTagList mpos = in.getList("Mother", 6);
            if (in.hasKey("SpeedT")) {
                stepSpeed = in.getDouble("SpeedT");
            }
            if (mpos != null && mpos.size() != 0) {
                motherx = mpos.f(0);
                mothery = mpos.f(1);
                motherz = mpos.f(2);
            } else {
                motherx = locX;
                mothery = locY;
                motherz = locZ;
            }

            NBTTagList lpath = in.getList("Path", 9);
            if (lpath == null || lpath.size() == 0) {
                return;
            }
            for (int count = 0; count < lpath.size(); count++) {
                NBTTagList dbl = (NBTTagList) lpath.i(count);
                path.add(new BezierPoint(dbl.f(0), dbl.f(1), dbl.f(2)));
            }
            if (next == null) {
                next = path.get(0);
            }

            if (path.size() > 2) {
                getCC(next);
                getStep();
                lastDist = 9999999;
            }
        } else {
            VoxelVoyage.log.info("[VoxelVoyage] [1] Killing entity ID " + getUniqueID());
            die();
        }
    }

    @Override
    public void b(NBTTagCompound out) {
        VoxelVoyage.log.info("[VoxelVoyage] Saving entity ID " + getUniqueID());
        out.setBoolean("isMother", motherEntity);
        if (motherEntity) {
            out.set("Mother", this.a(motherx, mothery, motherz));
            out.setDouble("SpeedT", stepSpeed);
            if (path == null || path.isEmpty()) {
                return;
            }
            NBTTagList spath = new NBTTagList();
            for (BezierPoint bezi : path) {
                NBTTagList dbl = new NBTTagList();
                dbl.add(new NBTTagDouble(bezi.x));
                dbl.add(new NBTTagDouble(bezi.y));
                dbl.add(new NBTTagDouble(bezi.z));
                spath.add(dbl);
            }
            out.set("Path", spath);
        }
    }

    @Override
    public boolean isAlive() {
        return true;
    }

    @Override
    public void rightClick(Player user, int slot) {
        if (focused == null || focused.getUniqueId() != user.getUniqueId()) {
            focused = user;
        }
        if (slot == TOGGLE_CONTROL) {
            toggleControl(user);
        } else if (slot == ADD_POINT) {
            addPoint(user);
        } else if (slot == REMOVE_VOYAGER_PATH) {
            VoyageData.removeVoyagerPath(path, motherx, mothery, motherz, stepSpeed, world, VoyagerType.DRAGON);
            die();
        } else if (slot == BEGIN_VOYAGE) {
            voyage(user);
        } else if (slot == DRAW_VOYAGE_PATH) {
            drawPath();
        } else if (slot == CLEAN_VOYAGE_PATH) {
            cleanPath();
        } else if (slot == TOGGLE_DEMO) {
            toggleDemo(user);
        } else if (slot == TOGGLE_ROTATION_CONTROL) {
            toggleControlRot(user);
        } else if (slot == TOGGLE_POSITION_CONTROL) {
            toggleControlPos(user);
        }
    }

    public void toggleDemo(Player user) {
        sendDemos = !sendDemos;
        user.sendMessage(ChatColor.GOLD + "Demo mode turned " + ChatColor.AQUA + (sendDemos ? "on" : "off"));
    }

    public void toggleControl(Player user) {
        if (focused == null || focused.getUniqueId() != user.getUniqueId()) {
            focused = user;
        }
        controlled = !controlled;
        controllRot = false;
        controllPos = false;
        distance = 12;
        if (controlled) {
            VoyageData.removeVoyagerPath(path, motherx, mothery, motherz, stepSpeed, world, VoyagerType.DRAGON);
        } else {
            VoyageData.saveVoyagerPath(path, motherx, mothery, motherz, stepSpeed, world, VoyagerType.DRAGON);
        }
    }

    public void toggleControlRot(Player user) {
        if (focused == null || focused.getUniqueId() != user.getUniqueId()) {
            focused = user;
        }
        controllRot = !controllRot;
        controlled = false;
        controllPos = false;
        distance = 12;
    }

    public void toggleControlPos(Player user) {
        if (focused == null || focused.getUniqueId() != user.getUniqueId()) {
            focused = user;
        }
        controllPos = !controllPos;
        controlled = false;
        controllRot = false;
        distance = 12;
        if (controllPos) {
            VoyageData.removeVoyagerPath(path, motherx, mothery, motherz, stepSpeed, world, VoyagerType.DRAGON);
        } else {
            VoyageData.saveVoyagerPath(path, motherx, mothery, motherz, stepSpeed, world, VoyagerType.DRAGON);
        }
    }

    public void voyage(Player user) {
        PrzlabsDragon dragon = new PrzlabsDragon(world, path, user);
        world.addEntity(dragon, SpawnReason.CUSTOM);
    }

    public void addPoint(Player user) {
        if (path == null) {
            path = new ArrayList<>();
            currentCurve = new BezierPoint[1];
        }
        path.add(new BezierPoint(user.getLocation()));

        currentPoint = 1;

        if (next == null) {
            next = path.get(0);
        }

        if (path.size() > 2) {
            getCC(next);
            getStep();
            lastDist = 9999999;
        }
        VoyageData.saveVoyagerPath(path, motherx, mothery, motherz, stepSpeed, world, VoyagerType.DRAGON);
    }

    public void setPath(ArrayList<BezierPoint> points) {
        path = points;

        currentPoint = 1;

        if (next == null) {
            next = path.get(0);
        }

        if (path.size() > 2) {
            getCC(next);
            getStep();
            lastDist = 9999999;
        }
    }

    public void drawPath() {
        if (crystalPath == null) {
            crystalPath = new PrzlabsCrystal[path.size()];
        } else {
            if (crystalPath != null && crystalPath.length != 0) {
                for (PrzlabsCrystal plc : crystalPath) {
                    if (plc == null) {
                        continue;
                    }
                    plc.die();
                }
                crystalPath = new PrzlabsCrystal[path.size()];
            }
        }
        if (path == null || path.isEmpty()) {
            return;
        }
        for (int count = 0; count < path.size(); count++) {
            PrzlabsCrystal crystal = new PrzlabsCrystal(world, path.get(count));
            if (world.addEntity(crystal, SpawnReason.CUSTOM)) {
                crystalPath[count] = crystal;
            }
        }
    }

    public void cleanPath() {
        if (crystalPath == null) {
            crystalPath = new PrzlabsCrystal[path.size()];
        } else {
            if (crystalPath != null && crystalPath.length != 0) {
                for (PrzlabsCrystal plc : crystalPath) {
                    if (plc == null) {
                        continue;
                    }
                    plc.die();
                }
                crystalPath = new PrzlabsCrystal[path.size()];
            }
        }
        VoyageData.saveVoyagerPath(path, motherx, mothery, motherz, stepSpeed, world, VoyagerType.DRAGON);
    }

    @Override
    public void die() {
        if (VOYAGE_ENTITIES.containsKey(getBukkitEntity().getWorld().getUID())) {
            VOYAGE_ENTITIES.get(getBukkitEntity().getWorld().getUID()).remove(getUniqueID());
        }
        if (VoxelVoyage.selected.containsValue(this)) {
            String pname = null;
            for (Entry<String, Entity> entr : VoxelVoyage.selected.entrySet()) {
                if (entr.getValue().getUniqueID() == getUniqueID()) {
                    pname = entr.getKey();
                }
            }
            VoxelVoyage.selected.remove(pname);
        }
        if (!motherEntity && focusName != null) {
            VoxelVoyage.flying.remove(focusName);
        }
        super.die();
    }

    @Override
    public int getAirTicks() {
        return (motherEntity ? 12347 : 12348);
    }
}
