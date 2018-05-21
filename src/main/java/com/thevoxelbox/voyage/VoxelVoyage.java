package com.thevoxelbox.voyage;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityTypes;
import net.minecraft.server.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class VoxelVoyage
        extends JavaPlugin {
    static {
        try {
            EntityTypes et = new EntityTypes();

            Method addEntity = EntityTypes.class.getDeclaredMethod("a", new Class[]{Class.class, String.class, Integer.TYPE});

            addEntity.setAccessible(true);

            addEntity.invoke(et, new Object[]{PrzlabsDragon.class, "PrzlabsDragon", Integer.valueOf(63)});
            System.out.println("[VoxelVoyage] PrzlabsDragon entity registered!");

            addEntity.invoke(et, new Object[]{PrzlabsCrystal.class, "PrzlabsCrystal", Integer.valueOf(200)});
            System.out.println("[VoxelVoyage] PrzlabsCrystal entity registered!");

            addEntity.invoke(et, new Object[]{PrzlabsBlaze.class, "PrzlabsBlaze", Integer.valueOf(61)});
            System.out.println("[VoxelVoyage] PrzlabsBlaze entity registered!");
        } catch (NoSuchMethodException ex) {
            System.out.println("[VoxelVoyage] PrzlabsDragon entity failed to register!");
            Logger.getLogger(VoxelVoyage.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            System.out.println("[VoxelVoyage] PrzlabsDragon entity failed to register!");
            Logger.getLogger(VoxelVoyage.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            System.out.println("[VoxelVoyage] PrzlabsDragon entity failed to register!");
            Logger.getLogger(VoxelVoyage.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            System.out.println("[VoxelVoyage] PrzlabsDragon entity failed to register!");
            Logger.getLogger(VoxelVoyage.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            System.out.println("[VoxelVoyage] PrzlabsDragon entity failed to register!");
            Logger.getLogger(VoxelVoyage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected static final Logger log = Logger.getLogger("Minecraft");
    public static boolean SPAWN_ENTITIES = true;
    private VPlayer plistener = new VPlayer();
    private VEntity elistener = new VEntity();
    public static TreeMap<UUID, TreeMap<Integer, Entity>> entities = new TreeMap();
    public static TreeMap<String, Entity> selected = new TreeMap();
    public static TreeSet<String> flying = new TreeSet();
    public static TreeSet<String> permitted = new TreeSet();
    public static String password;
    public static int voyageItem = 371;

    public static Entity getEntity(Player p) {

        if (!entities.containsKey(p.getWorld().getUID())) {
            p.sendMessage(ChatColor.RED + "No Voyage entity found.");
            return null;
        }
        Entity closest = null;
        double range = 9.9999999E7D;

        double bx = p.getLocation().getX();
        double by = p.getLocation().getY();
        double bz = p.getLocation().getZ();

        for (Entity ent : ((TreeMap) entities.get(p.getWorld().getUID())).values()) {
            switch (ent.getAirTicks()) {
                case 12347:
                case 12348:
                case 12349:
                case 12350:
                    double erange = Math.pow(bx - ent.locX, 2.0D) + Math.pow(by - ent.locY, 2.0D) + Math.pow(bz - ent.locZ, 2.0D);

                    if (!ent.dead) {


                        if (erange < range) {
                            range = erange;
                            closest = ent;
                        }
                    }

                    break;
            }

        }

        if ((closest != null) && (Math.pow(range, 0.5D) <= 40.0D)) {
            return closest;
        }
        p.sendMessage(ChatColor.RED + "No Voyage entity found.");
        return null;
    }

    public static boolean isPermitted(Player user) {
        return user.isOp() ? true : permitted.contains(user.getName());
    }


    public void onEnable() {
        for (Iterator i$ = Bukkit.getWorlds().iterator(); i$.hasNext(); ) {
            wrld = (World) i$.next();
            CraftWorld cw = (CraftWorld) wrld;
            for (Object o : cw.getHandle().entityList) {
                Entity ent = (Entity) o;
                switch (ent.getAirTicks()) {
                    case 12346:
                        ent.die();
                        break;

                    case 12347:
                    case 12348:
                    case 12349:
                    case 12350:
                        if (entities.containsKey(wrld.getUID())) {
                            ((TreeMap) entities.get(wrld.getUID())).put(Integer.valueOf(ent.id), ent);
                        } else {
                            entities.put(wrld.getUID(), new TreeMap());
                            ((TreeMap) entities.get(wrld.getUID())).put(Integer.valueOf(ent.id), ent);
                        }

                        break;
                }

            }
        }

        World wrld;
        loadProps();
        Bukkit.getPluginManager().registerEvent(Event.Type.PLAYER_INTERACT_ENTITY, this.plistener, Event.Priority.Normal, this);
        Bukkit.getPluginManager().registerEvent(Event.Type.PLAYER_INTERACT, this.plistener, Event.Priority.Normal, this);
        Bukkit.getPluginManager().registerEvent(Event.Type.CREATURE_SPAWN, this.elistener, Event.Priority.Lowest, this);

        PluginDescriptionFile pdfFile = getDescription();
        log.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled! Let's fly.");
    }

    private void loadProps() {
        File f = new File("plugins/VoxelVoyage/voyage.properties");
        if (!f.exists()) {
            f.getParentFile().mkdirs();
            try {
                f.createNewFile();
                saveProps();
            } catch (IOException ex) {
                Logger.getLogger(VoxelVoyage.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Properties prop = new Properties();
        try {
            prop.load(new FileReader(f));
        } catch (IOException ex) {
            Logger.getLogger(VoxelVoyage.class.getName()).log(Level.SEVERE, null, ex);
        }
        password = prop.getProperty("Password", null);
        if ((password != null) && (password.equalsIgnoreCase("null"))) {
            password = null;
        }
        voyageItem = Integer.parseInt(prop.getProperty("VoyageItem", "371"));
    }

    private void saveProps() {
        File f = new File("plugins/VoxelVoyage/voyage.properties");
        if (!f.exists()) {
            f.getParentFile().mkdirs();
            try {
                f.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(VoxelVoyage.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Properties prop = new Properties();
        prop.setProperty("Password", password == null ? "null" : password);
        prop.setProperty("VoyageItem", String.valueOf(voyageItem));
        try {
            prop.store(new PrintWriter(f), null);
        } catch (IOException ex) {
            Logger.getLogger(VoxelVoyage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = null;
        if ((sender instanceof Player)) {
            p = (Player) sender;
        } else {
            sender.sendMessage("Only players permitted!");
        }
        if (command.getName().equals("voyage")) {
            if ((args == null) || (args.length == 0)) {
                p.sendMessage(ChatColor.GREEN + "The available sub-commands are: /voyage [help] [?] [create] [ctrl] [select] [add] [editPath] [cleanpath] [demo] [kill] [createblaze]");
                return true;
            }
            if ((args[0].equalsIgnoreCase("help")) || (args[0].equals("?"))) {
                p.sendMessage(ChatColor.BLUE + "/voyage create - Creates a new Voyaging VoxelDragon.");
                p.sendMessage(ChatColor.AQUA + "/voyage createblaze - Creates a new Voyaging VoxelBlaze.");
                p.sendMessage(ChatColor.BLUE + "/voyage ctrl - Toggles the controll over a Voyager.");
                p.sendMessage(ChatColor.AQUA + "/voyage select - Selects a nearby Voyager.");
                p.sendMessage(ChatColor.BLUE + "/voyage add - Adds a new point to your selected Voyager's movement path.");
                p.sendMessage(ChatColor.AQUA + "/voyage editPath - This command allows the path for the selected Voyager to become visible as Crystals.");
                p.sendMessage(ChatColor.BLUE + "/voyage cleanpath - Cleans up the selected Voyager's Crystal path by removing the Crystals.");
                p.sendMessage(ChatColor.AQUA + "/voyage demo - Toggles the selected Voyager's demo mode.");
                p.sendMessage(ChatColor.BLUE + "/voyage kill - Destroys the selected Voyager and its associated waypoints.");
                p.sendMessage(ChatColor.AQUA + "/voyage password [pass] - Gives you access to Create Voyagers as a non-op user, or changes the password if you are OP.");
                return true;
            }
            if (args[0].equalsIgnoreCase("password")) {
                if (args.length < 2) {
                    p.sendMessage(ChatColor.RED + "Invalid number of parameters!");
                    return true;
                }
                if (p.isOp()) {
                    password = args[1];
                    p.sendMessage(ChatColor.AQUA + "The password has been set to: " + ChatColor.GREEN + password);
                    permitted.clear();
                    saveProps();
                    return true;
                }
                if (password == null) {
                    p.sendMessage("A password is not set.");
                    return true;
                }
                if (args[1].equals(password)) {
                    permitted.add(p.getName());
                    p.sendMessage(ChatColor.GREEN + "Password accepted!");
                } else {
                    p.sendMessage(ChatColor.DARK_GREEN + "Invalid password!");
                }
                return true;
            }
            if (isPermitted(p)) {
                if (args[0].equalsIgnoreCase("create")) {
                    PrzlabsDragon dragon = new PrzlabsDragon(((CraftWorld) p.getWorld()).getHandle(), true, p.getLocation());
                    if (((CraftWorld) p.getWorld()).getHandle().addEntity(dragon, CreatureSpawnEvent.SpawnReason.CUSTOM)) {
                        p.sendMessage(ChatColor.GREEN + "Done!");
                    } else {
                        p.sendMessage(ChatColor.RED + "Failure :(");
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("createblaze")) {
                    PrzlabsBlaze dragon = new PrzlabsBlaze(((CraftWorld) p.getWorld()).getHandle(), true, p.getLocation());
                    if (((CraftWorld) p.getWorld()).getHandle().addEntity(dragon, CreatureSpawnEvent.SpawnReason.CUSTOM)) {
                        p.sendMessage(ChatColor.GREEN + "Done!");
                    } else {
                        p.sendMessage(ChatColor.RED + "Failure :(");
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("crystal")) {
                    PrzlabsCrystal crystal = new PrzlabsCrystal(((CraftWorld) p.getWorld()).getHandle(), true);
                    crystal.setPosition(p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ());
                    if (((CraftWorld) p.getWorld()).getHandle().addEntity(crystal, CreatureSpawnEvent.SpawnReason.CUSTOM)) {
                        p.sendMessage(ChatColor.GREEN + "Done!");
                    } else {
                        p.sendMessage(ChatColor.RED + "Failure :(");
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("ctrl")) {
                    Entity focus = getEntity(p);
                    if (focus != null) {
                        focus.b(((CraftPlayer) p).getHandle(), 1);
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("select")) {
                    Entity focus = getEntity(p);
                    if (focus != null) {
                        selected.put(p.getName(), focus);
                        p.sendMessage(ChatColor.GOLD + "VoyageEntity selected");
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("add")) {
                    if (!selected.containsKey(p.getName())) {
                        p.sendMessage(ChatColor.RED + "Please select a dragon first!");
                    } else {
                        ((Entity) selected.get(p.getName())).b(((CraftPlayer) p).getHandle(), 20);
                        p.sendMessage(ChatColor.GRAY + "Point added");
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("kill")) {
                    if (!selected.containsKey(p.getName())) {
                        p.sendMessage(ChatColor.RED + "Please select a dragon first!");
                    } else {
                        ((Entity) selected.get(p.getName())).b(((CraftPlayer) p).getHandle(), 5);
                        ((Entity) selected.get(p.getName())).b(((CraftPlayer) p).getHandle(), 21);
                        p.sendMessage(ChatColor.GRAY + "Entity removed");
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("editpath")) {
                    if (!selected.containsKey(p.getName())) {
                        p.sendMessage(ChatColor.RED + "Please select a dragon first!");
                    } else {
                        ((Entity) selected.get(p.getName())).b(((CraftPlayer) p).getHandle(), 4);
                        p.sendMessage(ChatColor.GRAY + "Path ready");
                    }
                }
                if (args[0].equalsIgnoreCase("cleanpath")) {
                    if (!selected.containsKey(p.getName())) {
                        p.sendMessage(ChatColor.RED + "Please select a dragon first!");
                    } else {
                        ((Entity) selected.get(p.getName())).b(((CraftPlayer) p).getHandle(), 5);
                        p.sendMessage(ChatColor.GRAY + "Path ready");
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("demo")) {
                    if (!selected.containsKey(p.getName())) {
                        p.sendMessage(ChatColor.RED + "Please select a dragon first!");
                    } else {
                        ((Entity) selected.get(p.getName())).b(((CraftPlayer) p).getHandle(), 6);
                    }
                    return true;
                }
            } else {
                p.sendMessage(ChatColor.GOLD + "You are not permitted to use this command. Please input the password or login with an OP account.");
                return true;
            }
        }
        return false;
    }

    public void onDisable() {
    }
}
