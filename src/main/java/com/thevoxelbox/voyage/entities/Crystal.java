package com.thevoxelbox.voyage.entities;

import com.thevoxelbox.voyage.PrzlabsEntity;
import net.minecraft.server.v1_12_R1.DamageSource;
import net.minecraft.server.v1_12_R1.EntityEnderCrystal;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.MathHelper;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NetServerHandler;
import net.minecraft.server.v1_12_R1.Packet34EntityTeleport;
import net.minecraft.server.v1_12_R1.World;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;


public class Crystal extends EntityEnderCrystal {
    private Player focus;
    private BeziPoint bezi;
    private int index;
    private boolean controlled;
    private PrzlabsEntity owner;
    private boolean decorative = false;
    private int message = 0;
    private String[] messages = {"Hello, I'm decorative!", "Pretty cool huh?", "Bet you can't float like this!", "I'm cooler than you."};

    public Crystal(World world) {
        super(world);
    }

    public Crystal(World world, boolean decor) {
        super(world);
        this.decorative = decor;
    }

    public Crystal(World world, BeziPoint point, int count, PrzlabsEntity mother) {
        super(world);
        this.bezi = point;
        this.index = count;
        this.owner = mother;
        setPosition(this.bezi.x, this.bezi.y, this.bezi.z);
    }

    public void w_() {
        if (this.decorative) {
            if (++this.message == this.messages.length) {
                this.message = 0;
            }
            return;
        }
        if (this.bezi == null) {
            die();
            return;
        }
        if (this.focus == null) return;
        int i;
        int j;
        int k;
        if (this.controlled) {
            double distance = 1.75D;
            Location player_loc = this.focus.getLocation();
            double rot_x = (player_loc.getYaw() + 90.0F) % 360.0F;
            double rot_y = player_loc.getPitch() * -1.0F;
            double rot_ycos = Math.cos(Math.toRadians(rot_y));
            double rot_ysin = Math.sin(Math.toRadians(rot_y));
            double rot_xcos = Math.cos(Math.toRadians(rot_x));
            double rot_xsin = Math.sin(Math.toRadians(rot_x));

            double h_length = distance * rot_ycos;
            double y_offset = distance * rot_ysin;
            double x_offset = h_length * rot_xcos;
            double z_offset = h_length * rot_xsin;

            double target_x = x_offset + player_loc.getX();
            double target_y = y_offset + player_loc.getY();
            double target_z = z_offset + player_loc.getZ();
            this.bezi.x = target_x;
            this.bezi.y = target_y;
            this.bezi.z = target_z;
            setPosition(target_x, target_y, target_z);
            i = MathHelper.floor(target_x * 32.0D);
            j = MathHelper.floor(target_y * 32.0D);
            k = MathHelper.floor(target_z * 32.0D);
            for (Object o : this.world.players) {
                ((EntityPlayer) o).netServerHandler.sendPacket(new Packet34EntityTeleport(this.id, i, j, k, (byte) 0, (byte) 0));
            }
        }
    }


    public boolean damageEntity(DamageSource ds, int i) {
        return false;
    }

    public void rightClick(Player user) {
        if (this.decorative) {
            user.sendMessage(ChatColor.AQUA + "[" + ChatColor.LIGHT_PURPLE + "CRYSTAL" + ChatColor.AQUA + "] " + ChatColor.GREEN + this.messages[this.message]);
        }
        if ((this.focus == null) || (this.focus.getUniqueId() != user.getUniqueId())) {
            this.focus = user;
        }
        this.controlled = (!this.controlled);
    }

    public void a(NBTTagCompound in) {
        byte byt = in.getByte("Decor");
        if (byt == 1) {
            this.decorative = true;
        } else if (byt == 2) {
            this.decorative = false;
        } else {
            this.decorative = false;
        }
    }

    public void b(NBTTagCompound out) {
        out.setByte("Decor", (byte) (this.decorative ? 1 : 2));
    }

    public int getAirTicks() {
        return this.decorative ? 12345 : 12346;
    }

    public void b(net.minecraft.server.v1_12_R1.Entity entity, int index) {
        if ((entity instanceof EntityPlayer)) {
            EntityPlayer play = (EntityPlayer) entity;
            org.bukkit.entity.Entity bplay = play.getBukkitEntity();
            if ((bplay instanceof Player)) {
                rightClick((Player) bplay);
            }
        }
    }
}
