/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voyage;

import com.thevoxelbox.voyage.entity.BezierPoint;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 *
 * @author Voxel
 */
public class PrzlabsCrystal extends EntityEnderCrystal implements PrzlabsEntity {

    private Player focus;
    private BezierPoint bezi;
    private int index;
    private boolean controlled;
    private PrzlabsEntity owner;
    private boolean decorative = false;
    private int message = 0;
    private String[] messages = new String[]{"Hello, I'm decorative!", "Pretty cool huh?", "Bet you can't float like this!", "I'm cooler than you."};

    public PrzlabsCrystal(World world) {
        super(world);
    }

    public PrzlabsCrystal(World world, boolean decor) {
        super(world);
        decorative = decor;
    }

    public PrzlabsCrystal(World world, BezierPoint point, int count, PrzlabsEntity mother) {
        super(world);
        bezi = point;
        index = count;
        owner = mother;
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
            int i = MathHelper.floor(target_x * 32.0D);
            int j = MathHelper.floor(target_y * 32.0D);
            int k = MathHelper.floor(target_z * 32.0D);
//            for (Object o : world.players) {
//                ((EntityPlayer) o).netServerHandler.sendPacket(new Packet34EntityTeleport(id, i, j, k, (byte) 0, (byte) 0));
//            }
        }
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        return false;
    }

    @Override
    public void rightClick(Player user, int action) {
        if (decorative) {
            user.sendMessage(ChatColor.AQUA + "[" + ChatColor.LIGHT_PURPLE + "CRYSTAL" + ChatColor.AQUA + "] " + ChatColor.GREEN + messages[message]);
            return;
        }
        if (focus == null || focus.getUniqueId() != user.getUniqueId()) {
            focus = user;
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
