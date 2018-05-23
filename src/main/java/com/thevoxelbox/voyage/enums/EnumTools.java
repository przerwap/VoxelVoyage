package com.thevoxelbox.voyage.enums;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum EnumTools {
    CREATE_TOOL(Material.SPECTRAL_ARROW, "VoxelVoyage Create"),
    EDIT_TOOL(Material.SPECTRAL_ARROW, "VoxelVoyage Edit");

    private Material material;
    private String name;

    EnumTools(Material material, String name) {
        this.material = material;
        this.name = name;
    }

    public boolean matches(ItemStack itemStack) {
        if (itemStack.getType().equals(material)) {
            if (itemStack.getItemMeta().getDisplayName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public Material getMaterial() {
        return  material;
    }
}
