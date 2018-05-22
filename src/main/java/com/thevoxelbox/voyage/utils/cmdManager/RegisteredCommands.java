package com.thevoxelbox.voyage.utils.cmdManager;

import com.google.inject.Singleton;
import com.thevoxelbox.voyage.commands.CmdHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;

@Singleton
public class RegisteredCommands {
    protected CommandMap cmap = null;
    private List<RegisteredCommand> registeredCommands = new ArrayList<>();

    public <T extends CmdHandler>RegisteredCommands registerCommand(Supplier<T> regCmd, Plugin plugin) {
        if (regCmd.get().getClass().isAnnotationPresent(Cmd.class)) {
            Cmd reg = regCmd.get().getClass().getAnnotation(Cmd.class);
            T t = regCmd.get();
            t.setName(reg.name());
            t.setAliases(Arrays.asList(reg.aliases()));
            if (regCmd.get().getClass().isAnnotationPresent(Description.class)) {
                Description description = regCmd.get().getClass().getAnnotation(Description.class);
                t.setDescription(description.desc());
            } else {
                t.setDescription("");
            }
            t.setLabel(reg.label());
            t.setPermission(reg.permission());
            t.setUsage(reg.usage());

            try {
                final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");

                bukkitCommandMap.setAccessible(true);
                CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
                plugin.getLogger().log(Level.INFO, "Successfully registered command: " + reg.name());

                if (reg.enabled()) {
                    commandMap.register(reg.name(), t);
                } else {
                    commandMap.register(reg.name(), new DisabledCommand(reg.name()));
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        } else {
            plugin.getLogger().log(Level.WARNING, "Unable to register " + regCmd.get().getClass().getName() + " due to missing registration.");
        }


        return this;
    }
}