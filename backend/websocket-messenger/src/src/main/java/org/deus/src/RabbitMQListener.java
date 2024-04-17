package org.deus.src;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.deus.src.dtos.WebsocketMessageDTO;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.NoSuchAlgorithmException;

@Configuration
@AllArgsConstructor
public class RabbitMQListener {
    private final SimpMessagingTemplate messagingTemplate;

    @RabbitListener(queues = "websocket.message.sent")
    public void websocketMessageSent(Message message) throws JsonProcessingException, NoSuchAlgorithmException {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = new String(message.getBody());

        WebsocketMessageDTO websocketMessageDTO = objectMapper.readValue(json, WebsocketMessageDTO.class);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        messagingTemplate.convertAndSendToUser(
                username,
                websocketMessageDTO.getDestination(),
                websocketMessageDTO.getPayload()
        );
    }
}
