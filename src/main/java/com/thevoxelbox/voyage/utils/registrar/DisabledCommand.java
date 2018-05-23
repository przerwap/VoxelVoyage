package com.thevoxelbox.voyage.utils.registrar;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class DisabledCommand extends Command {

    public DisabledCommand(String name) {
        super(name, "", "",  new ArrayList<String>());
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        return false;
    }
}
