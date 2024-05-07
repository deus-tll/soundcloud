package org.deus.src.listeners;

import lombok.AllArgsConstructor;
import org.deus.src.exceptions.data.DataIsNotPresentException;
import org.deus.src.exceptions.data.DataProcessingException;
import org.deus.src.services.ConvertAvatarService;
import org.deus.src.services.RabbitMQService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
@AllArgsConstructor
public class ConvertAvatarRabbitMQListener {
    private final RabbitMQService rabbitMQService;
    private final ConvertAvatarService convertAvatarService;
    private static final Logger logger = LoggerFactory.getLogger(ConvertAvatarRabbitMQListener.class);

    @RabbitListener(queues = "convert.avatar")
    public void convertAvatar(Message message) {
        Optional<Long> optionalUserId = this.rabbitMQService.receiveUserLongId(message);

        if (optionalUserId.isEmpty()) {
            logger.error("UserId was not present when trying to convert avatar");
            this.sendErrorMessage();
            return;
        }

        Long userId = optionalUserId.get();

        try {
            this.convertAvatarService.convertAvatar(userId);
            this.rabbitMQService.sendWebsocketMessageDTO(
                    "websocket.message.send",
                    "/topic/avatar.ready",
                    "Your avatar is ready!",
                    null);
        }
        catch (DataIsNotPresentException | DataProcessingException e) {
            logger.error("Some problems have occurred while trying to convert avatar for user with id \"" + userId + "\"", e);
            this.sendErrorMessage();
        }
    }

    private void sendErrorMessage() {
        String errorMessage = "Something went wrong while trying to prepare user's avatar. Please try later";
        this.rabbitMQService.sendWebsocketMessageDTO(
                "websocket.message.send",
                "/topic/error",
                errorMessage,
                null);
    }
}