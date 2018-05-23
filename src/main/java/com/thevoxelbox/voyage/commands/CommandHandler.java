package com.thevoxelbox.voyage.commands;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.bukkit.command.defaults.BukkitCommand;

import java.util.ArrayList;
import java.util.List;

public abstract class CommandHandler extends BukkitCommand {
    CommandLineParser parser = new DefaultParser();
    public CommandHandler() {
        super ("", "", "", new ArrayList<>());
    }
    protected CommandHandler(String name) {
        super(name);
    }

    protected CommandHandler(String name, String description, String usageMessage, List<String> aliases) {
        super(name, description, usageMessage, aliases);
    }

    protected List<String> getHelp(Options options) {
        List<String> list = new ArrayList<>();

        options.getOptions().forEach(o->{
            if (o.hasArg() || o.hasOptionalArg()) {
                list.add("§4arg name&A: §3" + o.getArgName() + " §4has args&A:§3 " + o.hasArg() + " §4required&A:§3" + o.hasOptionalArg() + " " + o.getDescription());
            } else {
                list.add("§4arg name&A: §3" + o.getArgName() + " §4has args&A:§3 " + o.hasArg() + " §4required&A:§3" + o.hasOptionalArg() + " " + o.getDescription());
            }
        });

        return list;
    }
}
