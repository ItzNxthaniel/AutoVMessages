package dev.itznxthaniel.autoVMessages;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.itznxthaniel.autoVMessages.handlers.*;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;

import java.nio.file.Path;

@Getter
@Plugin(
        id = "itznxthaniel_velocityid",
        name = "itznxthaniel_velocityname",
        version = "itznxthaniel_velocityversion",
        description = "itznxthaniel_velocitydescription",
        authors = {"itznxthaniel_velocityauthor"}
)
public class AutoVMessages {

    public static final String PLUGIN_NAME = "AutoVMessages";
    public static boolean DEBUG_ENABLED = false;

    @Getter
    private static AutoVMessages instance;

    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;
    private CommandHandler commandHandler;
    private DataHandler dataHandler;
    @Setter
    private ConfigHandler configHandler;
    @Setter
    private MessageHandler messageHandler;
    private AutoMessagesHandler autoMessagesHandler;

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

        this.autoMessagesHandler = new AutoMessagesHandler(this);

        logger.info("AutoVMessages has been initialized. Automatic messages will begin in " +
                this.configHandler.getConfig().node("interval").getString() + " seconds.");
    }

    public boolean reload() {
        this.getConfigHandler().loadConfig(this);
        this.getMessageHandler().loadLang(this);
        this.getAutoMessagesHandler().setupAnnouncer();

        return true;
    }
}
