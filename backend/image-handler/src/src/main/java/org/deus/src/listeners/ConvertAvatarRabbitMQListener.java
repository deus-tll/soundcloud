package org.deus.src.listeners;

import lombok.AllArgsConstructor;
import org.deus.datalayerstarter.dtos.auth.UserDTO;
import org.deus.datalayerstarter.exceptions.data.DataIsNotPresentException;
import org.deus.datalayerstarter.exceptions.data.DataProcessingException;
import org.deus.datalayerstarter.exceptions.message.MessageSendingException;
import org.deus.rabbitmqstarter.services.RabbitMQService;
import org.deus.src.services.ConvertAvatarService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
@AllArgsConstructor
@ComponentScan(basePackageClasses = {RabbitMQService.class})
public class ConvertAvatarRabbitMQListener {
    private final RabbitMQService rabbitMQService;
    private final ConvertAvatarService convertAvatarService;
    private static final Logger logger = LoggerFactory.getLogger(ConvertAvatarRabbitMQListener.class);

    @RabbitListener(queues = "convert.avatar")
    public void convertAvatar(Message message) {
        Optional<Long> optionalUserId = this.rabbitMQService.receiveUserLongId(message);

        if (optionalUserId.isEmpty()) {
            String errorMessage = "UserId was not present when trying to convert avatar";
            logger.error(errorMessage);
            this.rabbitMQService.sendWebsocketMessageDTO(
                    "websocket.message.send",
                    "/topic/avatar-error",
                    errorMessage,
                    null);

            return;
        }

        Long userId = optionalUserId.get();

        try {
            this.convertAvatarService.convertAvatar(userId);

            this.rabbitMQService.sendWebsocketMessageDTO(
                    "websocket.message.send",
                    "/topic/avatar-ready",
                    "Your avatar is ready!",
                    null);
        }
        catch (DataIsNotPresentException | DataProcessingException e) {
            String errorMessage = "Some problems have occurred while trying to convert avatar";
            logger.error(errorMessage + " for user with id \"" + userId + "\"", e);

            this.rabbitMQService.sendWebsocketMessageDTO(
                    "websocket.message.send",
                    "/topic/avatar-error",
                    errorMessage,
                    null);
        }
    }
}