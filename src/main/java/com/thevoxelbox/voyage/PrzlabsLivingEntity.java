package com.thevoxelbox.voyage;

import net.minecraft.server.EntityLiving;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;


public class PrzlabsLivingEntity
        extends CraftLivingEntity {
    public PrzlabsLivingEntity(CraftServer server, EntityLiving entity) {
        super(server, entity);
    }

    public void remove() {
    }
}