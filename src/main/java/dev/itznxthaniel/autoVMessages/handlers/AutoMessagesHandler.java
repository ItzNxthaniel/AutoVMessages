package dev.itznxthaniel.autoVMessages.handlers;

import dev.itznxthaniel.autoVMessages.AutoVMessages;
import dev.itznxthaniel.autoVMessages.util.Message;
import dev.itznxthaniel.autoVMessages.util.Server;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class AutoMessagesHandler {
    AutoVMessages plugin;
    HashMap<String, Server> servers = new HashMap<>();
    HashMap<String, Message> autoMessages = new HashMap<>();

    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    Random generator;
    ScheduledFuture<?> taskHandle;
    Runnable announcer;

    public AutoMessagesHandler(AutoVMessages plugin) {
        this.plugin = plugin;
        this.generator = new Random();

        this.setupAnnouncer();
    }

    private void getServers() {
        this.plugin.getServer().getAllServers().forEach(server -> {
            servers.put(server.getServerInfo().getName(), new Server(server));
        });
    }

    private String[] handleConfigServers(ConfigurationNode rootNode, ConfigurationNode configServers) {
        if (AutoVMessages.DEBUG_ENABLED)
            this.plugin.getLogger().info("Processing servers node: " + configServers);

        if (configServers == null || configServers.empty()) {
            this.plugin.getLogger().warn("Servers property for '" + rootNode.key() + "' is invalid or empty. Skipping message.");
            return new String[0]; // Return empty array for invalid server configuration
        }

        // Handle "all" as a special case
        if ("all".equalsIgnoreCase(configServers.getString())) {
            return servers.keySet().toArray(new String[0]);
        }

        // Handle a list of servers
        if (configServers.isList()) {
            List<? extends ConfigurationNode> configServersList = configServers.childrenList();
            return configServersList.stream()
                    .map(ConfigurationNode::getString)
                    .filter(value -> value != null && servers.containsKey(value))
                    .toArray(String[]::new);
        }

        // Handle a single server as a scalar value
        String singleServer = configServers.getString();
        if (singleServer != null && servers.containsKey(singleServer)) {
            return new String[]{singleServer};
        } else if (singleServer != null) {
            this.plugin.getLogger().warn("Server '" + singleServer + "' was not found. Skipping message. These names are case-sensitive.");
        }

        // Default case: invalid or unrecognized servers node
        this.plugin.getLogger().warn("Servers property for '" + rootNode.key() + "' is invalid. Skipping message.");
        return new String[0];
    }


    private String handleConfigMessage(ConfigurationNode rootNode, ConfigurationNode message) {
        if (AutoVMessages.DEBUG_ENABLED)
            this.plugin.getLogger().info("Processing message node: " + message);

        if (message != null && !message.empty()) {
            if (message.isList()) {
                List<? extends ConfigurationNode> configMessages = message.childrenList();
                return configMessages.stream()
                        .map(ConfigurationNode::getString)
                        .filter(Objects::nonNull)
                        .collect(Collectors.joining("\n"));
            } else if (message.getString() != null) {
                return message.getString();
            }
        }

        this.plugin.getLogger().warn("Message property for '" + rootNode.getString() + "' is invalid or empty. Skipping message.");
        return null; // Returning null here to indicate an invalid or missing message
    }

    private void getMessages() {
        ConfigurationNode messagesNode = this.plugin.getConfigHandler().getConfig().node("messages");
        if (messagesNode == null || messagesNode.empty()) {
            this.plugin.getLogger().warn("No 'messages' node found in the configuration.");
            return;
        }

        Map<Object, ? extends ConfigurationNode> configMessages = messagesNode.childrenMap();

        configMessages.forEach((key, configNode) -> {
            String messageId = String.valueOf(key); // Use key as the message ID
            if (AutoVMessages.DEBUG_ENABLED)
                this.plugin.getLogger().info("Processing message ID: " + messageId);

            String[] configServers = handleConfigServers(configNode, configNode.node("servers"));
            String finalMessage = handleConfigMessage(configNode, configNode.node("message"));

            if (messageId != null && configServers.length > 0 && finalMessage != null) {
                autoMessages.put(messageId, new Message(messageId, configServers, finalMessage));
                if (AutoVMessages.DEBUG_ENABLED)
                    this.plugin.getLogger().info("Successfully added message (" + autoMessages.get(messageId).messageId() + "): '" +
                            autoMessages.get(messageId).message() + "', on servers: [" +
                            String.join(", ", autoMessages.get(messageId).servers()) + "]");
            } else {
                this.plugin.getLogger().warn("Message ID '" + messageId + "' could not be processed. Skipping.");
            }
        });
    }

    private void setupTask() {
        announcer = () -> {
            servers.forEach((name, server) -> {
                List<Message> filteredMessages = autoMessages.values().stream()
                        .filter(message -> Arrays.asList(message.servers()).contains(name))
                        .toList();

                if (filteredMessages.size() > 1) {
                    int randomIndex = this.generator.nextInt(filteredMessages.size());

                    while (server.getPreviousMessageId() != null && filteredMessages.get(randomIndex).messageId().equals(server.getPreviousMessageId())) {
                        randomIndex = this.generator.nextInt(filteredMessages.size());
                    }

                    server.setPreviousMessageId(filteredMessages.get(randomIndex).messageId());
                    this.plugin.getMessageHandler().sendAnnouncement(server.getServer(), filteredMessages.get(randomIndex));
                } else {
                    this.plugin.getMessageHandler().sendAnnouncement(server.getServer(), filteredMessages.get(0));

                }
            });
        };

        int interval = this.plugin.getConfigHandler().getConfig().node("interval").getInt();
        taskHandle = scheduler.scheduleAtFixedRate(announcer, interval, interval, TimeUnit.SECONDS);
    }

    private void destroyTask() {
        if (taskHandle != null && !taskHandle.isCancelled()) {
            taskHandle.cancel(true);
            taskHandle = null;
        }
    }

    public void setupAnnouncer() {
        this.getServers();
        this.getMessages();

        if (announcer != null) {
            destroyTask();
        }

        this.setupTask();
    }
}
