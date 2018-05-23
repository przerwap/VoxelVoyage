package com.thevoxelbox.voyage.events;

import com.google.inject.Inject;
import com.thevoxelbox.voyage.entity.Voyage;
import com.thevoxelbox.voyage.enums.EnumTools;
import com.thevoxelbox.voyage.services.VoyageService;
import com.thevoxelbox.voyage.utils.Response;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

public class RightClickEvent  {
    @Inject
    private VoyageService voyageService;

    @EventHandler
    public void onToolClickEvent(PlayerInteractEvent event) {
        if (EnumTools.CREATE_TOOL.matches(event.getItem())) {
            Response response = voyageService.createToolHandler(event.getPlayer().getUniqueId(), event.getClickedBlock().getLocation());
        }

    }
}
