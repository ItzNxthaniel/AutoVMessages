package dev.itznxthaniel.autoVMessages.commands;

import com.mojang.brigadier.context.CommandContext;
import com.velocitypowered.api.command.CommandSource;
import dev.itznxthaniel.autoVMessages.AutoVMessages;

public interface IAVMCommand {

    default boolean isConsoleAllowed() {
        return false;
    }

    default String getPermissionNode() {
        return null;
    }

    void execute(AutoVMessages plugin, CommandContext<CommandSource> context);

}