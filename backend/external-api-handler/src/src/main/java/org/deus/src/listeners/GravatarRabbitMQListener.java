package org.deus.src.listeners;

import lombok.AllArgsConstructor;

import org.deus.src.dtos.fromModels.UserDTO;
import org.deus.src.exceptions.data.DataProcessingException;
import org.deus.src.exceptions.data.DataSavingException;
import org.deus.src.exceptions.message.MessageSendingException;
import org.deus.src.services.GravatarService;
import org.deus.src.services.RabbitMQService;
import org.deus.src.services.StorageAvatarService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
@AllArgsConstructor
public class GravatarRabbitMQListener {
    private final RabbitMQService rabbitMQService;
    private final GravatarService gravatarService;
    private final StorageAvatarService storageAvatarService;
    private static final Logger logger = LoggerFactory.getLogger(GravatarRabbitMQListener.class);

    @Bean
    public Queue convertAvatar() {
        return new Queue("convert.avatar", true);
    }

    @RabbitListener(queues = "user.register")
    public void userRegister(Message message) {
        Optional<UserDTO> optionalUserDTO = this.rabbitMQService.receiveUserDTO(message);

        if (optionalUserDTO.isEmpty()) {
            logger.error(UserDTO.class.getName() + " was not present when trying to get gravatar's result");
            return;
        }

        UserDTO userDTO = optionalUserDTO.get();
        String username = userDTO.getUsername();

        try {
            String gravatarUrl = this.gravatarService.getGravatarUrl(userDTO.getEmail());
            this.storageAvatarService.gravatarDownloadAndPut(userDTO.getId(), gravatarUrl);
            this.rabbitMQService.sendUserDTO("convert.avatar", userDTO);
        }
        catch (DataProcessingException | DataSavingException e) {
            logger.error("Error while getting gravatar's result", e);
            this.sendErrorMessage(username);
        }
        catch (MessageSendingException e) {
            logger.error("Error while trying to send gravatar's result to microservice for converting", e);
            this.sendErrorMessage(username);
        }
    }

    private void sendErrorMessage(String username) {
        String errorMessage = "Something went wrong while trying to get user's avatar with service gravatar. We'll try later";

        this.rabbitMQService.sendWebsocketMessageDTO(
                "websocket.message.send",
                username,
                "/topic/error",
                errorMessage,
                null);
    }
}
