package dev.itznxthaniel.autoVMessages;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import org.slf4j.Logger;

@Plugin(id = "autovmessages", name = "AutoVMessages", version = "1.0.0-SNAPSHOT", description = "A Velocity plugin to setup automatic messages for your proxy.", authors = {"ItzNxthaniel"})
public class AutoVMessages {

    @Inject
    private Logger logger;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
    }
}
