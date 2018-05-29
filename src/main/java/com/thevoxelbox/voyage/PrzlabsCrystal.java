package com.thevoxelbox.voyage;

import net.minecraft.server.v1_12_R1.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import static java.lang.System.currentTimeMillis;

public class PrzlabsCrystal extends EntityEnderCrystal implements PrzlabsEntity {
    private Player focus;
    private BezierPoint bezi;
    private boolean controlled;
    private boolean decorative = false;
    private int message = 0;
    private String[] messages = new String[]{"Hello, I'm decorative!", "Pretty cool huh?", "Bet you can't float like this!", "I'm cooler than you."};
    private long cooldown;

    public PrzlabsCrystal(World world) {
        super(world);
    }

    public PrzlabsCrystal(World world, boolean decor) {
        super(world);
        decorative = decor;
    }

    public PrzlabsCrystal(World world, BezierPoint point) {
        super(world);
        bezi = point;
        setPosition(bezi.x, bezi.y, bezi.z);
    }

    @Override
    public void B_() {
        if (decorative) {
            if (++message == messages.length) {
                message = 0;
            }
            return;
        }
        if (bezi == null) {
            die();
            return;
        }
        if (controlled) {
            double distance = 1.75;
            Location player_loc = focus.getLocation();
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
            double target_y = y_offset + player_loc.getY() + 0.5;
            double target_z = z_offset + player_loc.getZ();
            bezi.x = target_x;
            bezi.y = target_y;
            bezi.z = target_z;
            setPosition(target_x, target_y, target_z);
            for (Object o : world.players) {
                ((EntityPlayer) o).playerConnection.sendPacket(new PacketPlayOutEntityTeleport(this));
            }
        }
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        return false;
    }

    @Override
    public void rightClick(Player user, int action) {
        if (currentTimeMillis() - cooldown < 2000) {
            return;
        }
        cooldown = currentTimeMillis();

        if (decorative) {
            user.sendMessage(ChatColor.AQUA + "[" + ChatColor.LIGHT_PURPLE + "CRYSTAL" + ChatColor.AQUA + "] " + ChatColor.GREEN + messages[message]);
            return;
        }
        if (focus == null || focus.getUniqueId() != user.getUniqueId()) {
            focus = user;
            user.sendMessage(ChatColor.GOLD + "Crystal Focused");
        }
        controlled = !controlled;
    }

    @Override
    public void a(NBTTagCompound in) {
        byte byt = in.getByte("Decor");
        if (byt == 1) {
            decorative = true;
        } else if (byt == 2) {
            decorative = false;
        } else {
            decorative = false;
        }
    }

    @Override
    public void b(NBTTagCompound out) {
        out.setByte("Decor", (decorative ? (byte) 1 : (byte) 2));
    }

    @Override
    public int getAirTicks() {
        return (decorative ? 12345 : 12346);
    }
}
