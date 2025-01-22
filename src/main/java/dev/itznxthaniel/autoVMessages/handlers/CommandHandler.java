package dev.itznxthaniel.autoVMessages.handlers;

import com.mojang.brigadier.context.CommandContext;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import dev.itznxthaniel.autoVMessages.AutoVMessages;
import dev.itznxthaniel.autoVMessages.commands.AVMBrigadierCommand;
import dev.itznxthaniel.autoVMessages.commands.IAVMCommand;
import dev.itznxthaniel.autoVMessages.commands.ReloadCommand;
import lombok.Getter;
import net.kyori.adventure.text.Component;

import java.util.HashMap;

public class CommandHandler {
    private final CommandManager commandManager;

    @Getter
    private final HashMap<String, IAVMCommand> commands = new HashMap<>();

    public CommandHandler(AutoVMessages plugin) {
        this.commandManager = plugin.getServer().getCommandManager();

        initCommands();
        registerAVMCommand();
    }

    private void initCommands() {
        commands.put("reload", new ReloadCommand());
    }

    private void registerAVMCommand() {
        CommandMeta commandMeta = this.commandManager.metaBuilder("autovmessages")
                .aliases("autovmessage", "avm")
                .plugin(AutoVMessages.getInstance())
                .build();

        BrigadierCommand command = AVMBrigadierCommand.createBrigadierCommand(AutoVMessages.getInstance().getServer());

        commandManager.register(commandMeta, command);
    }

    public void handleCommand(IAVMCommand command, CommandContext context) {
        if (context.getSource() instanceof ConsoleCommandSource && command.isConsoleAllowed()) {
            command.execute(AutoVMessages.getInstance(), context);
        } else if (context.getSource() instanceof Player player && command.getPermissionNode() != null) {
            if (player.hasPermission(command.getPermissionNode())) {
                command.execute(AutoVMessages.getInstance(), context);
            } else {
                Component message = AutoVMessages.getInstance().getMiniMessage().deserialize(
                        "<#202020>[<#CC3333>AutoVMessage<#202020>] <gray>You do not have permission for this command."
                );

                player.sendMessage(message);
            }
        } else {
            command.execute(AutoVMessages.getInstance(), context);
        }
    }
}
