package dev.itznxthaniel.autoVMessages.commands;

import com.mojang.brigadier.context.CommandContext;
import com.velocitypowered.api.command.CommandSource;
import dev.itznxthaniel.autoVMessages.AutoVMessages;

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

        plugin.getMessageHandler().sendPluginResponse(source, "autovmessages.command.avm.reload-in-progress");

        AutoVMessages.getInstance().reload();
    }
}
