package com.thevoxelbox.voyage;

import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.EntityEnderSignal;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.World;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PrzlabsRedBalloon extends EntityEnderSignal {
    private Player focus;

    public PrzlabsRedBalloon(World world) {
        super(world);
        if (focus == null) {
            die();
        }
    }

    public PrzlabsRedBalloon(Player user, World world) {
        super(world);
        this.focus = user;
    }

    @Override
    public void B_() {
        if (focus == null) {
            die();
        } else {
            double distance = 0.26;
            Location player_loc = focus.getLocation();
            double rot_x = (player_loc.getYaw() + 135) % 360;
            double rot_y = 0;
            double rot_ycos = Math.cos(Math.toRadians(rot_y));
            double rot_ysin = Math.sin(Math.toRadians(rot_y));
            double rot_xcos = Math.cos(Math.toRadians(rot_x));
            double rot_xsin = Math.sin(Math.toRadians(rot_x));

            double h_length = (distance * rot_ycos);
            double y_offset = (distance * rot_ysin);
            double x_offset = (h_length * rot_xcos);
            double z_offset = (h_length * rot_xsin);

            double target_x = x_offset + player_loc.getX();
            double target_y = y_offset + player_loc.getY() + 2.7;
            double target_z = z_offset + player_loc.getZ();
            setPosition(target_x, target_y, target_z);
        }
    }

    @Override
    public void a(BlockPosition blockPosition) {
    }

    @Override
    public void a(NBTTagCompound in) {
        die();
    }
}
