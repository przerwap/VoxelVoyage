package com.thevoxelbox.voyage;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

import net.minecraft.server.DamageSource;
import net.minecraft.server.EntityBlaze;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagDouble;
import net.minecraft.server.NBTTagList;
import net.minecraft.server.NPC;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.inventory.PlayerInventory;


public class PrzlabsBlaze
        extends EntityBlaze
        implements PrzlabsEntity, NPC {
    private ArrayList<BeziPoint> path = new ArrayList();
    private BeziPoint[] currentCurve = new BeziPoint[0];
    private int currentPoint = 1;
    private double currT = 1.0D;
    private double stepT = 0.1D;
    private Player focused;
    private String focusName;
    private BeziPoint next;
    private double lastDist = 9999999.0D;
    private boolean motherEntity = false;
    private boolean pathEnd = false;
    private boolean controlled = false;
    private double motherx;
    private double mothery;
    private double motherz;
    private PrzlabsCrystal[] crystalPath;
    private double distance = 2.5D;
    private int lastSlot = 0;
    private boolean demo = false;
    private boolean sendDemos = false;

    public PrzlabsBlaze(net.minecraft.server.World world) {
        super(world);
    }

    public PrzlabsBlaze(net.minecraft.server.World world, boolean mother) {
        super(world);
        this.motherEntity = mother;
        if (VoxelVoyage.entities.containsKey(getBukkitEntity().getWorld().getUID())) {
            ((TreeMap) VoxelVoyage.entities.get(getBukkitEntity().getWorld().getUID())).put(Integer.valueOf(this.id), this);
        } else {
            VoxelVoyage.entities.put(getBukkitEntity().getWorld().getUID(), new TreeMap());
            ((TreeMap) VoxelVoyage.entities.get(getBukkitEntity().getWorld().getUID())).put(Integer.valueOf(this.id), this);
        }
    }

    public PrzlabsBlaze(net.minecraft.server.World world, boolean mother, Location idle) {
        super(world);
        this.motherEntity = mother;
        setPositionRotation(idle.getX(), idle.getY(), idle.getZ(), idle.getYaw() + 180.0F, idle.getPitch());
        this.motherx = idle.getX();
        this.mothery = idle.getY();
        this.motherz = idle.getZ();
        if (VoxelVoyage.entities.containsKey(getBukkitEntity().getWorld().getUID())) {
            ((TreeMap) VoxelVoyage.entities.get(getBukkitEntity().getWorld().getUID())).put(Integer.valueOf(this.id), this);
        } else {
            VoxelVoyage.entities.put(getBukkitEntity().getWorld().getUID(), new TreeMap());
            ((TreeMap) VoxelVoyage.entities.get(getBukkitEntity().getWorld().getUID())).put(Integer.valueOf(this.id), this);
        }
    }

    public PrzlabsBlaze(net.minecraft.server.World world, ArrayList<BeziPoint> flightPath, Player pilot) {
        super(world);
        if ((flightPath == null) || (flightPath.size() < 2)) {
            die();
            return;
        }
        if (VoxelVoyage.flying.contains(pilot.getName())) {
            die();
            return;
        }
        this.focused = pilot;
        this.focusName = this.focused.getName();
        VoxelVoyage.flying.add(this.focusName);
        this.path = flightPath;
        setPosition(((BeziPoint) this.path.get(0)).x, ((BeziPoint) this.path.get(0)).y, ((BeziPoint) this.path.get(0)).z);
        this.currentCurve = new BeziPoint[]{(BeziPoint) this.path.get(0), (BeziPoint) this.path.get(1)};
        getCC((BeziPoint) this.path.get(0));
        getStep();
        this.next = BeziCurve.getBezi(0.003D, this.currentCurve);
        this.yaw = getCorrectYaw(this.next.x, this.next.z);
    }

    public PrzlabsBlaze(net.minecraft.server.World world, ArrayList<BeziPoint> flightPath, boolean demomode) {
        super(world);
        if ((flightPath == null) || (flightPath.size() < 2)) {
            die();
            return;
        }
        this.demo = demomode;
        this.path = flightPath;
        setPosition(((BeziPoint) this.path.get(0)).x, ((BeziPoint) this.path.get(0)).y, ((BeziPoint) this.path.get(0)).z);
        this.currentCurve = new BeziPoint[]{(BeziPoint) this.path.get(0), (BeziPoint) this.path.get(1)};
        getCC((BeziPoint) this.path.get(0));
        getStep();
        this.next = BeziCurve.getBezi(0.003D, this.currentCurve);
        this.yaw = getCorrectYaw(this.next.x, this.next.z);
    }

    public org.bukkit.entity.Entity getBukkitEntity() {
        if (this.bukkitEntity == null) {
            this.bukkitEntity = new PrzlabsLivingEntity(this.world.getServer(), this);
        }
        return this.bukkitEntity;
    }

    private float getCorrectYaw(double targetx, double targetz) {
        if (this.locZ > targetz)
            return (float) -Math.toDegrees(Math.atan((this.locX - targetx) / (this.locZ - targetz)));
        if (this.locZ < targetz) {
            return (float) -Math.toDegrees(Math.atan((this.locX - targetx) / (this.locZ - targetz))) + 180.0F;
        }
        return this.yaw;
    }

    private void getStep() {
        if (this.currentCurve.length > 2) {
            double cdist = 0.0D;
            BeziPoint lastbezi = this.currentCurve[0];

            for (double tt = 0.0D; tt < 1.0D; tt += 0.01D) {
                BeziPoint newbezi = BeziCurve.getBezi(tt, this.currentCurve);
                cdist += Math.pow(Math.pow(newbezi.x - lastbezi.x, 2.0D) + Math.pow(newbezi.y - lastbezi.y, 2.0D) + Math.pow(newbezi.z - lastbezi.z, 2.0D), 0.5D);
                lastbezi = newbezi;
            }
            this.stepT = (0.75D / cdist);
            this.currT = this.stepT;
        }
    }

    private boolean switchPoint() {
        double currDist = Math.pow(Math.pow(this.locX - this.currentCurve[1].x, 2.0D) + Math.pow(this.locY - this.currentCurve[1].y, 2.0D) + Math.pow(this.locZ - this.currentCurve[1].z, 2.0D), 0.5D);
        if (currDist <= this.lastDist) {
            this.lastDist = currDist;
            return true;
        }
        this.lastDist = 9999999.0D;
        return false;
    }

    private void getCC(BeziPoint currentbp) {
        if (this.currentPoint + 1 < this.path.size()) {
            this.currentCurve = new BeziPoint[]{currentbp, (BeziPoint) this.path.get(this.currentPoint), (BeziPoint) this.path.get(this.currentPoint + 1)};
        } else
            this.pathEnd = true;
    }

    private long lastDemo = 0L;
    long lastSaved;

    public void d() {
        if ((this.focused != null) && (!this.focused.isOnline())) {
            this.focused = null;
        }
        if ((this.motherEntity) && (!this.demo)) {
            if ((this.sendDemos) &&
                    (System.currentTimeMillis() - this.lastDemo > 4000L)) {
                this.lastDemo = System.currentTimeMillis();
                PrzlabsBlaze dragon = new PrzlabsBlaze(this.world, this.path, true);
                this.world.addEntity(dragon, CreatureSpawnEvent.SpawnReason.CUSTOM);
            }

            if (this.controlled) {
                changeDistance(this.focused);
                Location player_loc = this.focused.getLocation();
                double rot_x = (player_loc.getYaw() + 90.0F) % 360.0F;
                double rot_y = player_loc.getPitch() * -1.0F;
                double rot_ycos = Math.cos(Math.toRadians(rot_y));
                double rot_ysin = Math.sin(Math.toRadians(rot_y));
                double rot_xcos = Math.cos(Math.toRadians(rot_x));
                double rot_xsin = Math.sin(Math.toRadians(rot_x));

                double h_length = this.distance * rot_ycos;
                double y_offset = this.distance * rot_ysin;
                double x_offset = h_length * rot_xcos;
                double z_offset = h_length * rot_xsin;

                double target_x = x_offset + player_loc.getX();
                double target_y = y_offset + player_loc.getY() + 1.65D;
                double target_z = z_offset + player_loc.getZ();
                setPosition(target_x, target_y, target_z);
                this.yaw = getCorrectYaw(player_loc.getX(), player_loc.getZ());
                this.motherx = target_x;
                this.mothery = target_y;
                this.motherz = target_z;
                this.lastSaved = 0L;
            } else {
                setPosition(this.motherx, this.mothery, this.motherz);
            }
        } else if (((this.focused != null) && (this.focused.isOnline())) || (this.demo)) {
            if ((this.passenger == null) && (!this.demo)) {
                ((CraftPlayer) this.focused).getHandle().setPassengerOf(this);
            }
            if ((this.path != null) && (!this.path.isEmpty()) && (this.path.size() > 1)) {
                setPosition(this.next.x, this.next.y, this.next.z);
                if ((switchPoint()) || (this.pathEnd)) {
                    this.next = BeziCurve.getBezi(this.currT, this.currentCurve);
                    this.currT += this.stepT;
                    if (this.currT > 1.0D) {
                        if (this.focusName != null) {
                            VoxelVoyage.flying.remove(this.focusName);
                        }

                        die();
                    }
                } else {
                    getCC(this.next);
                    if (!this.pathEnd) {
                        getStep();
                        this.currentPoint += 1;
                        if (this.currentPoint == this.path.size()) {
                            this.currentPoint = 1;
                        }
                    } else {
                        this.next = BeziCurve.getBezi(this.currT, this.currentCurve);
                        this.currT += this.stepT;
                    }
                }
                this.yaw = getCorrectYaw(this.next.x, this.next.z);
            } else {
                if (this.focusName != null) {
                    VoxelVoyage.flying.remove(this.focusName);
                }

                die();
            }
        } else {
            if (this.focusName != null) {
                VoxelVoyage.flying.remove(this.focusName);
            }

            die();
        }
    }

    private void changeDistance(Player user) {
        int currentSlot = user.getInventory().getHeldItemSlot();
        if (this.lastSlot == 0) {
            if ((currentSlot != 0) && (currentSlot > 5)) {
                this.distance += 1.0D;
            } else if ((currentSlot != 0) && (currentSlot < 6)) {
                this.distance -= 1.0D;
            }
        } else if (this.lastSlot == 8) {
            if ((currentSlot != 8) && (currentSlot < 5)) {
                this.distance -= 1.0D;
            } else if ((currentSlot != 8) && (currentSlot > 4)) {
                this.distance += 1.0D;
            }
        } else if (currentSlot < this.lastSlot) {
            this.distance += 1.0D;
        } else if (currentSlot > this.lastSlot) {
            this.distance -= 1.0D;
        }

        this.lastSlot = currentSlot;
    }


    public void a(NBTTagCompound in) {
        this.motherEntity = in.getBoolean("isMother");
        if (this.motherEntity) {
            if (VoxelVoyage.entities.containsKey(getBukkitEntity().getWorld().getUID())) {
                ((TreeMap) VoxelVoyage.entities.get(getBukkitEntity().getWorld().getUID())).put(Integer.valueOf(this.id), this);
            } else {
                VoxelVoyage.entities.put(getBukkitEntity().getWorld().getUID(), new TreeMap());
                ((TreeMap) VoxelVoyage.entities.get(getBukkitEntity().getWorld().getUID())).put(Integer.valueOf(this.id), this);
            }
            NBTTagList mpos = in.getList("Mother");

            if ((mpos != null) && (mpos.size() != 0)) {
                this.motherx = ((NBTTagDouble) mpos.get(0)).data;
                this.mothery = ((NBTTagDouble) mpos.get(1)).data;
                this.motherz = ((NBTTagDouble) mpos.get(2)).data;
            } else {
                this.motherx = this.locX;
                this.mothery = this.locY;
                this.motherz = this.locZ;
            }

            NBTTagList lpath = in.getList("Path");
            if ((lpath == null) || (lpath.size() == 0)) {
                return;
            }
            for (int count = 0; count < lpath.size(); count++) {
                NBTTagList dbl = (NBTTagList) lpath.get(count);
                this.path.add(new BeziPoint(((NBTTagDouble) dbl.get(0)).data, ((NBTTagDouble) dbl.get(1)).data, ((NBTTagDouble) dbl.get(2)).data));
            }
            if (this.next == null) {
                this.next = ((BeziPoint) this.path.get(0));
            }

            if (this.path.size() > 2) {
                getCC(this.next);
                getStep();
                setPosition(((BeziPoint) this.path.get(0)).x, ((BeziPoint) this.path.get(0)).y, ((BeziPoint) this.path.get(0)).z);
                this.lastDist = 9999999.0D;
            }
        } else {
            die();
        }
    }


    public void b(NBTTagCompound out) {
        out.setBoolean("isMother", this.motherEntity);
        if (this.motherEntity) {
            out.set("Mother", a(new double[]{this.motherx, this.mothery, this.motherz}));
            if ((this.path == null) || (this.path.isEmpty())) {
                return;
            }
            NBTTagList spath = new NBTTagList();
            for (BeziPoint bezi : this.path) {
                NBTTagList dbl = new NBTTagList();
                dbl.add(new NBTTagDouble((String) null, bezi.x));
                dbl.add(new NBTTagDouble((String) null, bezi.y));
                dbl.add(new NBTTagDouble((String) null, bezi.z));
                spath.add(dbl);
            }
            out.set("Path", spath);
        }
    }

    public boolean isAlive() {
        return true;
    }

    public int getMaxHealth() {
        return 9999999;
    }

    public void rightClick(Player user, int slot) {
        if ((this.focused == null) || (this.focused.getUniqueId() != user.getUniqueId())) {
            this.focused = user;
        }
        if (slot == 1) {
            this.controlled = (!this.controlled);
        } else if (slot == 20) {
            addPoint(user);
        } else if (slot == 21) {
            die();
        } else if (slot == 3) {
            voyage(user);
        } else if (slot == 4) {
            drawPath();
        } else if (slot == 5) {
            cleanPath();
        } else if (slot == 6) {
            toggleDemo(user);
        }
    }

    public void toggleDemo(Player user) {
        this.sendDemos = (!this.sendDemos);
        user.sendMessage(ChatColor.GOLD + "Demo mode turned " + ChatColor.AQUA + (this.sendDemos ? "on" : "off"));
    }

    public void toggleControll(Player user) {
        if ((this.focused == null) || (this.focused.getUniqueId() != user.getUniqueId())) {
            this.focused = user;
        }
        this.controlled = (!this.controlled);
        this.distance = 2.5D;
        this.lastSlot = user.getInventory().getHeldItemSlot();
    }

    public void voyage(Player user) {
        PrzlabsBlaze dragon = new PrzlabsBlaze(this.world, this.path, user);
        this.world.addEntity(dragon, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    public void addPoint(Player user) {
        if (this.path == null) {
            this.path = new ArrayList();
            this.currentCurve = new BeziPoint[1];
        }
        this.path.add(new BeziPoint(user.getLocation()));

        this.currentPoint = 1;

        if (this.next == null) {
            this.next = ((BeziPoint) this.path.get(0));
        }

        if (this.path.size() > 2) {
            getCC(this.next);
            getStep();
            setPosition(((BeziPoint) this.path.get(0)).x, ((BeziPoint) this.path.get(0)).y, ((BeziPoint) this.path.get(0)).z);
            this.lastDist = 9999999.0D;
        }
        this.lastSaved = 0L;
    }

    public void drawPath() {
        if (this.crystalPath == null) {
            this.crystalPath = new PrzlabsCrystal[this.path.size()];
        } else if ((this.crystalPath != null) && (this.crystalPath.length != 0)) {
            for (PrzlabsCrystal plc : this.crystalPath) {
                if (plc != null) {

                    plc.die();
                }
            }
            this.crystalPath = new PrzlabsCrystal[this.path.size()];
        }

        if ((this.path == null) || (this.path.isEmpty())) {
            return;
        }
        for (int count = 0; count < this.path.size(); count++) {
            PrzlabsCrystal crystal = new PrzlabsCrystal(this.world, (BeziPoint) this.path.get(count), count, this);
            if (this.world.addEntity(crystal, CreatureSpawnEvent.SpawnReason.CUSTOM)) {
                this.crystalPath[count] = crystal;
            }
        }
    }

    public void cleanPath() {
        if (this.crystalPath == null) {
            this.crystalPath = new PrzlabsCrystal[this.path.size()];
        } else if ((this.crystalPath != null) && (this.crystalPath.length != 0)) {
            for (PrzlabsCrystal plc : this.crystalPath) {
                if (plc != null) {

                    /* 463 */
                    plc.die();
                }
            }
            /* 465 */
            this.crystalPath = new PrzlabsCrystal[this.path.size()];
        }
    }


    public void setCrystal(PrzlabsCrystal crystal, int index) {
        this.crystalPath[index] = crystal;
    }

    public void die() {
        if (VoxelVoyage.entities.containsKey(getBukkitEntity().getWorld().getUID())) {
            ((TreeMap) VoxelVoyage.entities.get(getBukkitEntity().getWorld().getUID())).remove(Integer.valueOf(this.id));
        }
        if (VoxelVoyage.selected.containsValue(this)) {
            String pname = null;
            for (Map.Entry<String, net.minecraft.server.Entity> entr : VoxelVoyage.selected.entrySet()) {
                if (((net.minecraft.server.Entity) entr.getValue()).id == this.id) {
                    pname = (String) entr.getKey();
                }
            }
            VoxelVoyage.selected.remove(pname);
        }
        if ((!this.motherEntity) && (this.focusName != null)) {
            VoxelVoyage.flying.remove(this.focusName);
        }
        super.die();
    }

    public int getEntID() {
        return this.id;
    }

    public boolean damageEntity(DamageSource damagesource, int i) {
        return false;
    }

    public int getAirTicks() {
        return this.motherEntity ? 12349 : 12350;
    }

    public void b(net.minecraft.server.Entity entity, int index) {
        if ((entity instanceof EntityPlayer)) {
            EntityPlayer play = (EntityPlayer) entity;
            org.bukkit.entity.Entity bplay = play.getBukkitEntity();
            if ((bplay instanceof Player)) {
                rightClick((Player) bplay, index);
            }
        }
    }
}
