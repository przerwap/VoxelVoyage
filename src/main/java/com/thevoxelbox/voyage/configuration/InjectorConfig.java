package com.thevoxelbox.voyage.configuration;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.thevoxelbox.voyage.repositories.TransitRepository;
import com.thevoxelbox.voyage.repositories.TransitRepositoryImpl;
import com.thevoxelbox.voyage.repositories.VoyageRepository;
import com.thevoxelbox.voyage.repositories.VoyageRepositoryImpl;

public class InjectorConfig extends AbstractModule {
    @Override
    protected void configure() {
        bind(TransitRepository.class).to(TransitRepositoryImpl.class).in(Scopes.SINGLETON);
        bind(VoyageRepository.class).to(VoyageRepositoryImpl.class).in(Scopes.SINGLETON);
    }
}

