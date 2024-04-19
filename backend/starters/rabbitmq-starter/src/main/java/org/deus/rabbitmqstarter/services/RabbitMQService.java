package org.deus.rabbitmqstarter.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.deus.dataobjectslayer.dtos.websocket.PayloadDTO;
import org.deus.dataobjectslayer.dtos.websocket.WebsocketMessageDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class RabbitMQService {
    private final RabbitTemplate rabbitTemplate;
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQService.class);

    public void sendWebsocketMessage(String queueName, String websocketDestination, String payloadMessage, Object payloadData) {
        PayloadDTO payloadDTO = new PayloadDTO(payloadMessage, payloadData);
        WebsocketMessageDTO websocketMessageDTO = new WebsocketMessageDTO(websocketDestination, payloadDTO);

        try {
            rabbitTemplate.convertAndSend(queueName, websocketMessageDTO.toJson());
        }
        catch (Exception e) {
            logger.error("Error while sending message to \"Websocket-Messenger Microservice\" via RabbitMQ", e);
        }
    }

    public Optional<WebsocketMessageDTO> receiveWebsocketMessage(Message message) {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = new String(message.getBody());
        WebsocketMessageDTO websocketMessageDTO = null;

        try {
            websocketMessageDTO = objectMapper.readValue(json, WebsocketMessageDTO.class);
        }
        catch (JsonProcessingException e) {
            logger.error("Error while trying to convert message back from json", e);
        }

        return Optional.ofNullable(websocketMessageDTO);
    }
}
