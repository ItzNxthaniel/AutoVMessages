package dev.itznxthaniel.autoVMessages.handlers;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import dev.itznxthaniel.autoVMessages.AutoVMessages;
import dev.itznxthaniel.autoVMessages.util.Message;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Objects;

@Getter
public class MessageHandler {
    private final AutoVMessages plugin;
    private ConfigurationNode lang;
    private final MiniMessage miniMessage;

    public MessageHandler(AutoVMessages plugin) {
        this.plugin = plugin;
        miniMessage = MiniMessage.builder()
                .tags(TagResolver.builder()
                        .resolver(StandardTags.color())
                        .resolver(StandardTags.decorations())
                        .build()
                )
                .build();

        this.loadLang(plugin);
    }

    public void loadLang(AutoVMessages plugin) {
        File langFile = new File(plugin.getDataDirectory().toFile(), "lang.yml");

        if (!langFile.exists()) {
            try (InputStream in = getClass().getClassLoader().getResourceAsStream("lang.yml")) {
                Files.copy(Objects.requireNonNull(in), langFile.toPath());
            } catch (IOException exception) {
                plugin.getLogger().error("Failed to load lang.yml", exception);
            }
        }

        YamlConfigurationLoader yamlConfigurationLoader = YamlConfigurationLoader.builder().file(langFile).build();

        try {
            this.lang = yamlConfigurationLoader.load();
        } catch (IOException exception) {
            plugin.getLogger().error("Failed to load lang.yml", exception);
        }
    }

    private String replaceVariables(String string) {
        string = string.replace("{pluginVersion}",
                this.plugin.getServer().getPluginManager().getPlugin(AutoVMessages.PLUGIN_NAME.toLowerCase()).get()
                        .getDescription().getVersion().get()
        );

        return string;
    }

    public void sendPluginResponse(CommandSource source, String langKey) {
        String prefix = lang.node("autovmessages", "prefix").getString();

        String[] keyParts = langKey.split("\\.");
        String response = lang.node(keyParts).getString();

        response = replaceVariables(response);

        Component deserializedResponse = this.miniMessage.deserialize(prefix + response);

        source.sendMessage(deserializedResponse);
    }

    public void sendAnnouncement(RegisteredServer server, Message message) {
        Component deserializedMessage = this.miniMessage.deserialize(message.message());

        server.sendMessage(deserializedMessage);
    }
}
