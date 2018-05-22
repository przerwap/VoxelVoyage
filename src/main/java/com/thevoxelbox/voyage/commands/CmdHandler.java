package com.thevoxelbox.voyage.commands;


import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.bukkit.command.defaults.BukkitCommand;

import java.util.ArrayList;
import java.util.List;

public abstract class CmdHandler extends BukkitCommand {
    public CmdHandler() {
        super ("", "", "", new ArrayList<>());
    }
    protected CmdHandler(String name) {
        super(name);
    }
    protected final CommandLineParser parser = new DefaultParser();


    protected CmdHandler(String name, String description, String usageMessage, List<String> aliases) {
        super(name, description, usageMessage, aliases);
    }


}
