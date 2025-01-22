package dev.itznxthaniel.autoVMessages.commands;

import com.mojang.brigadier.context.CommandContext;
import com.velocitypowered.api.command.CommandSource;
import dev.itznxthaniel.autoVMessages.AutoVMessages;
import net.kyori.adventure.text.Component;

public class ReloadCommand implements IAVMCommand {

    @Override
    public boolean isConsoleAllowed() {
        return true;
    }

    @Override
    public String getPermissionNode() {
        return "autovmessages.reload";
    }

    @Override
    public void execute(AutoVMessages plugin, CommandContext context) {
        CommandSource source = (CommandSource) context.getSource();

        Component parsedMessage = AutoVMessages.getInstance()
                .getMiniMessage()
                .deserialize("<#202020>[<#CC3333>AutoVMessage<#202020>] <gray>Reloading AutoVMessage.");

        source.sendMessage(parsedMessage);

        AutoVMessages.getInstance().reload();
    }
}
