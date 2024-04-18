package org.deus.src.config.rabbitmq;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class WebsocketMessageSentEvent {
    @Bean
    public Queue websocketMessageSent() {
        return new Queue("websocket.message.sent", true);
    }
}
