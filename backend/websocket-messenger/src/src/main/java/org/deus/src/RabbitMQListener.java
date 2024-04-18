package org.deus.src;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.deus.dataobjectslayer.dtos.websocket.WebsocketMessageDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
@AllArgsConstructor
public class RabbitMQListener {
    private final SimpMessagingTemplate messagingTemplate;
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQListener.class);
    @RabbitListener(queues = "websocket.message.sent")
    public void websocketMessageSent(Message message) {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = new String(message.getBody());

        try {
            WebsocketMessageDTO websocketMessageDTO = objectMapper.readValue(json, WebsocketMessageDTO.class);

            String username = SecurityContextHolder.getContext().getAuthentication().getName();

            messagingTemplate.convertAndSendToUser(
                    username,
                    websocketMessageDTO.getDestination(),
                    websocketMessageDTO.getPayload().toJson()
            );
        }
        catch (Exception e) {
            logger.error("Error while trying to send message via websocket", e);
        }

    }
}
