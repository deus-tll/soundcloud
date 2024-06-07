package org.deus.src.listeners;

import lombok.RequiredArgsConstructor;
import org.deus.src.dtos.fromModels.UserDTO;
import org.deus.src.exceptions.data.DataIsNotPresentException;
import org.deus.src.exceptions.data.DataProcessingException;
import org.deus.src.services.converters.ConvertAvatarService;
import org.deus.src.services.RabbitMQService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
@RequiredArgsConstructor
public class ConvertAvatarRabbitMQListener {
    private final RabbitMQService rabbitMQService;
    private final ConvertAvatarService convertAvatarService;
    private static final Logger logger = LoggerFactory.getLogger(ConvertAvatarRabbitMQListener.class);

    @RabbitListener(queues = "convert.avatar")
    public void convertAvatar(Message message) {
        Optional<UserDTO> optionalUserDTO = this.rabbitMQService.receiveUserDTO(message);

        if (optionalUserDTO.isEmpty()) {
            logger.error(UserDTO.class.getName() + " was not present when trying to convert avatar");
            return;
        }

        UserDTO userDTO = optionalUserDTO.get();

        try {
            int targetWidth = 300;
            int targetHeight = 300;

            this.convertAvatarService.convertAvatar(userDTO.getId(), targetWidth, targetHeight);

            this.rabbitMQService.sendWebsocketMessageDTO(
                    "websocket.message.send",
                    userDTO.getUsername(),
                    "/topic/avatar.ready",
                    "Your avatar is ready!",
                    null);
        }
        catch (DataIsNotPresentException | DataProcessingException e) {
            logger.error("Some problems have occurred while trying to convert avatar for user with id \"" + userDTO.getId() + "\"", e);

            this.rabbitMQService.sendWebsocketMessageDTO(
                    "websocket.message.send",
                    userDTO.getUsername(),
                    "/topic/error",
                    "Something went wrong while trying to prepare user's avatar. Please try later",
                    null);
        }
    }
}