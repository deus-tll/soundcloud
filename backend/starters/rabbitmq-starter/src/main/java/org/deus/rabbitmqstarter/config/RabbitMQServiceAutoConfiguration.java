package org.deus.rabbitmqstarter.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.deus.rabbitmqstarter.services.RabbitMQService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQServiceAutoConfiguration {
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public RabbitMQServiceAutoConfiguration(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @Bean
    public RabbitMQService rabbitMQService() {
        return new RabbitMQService(rabbitTemplate, objectMapper);
    }
}
