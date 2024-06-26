package org.deus.src;

import lombok.AllArgsConstructor;
import org.deus.src.dtos.websocket.WebsocketMessageDTO;
import org.deus.src.services.RabbitMQService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Optional;

@Configuration
@AllArgsConstructor
public class RabbitMQListener {
    private final SimpMessagingTemplate messagingTemplate;
    private final RabbitMQService rabbitMQService;
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQListener.class);
    @RabbitListener(queues = "websocket.message.send")
    public void websocketMessageSend(Message message) {
        Optional<WebsocketMessageDTO> optionalWebsocketMessageDTO = this.rabbitMQService.receiveWebsocketMessageDTO(message);

        if (optionalWebsocketMessageDTO.isEmpty()) {
            logger.error(WebsocketMessageDTO.class.getName() + " was not present when trying to send message via websocket");
            return;
        }

        WebsocketMessageDTO websocketMessageDTO = optionalWebsocketMessageDTO.get();

        try {
            messagingTemplate.convertAndSendToUser(
                    websocketMessageDTO.getUsername(),
                    websocketMessageDTO.getDestination(),
                    websocketMessageDTO.getPayload().toJson()
            );
        } catch (Exception e) {
            logger.error("Error while trying to send message via websocket", e);
        }
    }
}
