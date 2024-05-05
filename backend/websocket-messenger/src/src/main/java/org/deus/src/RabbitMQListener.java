package org.deus.src;

import lombok.AllArgsConstructor;
import org.deus.datalayerstarter.dtos.websocket.WebsocketMessageDTO;
import org.deus.rabbitmqstarter.services.RabbitMQService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@AllArgsConstructor
@ComponentScan(basePackageClasses = {RabbitMQService.class})
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

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        WebsocketMessageDTO websocketMessageDTO = optionalWebsocketMessageDTO.get();

        try {
            messagingTemplate.convertAndSendToUser(
                    username,
                    websocketMessageDTO.getDestination(),
                    websocketMessageDTO.getPayload().toJson()
            );
        } catch (Exception e) {
            logger.error("Error while trying to send message via websocket", e);
        }
    }
}
