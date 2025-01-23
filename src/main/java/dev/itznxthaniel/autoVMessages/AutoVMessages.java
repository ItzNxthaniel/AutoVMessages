package dev.itznxthaniel.autoVMessages;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.itznxthaniel.autoVMessages.handlers.CommandHandler;
import dev.itznxthaniel.autoVMessages.handlers.ConfigHandler;
import dev.itznxthaniel.autoVMessages.handlers.DataHandler;
import dev.itznxthaniel.autoVMessages.handlers.MessageHandler;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.slf4j.Logger;
import org.slf4j.event.Level;
import org.slf4j.event.LoggingEvent;
import org.slf4j.spi.LoggingEventBuilder;

import java.nio.file.Path;

@Getter
@Plugin(
        id = "autovmessages",
        name = "AutoVMessages",
        version = "1.0.0-SNAPSHOT",
        description = "A Velocity plugin to setup automatic messages for your proxy.",
        authors = {"ItzNxthaniel"}
)
public class AutoVMessages {

    @Getter
    private static AutoVMessages instance;

    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;
    private CommandHandler commandHandler;
    private DataHandler dataHandler;
    @Getter @Setter
    private ConfigHandler configHandler;
    @Getter @Setter
    private MessageHandler messageHandler;

    @Inject
    public AutoVMessages(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        instance = this;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        this.commandHandler = new CommandHandler(this);
        this.dataHandler = new DataHandler(this);

        logger.info("AutoVMessages has been initialized. Automatic messages will begin in " +
                this.configHandler.getConfig().node("interval").getString() + " seconds.");
    }

    public void reload() {
        this.getConfigHandler().loadConfig(this);
        this.getMessageHandler().loadLang(this);
    }
}
