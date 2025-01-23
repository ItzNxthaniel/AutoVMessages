package dev.itznxthaniel.autoVMessages.handlers;

import dev.itznxthaniel.autoVMessages.AutoVMessages;

public class DataHandler {
    public DataHandler(AutoVMessages plugin) {
        if (!plugin.getDataDirectory().toFile().exists()) plugin.getDataDirectory().toFile().mkdir();

        plugin.setConfigHandler(new ConfigHandler(plugin));
        plugin.setMessageHandler(new MessageHandler(plugin));
    }
}
