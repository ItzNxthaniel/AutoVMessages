package dev.itznxthaniel.autoVMessages.util;

import com.velocitypowered.api.proxy.server.RegisteredServer;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Server {
    @Setter
    private String previousMessageId = null;
    private final RegisteredServer server;

    public Server(RegisteredServer server) {
        this.server = server;
    }
}
