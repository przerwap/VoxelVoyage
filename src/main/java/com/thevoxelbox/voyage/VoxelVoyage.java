package com.thevoxelbox.voyage;

import net.minecraft.server.v1_12_R1.Entity;
import net.minecraft.server.v1_12_R1.EntityTypes;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VoxelVoyage extends JavaPlugin {

    static {
        try {
            EntityTypes et = EntityTypes.class.getConstructor().newInstance();

            Method addEntity = EntityTypes.class.getDeclaredMethod("a", int.class, String.class, Class.class, String.class);

            addEntity.setAccessible(true);

            addEntity.invoke(et, 63, "przlabs_dragon", PrzlabsDragon.class, "PrzlabsDragon");
            System.out.println("[VoxelVoyage] PrzlabsDragon entity registered!");

            addEntity.invoke(et, 200, "przlabs_crystal", PrzlabsCrystal.class, "PrzlabsCrystal");
            System.out.println("[VoxelVoyage] PrzlabsCrystal entity registered!");

            addEntity.invoke(et, 200, "przlabs_red_balloon", PrzlabsRedBalloon.class, "PrzlabsRedBalloon");
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException | IllegalArgumentException ex) {
            System.out.println("[VoxelVoyage] PrzlabsDragon entity failed to register!");
            Logger.getLogger(VoxelVoyage.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    protected static final Logger log = Logger.getLogger("Minecraft");
    public static boolean SPAWN_ENTITIES = true;
    private VPlayer plistener = new VPlayer();
    private VEntity elistener = new VEntity();
    public static TreeMap<UUID, TreeMap<UUID, Entity>> VOYAGE_ENTITIES = new TreeMap<>();
    public static TreeMap<String, Entity> selected = new TreeMap<>();
    public static TreeSet<String> flying = new TreeSet<>();
    public static TreeSet<String> permitted = new TreeSet<>();
    public static String password;
    public static int voyageItem = 371;
    public static boolean forceSpawning = false;

    public static Entity getNearestEntity(Player p) {
        if (!VOYAGE_ENTITIES.containsKey(p.getWorld().getUID())) {
            p.sendMessage(ChatColor.RED + "No Voyage entity found.");
            return null;
        }
        Entity closest = null;
        double range = 99999999;

        double bx = p.getLocation().getX();
        double by = p.getLocation().getY();
        double bz = p.getLocation().getZ();

        for (Entity ent : VOYAGE_ENTITIES.get(p.getWorld().getUID()).values()) {
            switch (ent.getAirTicks()) {
                case 12347:
                case 12348:
                case 12349:
                case 12350:
                    double erange = Math.pow(bx - ent.locX, 2) + Math.pow(by - ent.locY, 2) + Math.pow(bz - ent.locZ, 2);

                    if (ent.dead) {
                        continue;
                    }

                    if (erange < range) {
                        range = erange;
                        closest = ent;
                    }
                    break;

                default:
                    break;
            }
        }

        if (closest != null && Math.pow(range, 0.5) <= 40) {
            return closest;
        } else {
            p.sendMessage(ChatColor.RED + "No Voyage entity found.");
            return null;
        }
    }

    public static boolean isPermitted(Player user) {
        return (user.isOp() || permitted.contains(user.getName()));
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onEnable() {
        loadProps();

        for (World wrld : Bukkit.getWorlds()) {
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
                        if (!VOYAGE_ENTITIES.containsKey(wrld.getUID())) {
                            VOYAGE_ENTITIES.put(wrld.getUID(), new TreeMap<>());
                        }

                        VOYAGE_ENTITIES.get(wrld.getUID()).put(ent.getUniqueID(), ent);
                        break;

                    default:
                        break;
                }
            }
        }

        Bukkit.getPluginManager().registerEvents(plistener, this);
        Bukkit.getPluginManager().registerEvents(elistener, this);
//        Bukkit.getPluginManager().registerEvent(Type.PLAYER_INTERACT_ENTITY, plistener, Priority.Normal, this);
//        Bukkit.getPluginManager().registerEvent(Type.PLAYER_INTERACT, plistener, Priority.Normal, this);
//        Bukkit.getPluginManager().registerEvent(Type.CREATURE_SPAWN, elistener, Priority.Lowest, this);

/*        if (forceSpawning) {
            try {
                SimplePluginManager pm = (SimplePluginManager) Bukkit.getPluginManager();

                Method getListen = SimplePluginManager.class.getDeclaredMethod("getEventListeners", new Class[]{Event.Type.class});

                getListen.setAccessible(true);

                Object sset = getListen.invoke(pm, new Object[]{Event.Type.CREATURE_SPAWN});
                if (sset instanceof SortedSet) {
                    SortedSet set = (SortedSet) sset;
                    for (Object obj : set) {
                        if (obj instanceof RegisteredListener) {
                            RegisteredListener rl = (RegisteredListener) obj;
                            log.info("[VoxelVoyage] Registered CREATURE_SPAWN listener: " + rl.getPlugin().getDescription().getFullName() + " priority: " + rl.getPriority().name());
                        }
                    }
                }
            } catch (Exception e) {
                Logger.getLogger(VoxelVoyage.class.getName()).log(Level.SEVERE, null, e);
            }
        }*/

        PluginDescriptionFile pdfFile = this.getDescription();
        log.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled! Let's fly.");
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
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
        if (password != null && password.equalsIgnoreCase("null")) {
            password = null;
        }
        voyageItem = Integer.parseInt(prop.getProperty("VoyageItem", "371"));
        forceSpawning = Boolean.parseBoolean(prop.getProperty("ForceSpawning"));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
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
        prop.setProperty("Password", (password == null ? "null" : password));
        prop.setProperty("VoyageItem", String.valueOf(voyageItem));
        prop.setProperty("ForceSpawning", Boolean.toString(forceSpawning));
        try {
            prop.store(new PrintWriter(f), null);
        } catch (IOException ex) {
            Logger.getLogger(VoxelVoyage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p;

        if ((sender instanceof Player)) {
            p = (Player) sender;
        } else {
            sender.sendMessage("Only players permitted!");
            return false;
        }

        if ((command.getName().equals("voyage"))) {
            if ((args == null) || (args.length == 0)) {
                p.sendMessage(ChatColor.GREEN + "The available sub-commands are: /voyage [help] [?] [create] [ctrl] [ctrlrot] [ctrlpos] [select] [add] [editPath] [cleanpath] [demo] [kill] [createblaze] [loadbackup]");
                return true;
            }
            if (args[0].equalsIgnoreCase("help") || args[0].equals("?")) {
                p.sendMessage(ChatColor.BLUE + "/voyage create - Creates a new Voyaging VoxelDragon.");
                p.sendMessage(ChatColor.AQUA + "/voyage createblaze - Creates a new Voyaging VoxelBlaze.");
                p.sendMessage(ChatColor.BLUE + "/voyage ctrl | ctrlpos | ctrlrot - Toggles the controll over a Voyager.");
                p.sendMessage(ChatColor.AQUA + "/voyage select - Selects a nearby Voyager.");
                p.sendMessage(ChatColor.BLUE + "/voyage add - Adds a new point to your selected Voyager's movement path.");
                p.sendMessage(ChatColor.AQUA + "/voyage editPath - This command allows the path for the selected Voyager to become visible as Crystals.");
                p.sendMessage(ChatColor.BLUE + "/voyage cleanpath - Cleans up the selected Voyager's Crystal path by removing the Crystals.");
                p.sendMessage(ChatColor.AQUA + "/voyage demo - Toggles the selected Voyager's demo mode.");
                p.sendMessage(ChatColor.BLUE + "/voyage kill - Destroys the selected Voyager and its associated waypoints.");
                p.sendMessage(ChatColor.AQUA + "/voyage password [pass] - Gives you access to Create Voyagers as a non-op user, or changes the password if you are OP.");
                return true;
            } else if (args[0].equalsIgnoreCase("password")) {
                if (args.length < 2) {
                    if (p.isOp()) {
                        p.sendMessage(ChatColor.AQUA + "The password is set to: " + ChatColor.GREEN + password);
                        return true;
                    }
                    p.sendMessage(ChatColor.RED + "Invalid number of parameters!");
                    return true;
                }
                if (p.isOp()) {
                    password = args[1];
                    p.sendMessage(ChatColor.AQUA + "The password has been set to: " + ChatColor.GREEN + password);
                    permitted.clear();
                    saveProps();
                    return true;
                } else {
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
            }

            if (isPermitted(p)) {
                if (args[0].equalsIgnoreCase("create")) {
                    if (args.length == 2) {
                        double speed = Double.parseDouble(args[1]);
                        PrzlabsDragon dragon = new PrzlabsDragon(((CraftWorld) p.getWorld()).getHandle(), true, p.getLocation(), speed);
                        if (((CraftWorld) p.getWorld()).getHandle().addEntity(dragon, SpawnReason.CUSTOM)) {
                            p.sendMessage(ChatColor.GREEN + "Done!");
                        } else {
                            p.sendMessage(ChatColor.RED + "Failure :(");
                        }
                    } else {
                        PrzlabsDragon dragon = new PrzlabsDragon(((CraftWorld) p.getWorld()).getHandle(), true, p.getLocation());
                        if (((CraftWorld) p.getWorld()).getHandle().addEntity(dragon, SpawnReason.CUSTOM)) {
                            p.sendMessage(ChatColor.GREEN + "Done!");
                        } else {
                            p.sendMessage(ChatColor.RED + "Failure :(");
                        }
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("crystal")) {
                    PrzlabsCrystal crystal = new PrzlabsCrystal(((CraftWorld) p.getWorld()).getHandle(), true);
                    crystal.setPosition(p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ());
                    if (((CraftWorld) p.getWorld()).getHandle().addEntity(crystal, SpawnReason.CUSTOM)) {
                        p.sendMessage(ChatColor.GREEN + "Done!");
                    } else {
                        p.sendMessage(ChatColor.RED + "Failure :(");
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("shinyballoon")) {
                    PrzlabsRedBalloon crystal = new PrzlabsRedBalloon(p, ((CraftWorld) p.getWorld()).getHandle());
                    crystal.setPosition(p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ());
                    if (((CraftWorld) p.getWorld()).getHandle().addEntity(crystal, SpawnReason.CUSTOM)) {
                        p.sendMessage(ChatColor.GREEN + "Done!");
                    } else {
                        p.sendMessage(ChatColor.RED + "Failure :(");
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("ctrl")) {
                    performRightClick(p, 1);
                    return true;
                } else if (args[0].equalsIgnoreCase("ctrlrot")) {
                    performRightClick(p, 7);
                    return true;
                } else if (args[0].equalsIgnoreCase("ctrlpos")) {
                    performRightClick(p, 8);
                    return true;
                } else if (args[0].equalsIgnoreCase("select")) {
                    Entity focus = getNearestEntity(p);
                    if (focus != null) {
                        selected.put(p.getName(), focus);
                        p.sendMessage(ChatColor.GOLD + "VoyageEntity selected");
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("add")) {
                    if (!selected.containsKey(p.getName())) {
                        p.sendMessage(ChatColor.RED + "Please select a dragon first!");
                    } else {
                        rightClickSelected(p, 20);
                        p.sendMessage(ChatColor.GRAY + "Point added");
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("kill")) {
                    if (!selected.containsKey(p.getName())) {
                        p.sendMessage(ChatColor.RED + "Please select a dragon first!");
                    } else {
                        rightClickSelected(p, 5);
                        rightClickSelected(p, 21);
                        p.sendMessage(ChatColor.GRAY + "Entity removed");
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("editpath")) {
                    if (!selected.containsKey(p.getName())) {
                        p.sendMessage(ChatColor.RED + "Please select a dragon first!");
                    } else {
                        rightClickSelected(p, 4);
                        p.sendMessage(ChatColor.GRAY + "Path ready");
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("cleanpath")) {
                    if (!selected.containsKey(p.getName())) {
                        p.sendMessage(ChatColor.RED + "Please select a dragon first!");
                    } else {
                        rightClickSelected(p, 5);
                        p.sendMessage(ChatColor.GRAY + "Path ready");
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("demo")) {
                    if (!selected.containsKey(p.getName())) {
                        p.sendMessage(ChatColor.RED + "Please select a dragon first!");
                    } else {
                        rightClickSelected(p, 6);
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("loadBackup")) {
                    if (args.length == 2) {
                        World world = Bukkit.getWorld(args[1]);
                        if (world != null) {
                            p.sendMessage(ChatColor.LIGHT_PURPLE + "Beginning...");
                            VoyageData.loadVoyagersFromBackup(((CraftWorld) world).getHandle());
                            p.sendMessage(ChatColor.DARK_PURPLE + "Finished!");
                        } else {
                            p.sendMessage(ChatColor.RED + "No world found by the name \"" + args[1] + "\"");
                        }
                    } else {
                        p.sendMessage(ChatColor.LIGHT_PURPLE + "Beginning...");
                        VoyageData.loadVoyagersFromBackup(null);
                        p.sendMessage(ChatColor.DARK_PURPLE + "Finished!");
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

    private void rightClickSelected(Player player, int action) {
        Entity entity = selected.get(player.getName());

        rightClickEntity(player, action, entity);
    }

    private void performRightClick(Player player, int action) {
        Entity entity = getNearestEntity(player);

        rightClickEntity(player, action, entity);
    }

    private void rightClickEntity(Player player, int action, Entity entity) {
        if (entity != null && entity instanceof PrzlabsEntity) {
            ((PrzlabsEntity) entity).rightClick(player, action);
        }
    }
}
