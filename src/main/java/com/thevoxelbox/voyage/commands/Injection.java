package com.thevoxelbox.voyage.commands;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.thevoxelbox.voyage.configuration.InjectorConfig;

public interface Injection {
    Injector injector = Guice.createInjector(new InjectorConfig());
}
