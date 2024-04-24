package org.deus.src.listeners;

import lombok.AllArgsConstructor;
import org.deus.datalayerstarter.dtos.auth.UserDTO;
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

        optionalUserId.ifPresentOrElse(userId -> {
            this.convertAvatarService.convertAvatar(userId);
        }, () -> {
            logger.error(UserDTO.class.getName() + " was not present when trying to convert avatar");
        });
    }
}
