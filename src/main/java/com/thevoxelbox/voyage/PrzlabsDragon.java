/*     */ package com.thevoxelbox.voyage;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.TreeMap;
/*     */ import java.util.TreeSet;
/*     */ import net.minecraft.server.EntityEnderDragon;
/*     */ import net.minecraft.server.EntityPlayer;
/*     */ import net.minecraft.server.NBTTagCompound;
/*     */ import net.minecraft.server.NBTTagDouble;
/*     */ import net.minecraft.server.NBTTagList;
/*     */ import net.minecraft.server.NPC;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.Location;
/*     */ import org.bukkit.craftbukkit.entity.CraftPlayer;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
/*     */ import org.bukkit.inventory.PlayerInventory;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PrzlabsDragon
/*     */   extends EntityEnderDragon
/*     */   implements PrzlabsEntity, NPC
/*     */ {
/*  31 */   private ArrayList<BeziPoint> path = new ArrayList();
/*  32 */   private BeziPoint[] currentCurve = new BeziPoint[0];
/*  33 */   private int currentPoint = 1;
/*  34 */   private double currT = 1.0D;
/*  35 */   private double stepT = 0.1D;
/*     */   private Player focused;
/*     */   private String focusName;
/*     */   private BeziPoint next;
/*  39 */   private double lastDist = 9999999.0D;
/*  40 */   private boolean motherEntity = false;
/*  41 */   private boolean pathEnd = false;
/*  42 */   private boolean controlled = false;
/*     */   private double motherx;
/*     */   private double mothery;
/*     */   private double motherz;
/*     */   private PrzlabsCrystal[] crystalPath;
/*  47 */   private double distance = 12.0D;
/*  48 */   private int lastSlot = 0;
/*  49 */   private boolean demo = false;
/*  50 */   private boolean sendDemos = false;
/*     */   
/*     */   public PrzlabsDragon(net.minecraft.server.World world) {
/*  53 */     super(world);
/*     */   }
/*     */   
/*     */   public PrzlabsDragon(net.minecraft.server.World world, boolean mother) {
/*  57 */     super(world);
/*  58 */     this.motherEntity = mother;
/*  59 */     if (VoxelVoyage.entities.containsKey(getBukkitEntity().getWorld().getUID())) {
/*  60 */       ((TreeMap)VoxelVoyage.entities.get(getBukkitEntity().getWorld().getUID())).put(Integer.valueOf(this.id), this);
/*     */     } else {
/*  62 */       VoxelVoyage.entities.put(getBukkitEntity().getWorld().getUID(), new TreeMap());
/*  63 */       ((TreeMap)VoxelVoyage.entities.get(getBukkitEntity().getWorld().getUID())).put(Integer.valueOf(this.id), this);
/*     */     }
/*     */   }
/*     */   
/*     */   public PrzlabsDragon(net.minecraft.server.World world, boolean mother, Location idle) {
/*  68 */     super(world);
/*  69 */     this.motherEntity = mother;
/*  70 */     setPositionRotation(idle.getX(), idle.getY(), idle.getZ(), idle.getYaw() + 180.0F, idle.getPitch());
/*  71 */     this.motherx = idle.getX();
/*  72 */     this.mothery = idle.getY();
/*  73 */     this.motherz = idle.getZ();
/*  74 */     if (VoxelVoyage.entities.containsKey(getBukkitEntity().getWorld().getUID())) {
/*  75 */       ((TreeMap)VoxelVoyage.entities.get(getBukkitEntity().getWorld().getUID())).put(Integer.valueOf(this.id), this);
/*     */     } else {
/*  77 */       VoxelVoyage.entities.put(getBukkitEntity().getWorld().getUID(), new TreeMap());
/*  78 */       ((TreeMap)VoxelVoyage.entities.get(getBukkitEntity().getWorld().getUID())).put(Integer.valueOf(this.id), this);
/*     */     }
/*     */   }
/*     */   
/*     */   public PrzlabsDragon(net.minecraft.server.World world, ArrayList<BeziPoint> flightPath, Player pilot) {
/*  83 */     super(world);
/*  84 */     if ((flightPath == null) || (flightPath.size() < 2))
/*     */     {
/*  86 */       die();
/*  87 */       return;
/*     */     }
/*  89 */     if (VoxelVoyage.flying.contains(pilot.getName())) {
/*  90 */       die();
/*  91 */       return;
/*     */     }
/*  93 */     this.focused = pilot;
/*  94 */     this.focusName = this.focused.getName();
/*  95 */     VoxelVoyage.flying.add(this.focusName);
/*  96 */     this.path = flightPath;
/*  97 */     setPosition(((BeziPoint)this.path.get(0)).x, ((BeziPoint)this.path.get(0)).y, ((BeziPoint)this.path.get(0)).z);
/*  98 */     this.currentCurve = new BeziPoint[] { (BeziPoint)this.path.get(0), (BeziPoint)this.path.get(1) };
/*  99 */     getCC((BeziPoint)this.path.get(0));
/* 100 */     getStep();
/* 101 */     this.next = BeziCurve.getBezi(0.003D, this.currentCurve);
/* 102 */     this.yaw = getCorrectYaw(this.next.x, this.next.z);
/*     */   }
/*     */   
/*     */   public PrzlabsDragon(net.minecraft.server.World world, ArrayList<BeziPoint> flightPath, boolean demomode) {
/* 106 */     super(world);
/* 107 */     if ((flightPath == null) || (flightPath.size() < 2))
/*     */     {
/* 109 */       die();
/* 110 */       return;
/*     */     }
/* 112 */     this.demo = demomode;
/* 113 */     this.path = flightPath;
/* 114 */     setPosition(((BeziPoint)this.path.get(0)).x, ((BeziPoint)this.path.get(0)).y, ((BeziPoint)this.path.get(0)).z);
/* 115 */     this.currentCurve = new BeziPoint[] { (BeziPoint)this.path.get(0), (BeziPoint)this.path.get(1) };
/* 116 */     getCC((BeziPoint)this.path.get(0));
/* 117 */     getStep();
/* 118 */     this.next = BeziCurve.getBezi(0.003D, this.currentCurve);
/* 119 */     this.yaw = getCorrectYaw(this.next.x, this.next.z);
/*     */   }
/*     */   
/*     */   public org.bukkit.entity.Entity getBukkitEntity()
/*     */   {
/* 124 */     if (this.bukkitEntity == null) {
/* 125 */       this.bukkitEntity = new PrzlabsLivingEntity(this.world.getServer(), this);
/*     */     }
/* 127 */     return this.bukkitEntity;
/*     */   }
/*     */   
/*     */   private float getCorrectYaw(double targetx, double targetz) {
/* 131 */     if (this.locZ > targetz)
/* 132 */       return (float)-Math.toDegrees(Math.atan((this.locX - targetx) / (this.locZ - targetz)));
/* 133 */     if (this.locZ < targetz) {
/* 134 */       return (float)-Math.toDegrees(Math.atan((this.locX - targetx) / (this.locZ - targetz))) + 180.0F;
/*     */     }
/* 136 */     return this.yaw;
/*     */   }
/*     */   
/*     */   private void getStep()
/*     */   {
/* 141 */     if (this.currentCurve.length > 2) {
/* 142 */       double cdist = 0.0D;
/* 143 */       BeziPoint lastbezi = this.currentCurve[0];
/*     */       
/* 145 */       for (double tt = 0.0D; tt < 1.0D; tt += 0.01D) {
/* 146 */         BeziPoint newbezi = BeziCurve.getBezi(tt, this.currentCurve);
/* 147 */         cdist += Math.pow(Math.pow(newbezi.x - lastbezi.x, 2.0D) + Math.pow(newbezi.y - lastbezi.y, 2.0D) + Math.pow(newbezi.z - lastbezi.z, 2.0D), 0.5D);
/* 148 */         lastbezi = newbezi;
/*     */       }
/* 150 */       this.stepT = (0.75D / cdist);
/* 151 */       this.currT = this.stepT;
/*     */     }
/*     */   }
/*     */   
/*     */   private boolean switchPoint() {
/* 156 */     double currDist = Math.pow(Math.pow(this.locX - this.currentCurve[1].x, 2.0D) + Math.pow(this.locY - this.currentCurve[1].y, 2.0D) + Math.pow(this.locZ - this.currentCurve[1].z, 2.0D), 0.5D);
/* 157 */     if (currDist <= this.lastDist) {
/* 158 */       this.lastDist = currDist;
/* 159 */       return true;
/*     */     }
/* 161 */     this.lastDist = 9999999.0D;
/* 162 */     return false;
/*     */   }
/*     */   
/*     */   private void getCC(BeziPoint currentbp)
/*     */   {
/* 167 */     if (this.currentPoint + 1 < this.path.size()) {
/* 168 */       this.currentCurve = new BeziPoint[] { currentbp, (BeziPoint)this.path.get(this.currentPoint), (BeziPoint)this.path.get(this.currentPoint + 1) };
/*     */     } else
/* 170 */       this.pathEnd = true;
/*     */   }
/*     */   
/* 173 */   private long lastDemo = 0L;
/*     */   long lastSaved;
/*     */   
/*     */   public void d() {
/* 177 */     if ((this.focused != null) && (!this.focused.isOnline())) {
/* 178 */       this.focused = null;
/*     */     }
/* 180 */     if ((this.motherEntity) && (!this.demo)) {
/* 181 */       if ((this.sendDemos) && 
/* 182 */         (System.currentTimeMillis() - this.lastDemo > 4000L)) {
/* 183 */         this.lastDemo = System.currentTimeMillis();
/* 184 */         PrzlabsDragon dragon = new PrzlabsDragon(this.world, this.path, true);
/* 185 */         this.world.addEntity(dragon, CreatureSpawnEvent.SpawnReason.CUSTOM);
/*     */       }
/*     */       
/* 188 */       if (this.controlled) {
/* 189 */         changeDistance(this.focused);
/* 190 */         Location player_loc = this.focused.getLocation();
/* 191 */         double rot_x = (player_loc.getYaw() + 90.0F) % 360.0F;
/* 192 */         double rot_y = player_loc.getPitch() * -1.0F;
/* 193 */         double rot_ycos = Math.cos(Math.toRadians(rot_y));
/* 194 */         double rot_ysin = Math.sin(Math.toRadians(rot_y));
/* 195 */         double rot_xcos = Math.cos(Math.toRadians(rot_x));
/* 196 */         double rot_xsin = Math.sin(Math.toRadians(rot_x));
/*     */         
/* 198 */         double h_length = this.distance * rot_ycos;
/* 199 */         double y_offset = this.distance * rot_ysin;
/* 200 */         double x_offset = h_length * rot_xcos;
/* 201 */         double z_offset = h_length * rot_xsin;
/*     */         
/* 203 */         double target_x = x_offset + player_loc.getX();
/* 204 */         double target_y = y_offset + player_loc.getY() + 1.65D;
/* 205 */         double target_z = z_offset + player_loc.getZ();
/* 206 */         setPosition(target_x, target_y, target_z);
/* 207 */         this.yaw = getCorrectYaw(player_loc.getX(), player_loc.getZ());
/* 208 */         this.motherx = target_x;
/* 209 */         this.mothery = target_y;
/* 210 */         this.motherz = target_z;
/* 211 */         this.lastSaved = 0L;
/*     */       } else {
/* 213 */         setPosition(this.motherx, this.mothery, this.motherz);
/*     */       }
/*     */     }
/* 216 */     else if (((this.focused != null) && (this.focused.isOnline())) || (this.demo)) {
/* 217 */       if ((this.passenger == null) && (!this.demo)) {
/* 218 */         ((CraftPlayer)this.focused).getHandle().setPassengerOf(this);
/*     */       }
/* 220 */       if ((this.path != null) && (!this.path.isEmpty()) && (this.path.size() > 1)) {
/* 221 */         setPosition(this.next.x, this.next.y, this.next.z);
/* 222 */         if ((switchPoint()) || (this.pathEnd)) {
/* 223 */           this.next = BeziCurve.getBezi(this.currT, this.currentCurve);
/* 224 */           this.currT += this.stepT;
/* 225 */           if (this.currT > 1.0D) {
/* 226 */             if (this.focusName != null) {
/* 227 */               VoxelVoyage.flying.remove(this.focusName);
/*     */             }
/*     */             
/* 230 */             die();
/*     */           }
/*     */         } else {
/* 233 */           getCC(this.next);
/* 234 */           if (!this.pathEnd) {
/* 235 */             getStep();
/* 236 */             this.currentPoint += 1;
/* 237 */             if (this.currentPoint == this.path.size()) {
/* 238 */               this.currentPoint = 1;
/*     */             }
/*     */           } else {
/* 241 */             this.next = BeziCurve.getBezi(this.currT, this.currentCurve);
/* 242 */             this.currT += this.stepT;
/*     */           }
/*     */         }
/* 245 */         this.yaw = getCorrectYaw(this.next.x, this.next.z);
/*     */       } else {
/* 247 */         if (this.focusName != null) {
/* 248 */           VoxelVoyage.flying.remove(this.focusName);
/*     */         }
/*     */         
/* 251 */         die();
/*     */       }
/*     */     } else {
/* 254 */       if (this.focusName != null) {
/* 255 */         VoxelVoyage.flying.remove(this.focusName);
/*     */       }
/*     */       
/* 258 */       die();
/*     */     }
/*     */   }
/*     */   
/*     */   private void changeDistance(Player user)
/*     */   {
/* 264 */     int currentSlot = user.getInventory().getHeldItemSlot();
/* 265 */     if (this.lastSlot == 0) {
/* 266 */       if ((currentSlot != 0) && (currentSlot > 5)) {
/* 267 */         this.distance += 1.0D;
/* 268 */       } else if ((currentSlot != 0) && (currentSlot < 6)) {
/* 269 */         this.distance -= 1.0D;
/*     */       }
/* 271 */     } else if (this.lastSlot == 8) {
/* 272 */       if ((currentSlot != 8) && (currentSlot < 5)) {
/* 273 */         this.distance -= 1.0D;
/* 274 */       } else if ((currentSlot != 8) && (currentSlot > 4)) {
/* 275 */         this.distance += 1.0D;
/*     */       }
/*     */     }
/* 278 */     else if (currentSlot < this.lastSlot) {
/* 279 */       this.distance += 1.0D;
/* 280 */     } else if (currentSlot > this.lastSlot) {
/* 281 */       this.distance -= 1.0D;
/*     */     }
/*     */     
/* 284 */     this.lastSlot = currentSlot;
/*     */   }
/*     */   
/*     */ 
/*     */   public void a(NBTTagCompound in)
/*     */   {
/* 290 */     this.motherEntity = in.getBoolean("isMother");
/* 291 */     if (this.motherEntity) {
/* 292 */       if (VoxelVoyage.entities.containsKey(getBukkitEntity().getWorld().getUID())) {
/* 293 */         ((TreeMap)VoxelVoyage.entities.get(getBukkitEntity().getWorld().getUID())).put(Integer.valueOf(this.id), this);
/*     */       } else {
/* 295 */         VoxelVoyage.entities.put(getBukkitEntity().getWorld().getUID(), new TreeMap());
/* 296 */         ((TreeMap)VoxelVoyage.entities.get(getBukkitEntity().getWorld().getUID())).put(Integer.valueOf(this.id), this);
/*     */       }
/* 298 */       NBTTagList mpos = in.getList("Mother");
/*     */       
/* 300 */       if ((mpos != null) && (mpos.size() != 0)) {
/* 301 */         this.motherx = ((NBTTagDouble)mpos.get(0)).data;
/* 302 */         this.mothery = ((NBTTagDouble)mpos.get(1)).data;
/* 303 */         this.motherz = ((NBTTagDouble)mpos.get(2)).data;
/*     */       } else {
/* 305 */         this.motherx = this.locX;
/* 306 */         this.mothery = this.locY;
/* 307 */         this.motherz = this.locZ;
/*     */       }
/*     */       
/* 310 */       NBTTagList lpath = in.getList("Path");
/* 311 */       if ((lpath == null) || (lpath.size() == 0)) {
/* 312 */         return;
/*     */       }
/* 314 */       for (int count = 0; count < lpath.size(); count++) {
/* 315 */         NBTTagList dbl = (NBTTagList)lpath.get(count);
/* 316 */         this.path.add(new BeziPoint(((NBTTagDouble)dbl.get(0)).data, ((NBTTagDouble)dbl.get(1)).data, ((NBTTagDouble)dbl.get(2)).data));
/*     */       }
/* 318 */       if (this.next == null) {
/* 319 */         this.next = ((BeziPoint)this.path.get(0));
/*     */       }
/*     */       
/* 322 */       if (this.path.size() > 2) {
/* 323 */         getCC(this.next);
/* 324 */         getStep();
/* 325 */         setPosition(((BeziPoint)this.path.get(0)).x, ((BeziPoint)this.path.get(0)).y, ((BeziPoint)this.path.get(0)).z);
/* 326 */         this.lastDist = 9999999.0D;
/*     */       }
/*     */     }
/*     */     else {
/* 330 */       die();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void b(NBTTagCompound out)
/*     */   {
/* 338 */     out.setBoolean("isMother", this.motherEntity);
/* 339 */     if (this.motherEntity) {
/* 340 */       out.set("Mother", a(new double[] { this.motherx, this.mothery, this.motherz }));
/* 341 */       if ((this.path == null) || (this.path.isEmpty())) {
/* 342 */         return;
/*     */       }
/* 344 */       NBTTagList spath = new NBTTagList();
/* 345 */       for (BeziPoint bezi : this.path) {
/* 346 */         NBTTagList dbl = new NBTTagList();
/* 347 */         dbl.add(new NBTTagDouble((String)null, bezi.x));
/* 348 */         dbl.add(new NBTTagDouble((String)null, bezi.y));
/* 349 */         dbl.add(new NBTTagDouble((String)null, bezi.z));
/* 350 */         spath.add(dbl);
/*     */       }
/* 352 */       out.set("Path", spath);
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isAlive()
/*     */   {
/* 358 */     return true;
/*     */   }
/*     */   
/*     */   public int getMaxHealth()
/*     */   {
/* 363 */     return 9999999;
/*     */   }
/*     */   
/*     */   public void rightClick(Player user, int slot)
/*     */   {
/* 368 */     if ((this.focused == null) || (this.focused.getUniqueId() != user.getUniqueId())) {
/* 369 */       this.focused = user;
/*     */     }
/* 371 */     if (slot == 1) {
/* 372 */       this.controlled = (!this.controlled);
/* 373 */     } else if (slot == 20) {
/* 374 */       addPoint(user);
/* 375 */     } else if (slot == 21) {
/* 376 */       die();
/* 377 */     } else if (slot == 3) {
/* 378 */       voyage(user);
/* 379 */     } else if (slot == 4) {
/* 380 */       drawPath();
/* 381 */     } else if (slot == 5) {
/* 382 */       cleanPath();
/* 383 */     } else if (slot == 6) {
/* 384 */       toggleDemo(user);
/*     */     }
/*     */   }
/*     */   
/*     */   public void toggleDemo(Player user) {
/* 389 */     this.sendDemos = (!this.sendDemos);
/* 390 */     user.sendMessage(ChatColor.GOLD + "Demo mode turned " + ChatColor.AQUA + (this.sendDemos ? "on" : "off"));
/*     */   }
/*     */   
/*     */   public void toggleControll(Player user) {
/* 394 */     if ((this.focused == null) || (this.focused.getUniqueId() != user.getUniqueId())) {
/* 395 */       this.focused = user;
/*     */     }
/* 397 */     this.controlled = (!this.controlled);
/* 398 */     this.distance = 12.0D;
/*     */   }
/*     */   
/*     */   public void voyage(Player user) {
/* 402 */     PrzlabsDragon dragon = new PrzlabsDragon(this.world, this.path, user);
/* 403 */     this.world.addEntity(dragon, CreatureSpawnEvent.SpawnReason.CUSTOM);
/*     */   }
/*     */   
/*     */   public void addPoint(Player user) {
/* 407 */     if (this.path == null) {
/* 408 */       this.path = new ArrayList();
/* 409 */       this.currentCurve = new BeziPoint[1];
/*     */     }
/* 411 */     this.path.add(new BeziPoint(user.getLocation()));
/*     */     
/* 413 */     this.currentPoint = 1;
/*     */     
/* 415 */     if (this.next == null) {
/* 416 */       this.next = ((BeziPoint)this.path.get(0));
/*     */     }
/*     */     
/* 419 */     if (this.path.size() > 2) {
/* 420 */       getCC(this.next);
/* 421 */       getStep();
/* 422 */       setPosition(((BeziPoint)this.path.get(0)).x, ((BeziPoint)this.path.get(0)).y, ((BeziPoint)this.path.get(0)).z);
/* 423 */       this.lastDist = 9999999.0D;
/*     */     }
/* 425 */     this.lastSaved = 0L;
/*     */   }
/*     */   
/*     */   public void drawPath() {
/* 429 */     if (this.crystalPath == null) {
/* 430 */       this.crystalPath = new PrzlabsCrystal[this.path.size()];
/*     */     }
/* 432 */     else if ((this.crystalPath != null) && (this.crystalPath.length != 0)) {
/* 433 */       for (PrzlabsCrystal plc : this.crystalPath) {
/* 434 */         if (plc != null)
/*     */         {
/*     */ 
/* 437 */           plc.die(); }
/*     */       }
/* 439 */       this.crystalPath = new PrzlabsCrystal[this.path.size()];
/*     */     }
/*     */     
/* 442 */     if ((this.path == null) || (this.path.isEmpty())) {
/* 443 */       return;
/*     */     }
/* 445 */     for (int count = 0; count < this.path.size(); count++) {
/* 446 */       PrzlabsCrystal crystal = new PrzlabsCrystal(this.world, (BeziPoint)this.path.get(count), count, this);
/* 447 */       if (this.world.addEntity(crystal, CreatureSpawnEvent.SpawnReason.CUSTOM)) {
/* 448 */         this.crystalPath[count] = crystal;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void cleanPath() {
/* 454 */     if (this.crystalPath == null) {
/* 455 */       this.crystalPath = new PrzlabsCrystal[this.path.size()];
/*     */     }
/* 457 */     else if ((this.crystalPath != null) && (this.crystalPath.length != 0)) {
/* 458 */       for (PrzlabsCrystal plc : this.crystalPath) {
/* 459 */         if (plc != null)
/*     */         {
/*     */ 
/* 462 */           plc.die(); }
/*     */       }
/* 464 */       this.crystalPath = new PrzlabsCrystal[this.path.size()];
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void setCrystal(PrzlabsCrystal crystal, int index)
/*     */   {
/* 471 */     this.crystalPath[index] = crystal;
/*     */   }
/*     */   
/*     */   public void die()
/*     */   {
/* 476 */     if (VoxelVoyage.entities.containsKey(getBukkitEntity().getWorld().getUID())) {
/* 477 */       ((TreeMap)VoxelVoyage.entities.get(getBukkitEntity().getWorld().getUID())).remove(Integer.valueOf(this.id));
/*     */     }
/* 479 */     if (VoxelVoyage.selected.containsValue(this)) {
/* 480 */       String pname = null;
/* 481 */       for (Map.Entry<String, net.minecraft.server.Entity> entr : VoxelVoyage.selected.entrySet()) {
/* 482 */         if (((net.minecraft.server.Entity)entr.getValue()).id == this.id) {
/* 483 */           pname = (String)entr.getKey();
/*     */         }
/*     */       }
/* 486 */       VoxelVoyage.selected.remove(pname);
/*     */     }
/* 488 */     if ((!this.motherEntity) && (this.focusName != null)) {
/* 489 */       VoxelVoyage.flying.remove(this.focusName);
/*     */     }
/* 491 */     super.die();
/*     */   }
/*     */   
/*     */   public int getEntID()
/*     */   {
/* 496 */     return this.id;
/*     */   }
/*     */   
/*     */   public int getAirTicks()
/*     */   {
/* 501 */     return this.motherEntity ? 12347 : 12348;
/*     */   }
/*     */   
/*     */   public void b(net.minecraft.server.Entity entity, int index)
/*     */   {
/* 506 */     if ((entity instanceof EntityPlayer)) {
/* 507 */       EntityPlayer play = (EntityPlayer)entity;
/* 508 */       org.bukkit.entity.Entity bplay = play.getBukkitEntity();
/* 509 */       if ((bplay instanceof Player)) {
/* 510 */         rightClick((Player)bplay, index);
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\intellij\VoxelVoyage\VoxelVoyage.jar!\com\thevoxelbox\voyage\PrzlabsDragon.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */