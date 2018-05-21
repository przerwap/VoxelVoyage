/*     */ package com.thevoxelbox.voyage;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileReader;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Properties;
/*     */ import java.util.TreeMap;
/*     */ import java.util.TreeSet;
/*     */ import java.util.UUID;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import net.minecraft.server.Entity;
/*     */ import net.minecraft.server.EntityTypes;
/*     */ import net.minecraft.server.WorldServer;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.Location;
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.command.Command;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.craftbukkit.CraftWorld;
/*     */ import org.bukkit.craftbukkit.entity.CraftPlayer;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.event.Event.Priority;
/*     */ import org.bukkit.event.Event.Type;
/*     */ import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
/*     */ import org.bukkit.plugin.PluginDescriptionFile;
/*     */ import org.bukkit.plugin.PluginManager;
/*     */ import org.bukkit.plugin.java.JavaPlugin;
/*     */ 
/*     */ public class VoxelVoyage
/*     */   extends JavaPlugin
/*     */ {
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/*  44 */       EntityTypes et = new EntityTypes();
/*     */       
/*  46 */       Method addEntity = EntityTypes.class.getDeclaredMethod("a", new Class[] { Class.class, String.class, Integer.TYPE });
/*     */       
/*  48 */       addEntity.setAccessible(true);
/*     */       
/*  50 */       addEntity.invoke(et, new Object[] { PrzlabsDragon.class, "PrzlabsDragon", Integer.valueOf(63) });
/*  51 */       System.out.println("[VoxelVoyage] PrzlabsDragon entity registered!");
/*     */       
/*  53 */       addEntity.invoke(et, new Object[] { PrzlabsCrystal.class, "PrzlabsCrystal", Integer.valueOf(200) });
/*  54 */       System.out.println("[VoxelVoyage] PrzlabsCrystal entity registered!");
/*     */       
/*  56 */       addEntity.invoke(et, new Object[] { PrzlabsBlaze.class, "PrzlabsBlaze", Integer.valueOf(61) });
/*  57 */       System.out.println("[VoxelVoyage] PrzlabsBlaze entity registered!");
/*     */     }
/*     */     catch (NoSuchMethodException ex) {
/*  60 */       System.out.println("[VoxelVoyage] PrzlabsDragon entity failed to register!");
/*  61 */       Logger.getLogger(VoxelVoyage.class.getName()).log(Level.SEVERE, null, ex);
/*     */     } catch (SecurityException ex) {
/*  63 */       System.out.println("[VoxelVoyage] PrzlabsDragon entity failed to register!");
/*  64 */       Logger.getLogger(VoxelVoyage.class.getName()).log(Level.SEVERE, null, ex);
/*     */     } catch (IllegalAccessException ex) {
/*  66 */       System.out.println("[VoxelVoyage] PrzlabsDragon entity failed to register!");
/*  67 */       Logger.getLogger(VoxelVoyage.class.getName()).log(Level.SEVERE, null, ex);
/*     */     } catch (IllegalArgumentException ex) {
/*  69 */       System.out.println("[VoxelVoyage] PrzlabsDragon entity failed to register!");
/*  70 */       Logger.getLogger(VoxelVoyage.class.getName()).log(Level.SEVERE, null, ex);
/*     */     } catch (InvocationTargetException ex) {
/*  72 */       System.out.println("[VoxelVoyage] PrzlabsDragon entity failed to register!");
/*  73 */       Logger.getLogger(VoxelVoyage.class.getName()).log(Level.SEVERE, null, ex);
/*     */     } }
/*     */   
/*  76 */   protected static final Logger log = Logger.getLogger("Minecraft");
/*  77 */   public static boolean SPAWN_ENTITIES = true;
/*  78 */   private VPlayer plistener = new VPlayer();
/*  79 */   private VEntity elistener = new VEntity();
/*  80 */   public static TreeMap<UUID, TreeMap<Integer, Entity>> entities = new TreeMap();
/*  81 */   public static TreeMap<String, Entity> selected = new TreeMap();
/*  82 */   public static TreeSet<String> flying = new TreeSet();
/*  83 */   public static TreeSet<String> permitted = new TreeSet();
/*     */   public static String password;
/*  85 */   public static int voyageItem = 371;
/*     */   
/*     */   public static Entity getEntity(Player p) {
/*  88 */     if (!entities.containsKey(p.getWorld().getUID())) {
/*  89 */       p.sendMessage(ChatColor.RED + "No Voyage entity found.");
/*  90 */       return null;
/*     */     }
/*  92 */     Entity closest = null;
/*  93 */     double range = 9.9999999E7D;
/*     */     
/*  95 */     double bx = p.getLocation().getX();
/*  96 */     double by = p.getLocation().getY();
/*  97 */     double bz = p.getLocation().getZ();
/*     */     
/*  99 */     for (Entity ent : ((TreeMap)entities.get(p.getWorld().getUID())).values()) {
/* 100 */       switch (ent.getAirTicks()) {
/*     */       case 12347: 
/*     */       case 12348: 
/*     */       case 12349: 
/*     */       case 12350: 
/* 105 */         double erange = Math.pow(bx - ent.locX, 2.0D) + Math.pow(by - ent.locY, 2.0D) + Math.pow(bz - ent.locZ, 2.0D);
/*     */         
/* 107 */         if (!ent.dead)
/*     */         {
/*     */ 
/*     */ 
/* 111 */           if (erange < range) {
/* 112 */             range = erange;
/* 113 */             closest = ent;
/*     */           }
/*     */         }
/*     */         
/*     */         break;
/*     */       }
/*     */       
/*     */     }
/*     */     
/* 122 */     if ((closest != null) && (Math.pow(range, 0.5D) <= 40.0D)) {
/* 123 */       return closest;
/*     */     }
/* 125 */     p.sendMessage(ChatColor.RED + "No Voyage entity found.");
/* 126 */     return null;
/*     */   }
/*     */   
/*     */   public static boolean isPermitted(Player user)
/*     */   {
/* 131 */     return user.isOp() ? true : permitted.contains(user.getName());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void onEnable()
/*     */   {
/* 141 */     for (Iterator i$ = Bukkit.getWorlds().iterator(); i$.hasNext();) { wrld = (World)i$.next();
/* 142 */       CraftWorld cw = (CraftWorld)wrld;
/* 143 */       for (Object o : cw.getHandle().entityList)
/*     */       {
/* 145 */         Entity ent = (Entity)o;
/* 146 */         switch (ent.getAirTicks())
/*     */         {
/*     */         case 12346: 
/* 149 */           ent.die();
/* 150 */           break;
/*     */         
/*     */         case 12347: 
/*     */         case 12348: 
/*     */         case 12349: 
/*     */         case 12350: 
/* 156 */           if (entities.containsKey(wrld.getUID())) {
/* 157 */             ((TreeMap)entities.get(wrld.getUID())).put(Integer.valueOf(ent.id), ent);
/*     */           } else {
/* 159 */             entities.put(wrld.getUID(), new TreeMap());
/* 160 */             ((TreeMap)entities.get(wrld.getUID())).put(Integer.valueOf(ent.id), ent);
/*     */           }
/*     */           
/*     */           break;
/*     */         }
/*     */         
/*     */       }
/*     */     }
/*     */     
/*     */     World wrld;
/*     */     
/* 171 */     loadProps();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 178 */     Bukkit.getPluginManager().registerEvent(Event.Type.PLAYER_INTERACT_ENTITY, this.plistener, Event.Priority.Normal, this);
/* 179 */     Bukkit.getPluginManager().registerEvent(Event.Type.PLAYER_INTERACT, this.plistener, Event.Priority.Normal, this);
/* 180 */     Bukkit.getPluginManager().registerEvent(Event.Type.CREATURE_SPAWN, this.elistener, Event.Priority.Lowest, this);
/*     */     
/* 182 */     PluginDescriptionFile pdfFile = getDescription();
/* 183 */     log.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled! Let's fly.");
/*     */   }
/*     */   
/*     */   private void loadProps() {
/* 187 */     File f = new File("plugins/VoxelVoyage/voyage.properties");
/* 188 */     if (!f.exists()) {
/* 189 */       f.getParentFile().mkdirs();
/*     */       try {
/* 191 */         f.createNewFile();
/* 192 */         saveProps();
/*     */       } catch (IOException ex) {
/* 194 */         Logger.getLogger(VoxelVoyage.class.getName()).log(Level.SEVERE, null, ex);
/*     */       }
/*     */     }
/* 197 */     Properties prop = new Properties();
/*     */     try {
/* 199 */       prop.load(new FileReader(f));
/*     */     } catch (IOException ex) {
/* 201 */       Logger.getLogger(VoxelVoyage.class.getName()).log(Level.SEVERE, null, ex);
/*     */     }
/* 203 */     password = prop.getProperty("Password", null);
/* 204 */     if ((password != null) && (password.equalsIgnoreCase("null"))) {
/* 205 */       password = null;
/*     */     }
/* 207 */     voyageItem = Integer.parseInt(prop.getProperty("VoyageItem", "371"));
/*     */   }
/*     */   
/*     */   private void saveProps() {
/* 211 */     File f = new File("plugins/VoxelVoyage/voyage.properties");
/* 212 */     if (!f.exists()) {
/* 213 */       f.getParentFile().mkdirs();
/*     */       try {
/* 215 */         f.createNewFile();
/*     */       } catch (IOException ex) {
/* 217 */         Logger.getLogger(VoxelVoyage.class.getName()).log(Level.SEVERE, null, ex);
/*     */       }
/*     */     }
/* 220 */     Properties prop = new Properties();
/* 221 */     prop.setProperty("Password", password == null ? "null" : password);
/* 222 */     prop.setProperty("VoyageItem", String.valueOf(voyageItem));
/*     */     try {
/* 224 */       prop.store(new PrintWriter(f), null);
/*     */     } catch (IOException ex) {
/* 226 */       Logger.getLogger(VoxelVoyage.class.getName()).log(Level.SEVERE, null, ex);
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
/*     */   {
/* 232 */     Player p = null;
/*     */     
/* 234 */     if ((sender instanceof Player)) {
/* 235 */       p = (Player)sender;
/*     */     } else {
/* 237 */       sender.sendMessage("Only players permitted!");
/*     */     }
/*     */     
/* 240 */     if (command.getName().equals("voyage")) {
/* 241 */       if ((args == null) || (args.length == 0)) {
/* 242 */         p.sendMessage(ChatColor.GREEN + "The available sub-commands are: /voyage [help] [?] [create] [ctrl] [select] [add] [editPath] [cleanpath] [demo] [kill] [createblaze]");
/* 243 */         return true;
/*     */       }
/* 245 */       if ((args[0].equalsIgnoreCase("help")) || (args[0].equals("?"))) {
/* 246 */         p.sendMessage(ChatColor.BLUE + "/voyage create - Creates a new Voyaging VoxelDragon.");
/* 247 */         p.sendMessage(ChatColor.AQUA + "/voyage createblaze - Creates a new Voyaging VoxelBlaze.");
/* 248 */         p.sendMessage(ChatColor.BLUE + "/voyage ctrl - Toggles the controll over a Voyager.");
/* 249 */         p.sendMessage(ChatColor.AQUA + "/voyage select - Selects a nearby Voyager.");
/* 250 */         p.sendMessage(ChatColor.BLUE + "/voyage add - Adds a new point to your selected Voyager's movement path.");
/* 251 */         p.sendMessage(ChatColor.AQUA + "/voyage editPath - This command allows the path for the selected Voyager to become visible as Crystals.");
/* 252 */         p.sendMessage(ChatColor.BLUE + "/voyage cleanpath - Cleans up the selected Voyager's Crystal path by removing the Crystals.");
/* 253 */         p.sendMessage(ChatColor.AQUA + "/voyage demo - Toggles the selected Voyager's demo mode.");
/* 254 */         p.sendMessage(ChatColor.BLUE + "/voyage kill - Destroys the selected Voyager and its associated waypoints.");
/* 255 */         p.sendMessage(ChatColor.AQUA + "/voyage password [pass] - Gives you access to Create Voyagers as a non-op user, or changes the password if you are OP.");
/* 256 */         return true; }
/* 257 */       if (args[0].equalsIgnoreCase("password")) {
/* 258 */         if (args.length < 2) {
/* 259 */           p.sendMessage(ChatColor.RED + "Invalid number of parameters!");
/* 260 */           return true;
/*     */         }
/* 262 */         if (p.isOp()) {
/* 263 */           password = args[1];
/* 264 */           p.sendMessage(ChatColor.AQUA + "The password has been set to: " + ChatColor.GREEN + password);
/* 265 */           permitted.clear();
/* 266 */           saveProps();
/* 267 */           return true;
/*     */         }
/* 269 */         if (password == null) {
/* 270 */           p.sendMessage("A password is not set.");
/* 271 */           return true;
/*     */         }
/* 273 */         if (args[1].equals(password)) {
/* 274 */           permitted.add(p.getName());
/* 275 */           p.sendMessage(ChatColor.GREEN + "Password accepted!");
/*     */         } else {
/* 277 */           p.sendMessage(ChatColor.DARK_GREEN + "Invalid password!");
/*     */         }
/* 279 */         return true;
/*     */       }
/*     */       
/*     */ 
/* 283 */       if (isPermitted(p)) {
/* 284 */         if (args[0].equalsIgnoreCase("create")) {
/* 285 */           PrzlabsDragon dragon = new PrzlabsDragon(((CraftWorld)p.getWorld()).getHandle(), true, p.getLocation());
/* 286 */           if (((CraftWorld)p.getWorld()).getHandle().addEntity(dragon, CreatureSpawnEvent.SpawnReason.CUSTOM)) {
/* 287 */             p.sendMessage(ChatColor.GREEN + "Done!");
/*     */           } else {
/* 289 */             p.sendMessage(ChatColor.RED + "Failure :(");
/*     */           }
/* 291 */           return true; }
/* 292 */         if (args[0].equalsIgnoreCase("createblaze")) {
/* 293 */           PrzlabsBlaze dragon = new PrzlabsBlaze(((CraftWorld)p.getWorld()).getHandle(), true, p.getLocation());
/* 294 */           if (((CraftWorld)p.getWorld()).getHandle().addEntity(dragon, CreatureSpawnEvent.SpawnReason.CUSTOM)) {
/* 295 */             p.sendMessage(ChatColor.GREEN + "Done!");
/*     */           } else {
/* 297 */             p.sendMessage(ChatColor.RED + "Failure :(");
/*     */           }
/* 299 */           return true; }
/* 300 */         if (args[0].equalsIgnoreCase("crystal")) {
/* 301 */           PrzlabsCrystal crystal = new PrzlabsCrystal(((CraftWorld)p.getWorld()).getHandle(), true);
/* 302 */           crystal.setPosition(p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ());
/* 303 */           if (((CraftWorld)p.getWorld()).getHandle().addEntity(crystal, CreatureSpawnEvent.SpawnReason.CUSTOM)) {
/* 304 */             p.sendMessage(ChatColor.GREEN + "Done!");
/*     */           } else {
/* 306 */             p.sendMessage(ChatColor.RED + "Failure :(");
/*     */           }
/* 308 */           return true; }
/* 309 */         if (args[0].equalsIgnoreCase("ctrl")) {
/* 310 */           Entity focus = getEntity(p);
/* 311 */           if (focus != null) {
/* 312 */             focus.b(((CraftPlayer)p).getHandle(), 1);
/*     */           }
/* 314 */           return true; }
/* 315 */         if (args[0].equalsIgnoreCase("select")) {
/* 316 */           Entity focus = getEntity(p);
/* 317 */           if (focus != null) {
/* 318 */             selected.put(p.getName(), focus);
/* 319 */             p.sendMessage(ChatColor.GOLD + "VoyageEntity selected");
/*     */           }
/* 321 */           return true; }
/* 322 */         if (args[0].equalsIgnoreCase("add")) {
/* 323 */           if (!selected.containsKey(p.getName())) {
/* 324 */             p.sendMessage(ChatColor.RED + "Please select a dragon first!");
/*     */           } else {
/* 326 */             ((Entity)selected.get(p.getName())).b(((CraftPlayer)p).getHandle(), 20);
/* 327 */             p.sendMessage(ChatColor.GRAY + "Point added");
/*     */           }
/* 329 */           return true; }
/* 330 */         if (args[0].equalsIgnoreCase("kill")) {
/* 331 */           if (!selected.containsKey(p.getName())) {
/* 332 */             p.sendMessage(ChatColor.RED + "Please select a dragon first!");
/*     */           } else {
/* 334 */             ((Entity)selected.get(p.getName())).b(((CraftPlayer)p).getHandle(), 5);
/* 335 */             ((Entity)selected.get(p.getName())).b(((CraftPlayer)p).getHandle(), 21);
/* 336 */             p.sendMessage(ChatColor.GRAY + "Entity removed");
/*     */           }
/* 338 */           return true; }
/* 339 */         if (args[0].equalsIgnoreCase("editpath")) {
/* 340 */           if (!selected.containsKey(p.getName())) {
/* 341 */             p.sendMessage(ChatColor.RED + "Please select a dragon first!");
/*     */           } else {
/* 343 */             ((Entity)selected.get(p.getName())).b(((CraftPlayer)p).getHandle(), 4);
/* 344 */             p.sendMessage(ChatColor.GRAY + "Path ready");
/*     */           }
/* 346 */           return true; }
/* 347 */         if (args[0].equalsIgnoreCase("cleanpath")) {
/* 348 */           if (!selected.containsKey(p.getName())) {
/* 349 */             p.sendMessage(ChatColor.RED + "Please select a dragon first!");
/*     */           } else {
/* 351 */             ((Entity)selected.get(p.getName())).b(((CraftPlayer)p).getHandle(), 5);
/* 352 */             p.sendMessage(ChatColor.GRAY + "Path ready");
/*     */           }
/* 354 */           return true; }
/* 355 */         if (args[0].equalsIgnoreCase("demo")) {
/* 356 */           if (!selected.containsKey(p.getName())) {
/* 357 */             p.sendMessage(ChatColor.RED + "Please select a dragon first!");
/*     */           } else {
/* 359 */             ((Entity)selected.get(p.getName())).b(((CraftPlayer)p).getHandle(), 6);
/*     */           }
/* 361 */           return true;
/*     */         }
/*     */       } else {
/* 364 */         p.sendMessage(ChatColor.GOLD + "You are not permitted to use this command. Please input the password or login with an OP account.");
/* 365 */         return true;
/*     */       }
/*     */     }
/* 368 */     return false;
/*     */   }
/*     */   
/*     */   public void onDisable() {}
/*     */ }


/* Location:              C:\intellij\VoxelVoyage\VoxelVoyage.jar!\com\thevoxelbox\voyage\VoxelVoyage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */