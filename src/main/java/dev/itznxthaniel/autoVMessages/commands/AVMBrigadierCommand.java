package dev.itznxthaniel.autoVMessages.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.itznxthaniel.autoVMessages.AutoVMessages;
import net.kyori.adventure.text.Component;

public final class AVMBrigadierCommand {
    public static BrigadierCommand createBrigadierCommand(final ProxyServer proxy) {
        LiteralCommandNode<CommandSource> avmNode = BrigadierCommand.literalArgumentBuilder("autovmessages")
                .executes(context -> {
                    CommandSource source = context.getSource();

                    Component message = AutoVMessages.getInstance().getMiniMessage().deserialize(
                            "<#202020>[<#CC3333>AutoVMessage<#202020>] "
                    );
                    source.sendMessage(message);

                    return Command.SINGLE_SUCCESS;
                })
                .then(BrigadierCommand.requiredArgumentBuilder("argument", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            CommandSource source = ctx.getSource();

                            AutoVMessages.getInstance().getCommandHandler().getCommands().forEach((s, iavmCommand) -> {
                                if (iavmCommand.getPermissionNode() != null && source.hasPermission(iavmCommand.getPermissionNode())) {
                                    builder.suggest(s);
                                }
                            });
                            return builder.buildFuture();
                        })
                        .executes(context -> {
                            String argumentProvided = context.getArgument("argument", String.class);

                            IAVMCommand command = AutoVMessages.getInstance().getCommandHandler().getCommands().get(argumentProvided);
                            if (command != null) {
                                AutoVMessages.getInstance().getCommandHandler().handleCommand(command, context);
                            }

                            return Command.SINGLE_SUCCESS;
                        })
                )
                .build();
        return new BrigadierCommand(avmNode);
    }
}
