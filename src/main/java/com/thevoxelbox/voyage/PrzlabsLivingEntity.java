package com.thevoxelbox.voyage;

import net.minecraft.server.v1_12_R1.EntityLiving;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftLivingEntity;

public class PrzlabsLivingEntity extends CraftLivingEntity {
    public PrzlabsLivingEntity(final CraftServer server, final EntityLiving entity) {
        super(server, entity);
    }

    @Override
    public void remove() {
    }
}
