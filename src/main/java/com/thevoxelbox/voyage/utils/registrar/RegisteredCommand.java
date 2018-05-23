package com.thevoxelbox.voyage.utils.registrar;

import org.apache.commons.cli.Options;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class RegisteredCommand extends Command {
    public RegisteredCommand(String name, String description, String usageMessage, List<String> aliases) {
        super(name, description, usageMessage, aliases);
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        return false;
    }
}
