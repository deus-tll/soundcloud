package org.deus.rabbitmqstarter.config;

import org.deus.rabbitmqstarter.services.RabbitMQService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class RabbitMQServiceAutoConfiguration {
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public RabbitMQServiceAutoConfiguration(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Bean
    public RabbitMQService rabbitMQService() {
        return new RabbitMQService(rabbitTemplate);
    }
}
