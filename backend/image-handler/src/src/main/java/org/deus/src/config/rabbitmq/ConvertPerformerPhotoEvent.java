package org.deus.src.config.rabbitmq;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConvertPerformerPhotoEvent {
    @Bean
    public Queue convertPerformerPhoto() {
        return new Queue("convert.performer_photo", true);
    }
}
