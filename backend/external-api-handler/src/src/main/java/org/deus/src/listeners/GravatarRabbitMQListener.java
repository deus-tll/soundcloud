package org.deus.src.listeners;

import lombok.AllArgsConstructor;
import org.deus.datalayerstarter.dtos.auth.UserDTO;
import org.deus.rabbitmqstarter.services.RabbitMQService;
import org.deus.src.services.GravatarService;
import org.deus.storagestarter.services.StorageAvatarService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
@AllArgsConstructor
@ComponentScan(basePackageClasses = {StorageAvatarService.class, RabbitMQService.class})
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

        optionalUserDTO.ifPresentOrElse(userDTO -> {
            try {
                String gravatarUrl = this.gravatarService.getGravatarUrl(userDTO.getEmail());
                this.storageAvatarService.gravatarDownloadAndPut(userDTO.getId(), gravatarUrl);

                this.rabbitMQService.sendUserId("convert.avatar", userDTO.getId());
            }
            catch (RuntimeException e) {
                logger.error("Error while getting gravatar's result", e);
            }
        }, () -> {
            logger.error(UserDTO.class.getName() + " was not present when trying to get gravatar's result");
        });
    }
}
