package org.deus.src.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class WebsocketMessageSendEvent {
    @Bean
    public Queue websocketMessageSend() {
        return new Queue("websocket.message.send", true);
    }
}
