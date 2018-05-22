package com.thevoxelbox.voyage.utils.cmdManager;

import org.bukkit.command.CommandMap;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

@Retention(RetentionPolicy.RUNTIME)
public @interface Cmd {
    String name();
    String label();
    String usage();
    String[] aliases();
    boolean enabled();
    String permission();
}
