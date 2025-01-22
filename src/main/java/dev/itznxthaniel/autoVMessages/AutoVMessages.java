package dev.itznxthaniel.autoVMessages;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.itznxthaniel.autoVMessages.handlers.CommandHandler;
import lombok.Getter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.slf4j.Logger;

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
    private MiniMessage miniMessage;
    private CommandHandler commandHandler;

    @Inject
    public AutoVMessages(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        instance = this;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        logger.info("AutoVMessages init. Setting up.");

        miniMessage = MiniMessage.builder()
                .tags(TagResolver.builder()
                        .resolver(StandardTags.color())
                        .resolver(StandardTags.decorations())
                        .build()
                )
                .build();

        this.commandHandler = new CommandHandler(this);
    }

    public void reload() {
        this.logger.info("RELOAD HERE");
    }
}
