package org.deus.src.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.deus.src.dtos.fromModels.UserDTO;
import org.deus.src.dtos.websocket.PayloadDTO;
import org.deus.src.dtos.websocket.WebsocketMessageDTO;
import org.deus.src.exceptions.message.MessageSendingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class RabbitMQService {
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQService.class);

    public <T> Optional<T> deserializeMessage(Message message, Class<T> targetClass) {
        try {
            String json = new String(message.getBody());
            T object = objectMapper.readValue(json, targetClass);

            return Optional.of(object);
        }
        catch (Exception e) {
            logger.error("Error while trying to convert message back from json", e);

            return Optional.empty();
        }
    }

    public void sendWebsocketMessageDTO(String queueName, String username, String websocketDestination, String payloadMessage, Object payloadData) {
        PayloadDTO payloadDTO = new PayloadDTO(payloadMessage, payloadData);
        WebsocketMessageDTO websocketMessageDTO = new WebsocketMessageDTO(websocketDestination, username, payloadDTO);

        try {
            rabbitTemplate.convertAndSend(queueName, websocketMessageDTO.toJson());
        }
        catch (Exception e) {
            logger.error("Error while sending WebsocketMessageDTO via RabbitMQ to queue \"" + queueName + "\"", e);
        }
    }

    public Optional<WebsocketMessageDTO> receiveWebsocketMessageDTO(Message message) {
        return deserializeMessage(message, WebsocketMessageDTO.class);
    }

    public void sendUserDTO(String queueName, UserDTO userDTO) throws MessageSendingException {
        try {
            rabbitTemplate.convertAndSend(queueName, userDTO.toJson());
        }
        catch (Exception e) {
            String errorMessage = "Error while sending UserDTO via RabbitMQ to queue \"" + queueName + "\"";
            logger.error(errorMessage, e);
            throw new MessageSendingException(errorMessage, e);
        }
    }

    public Optional<UserDTO> receiveUserDTO(Message message) {
        return deserializeMessage(message, UserDTO.class);
    }

    public void sendUserId(String queueName, Long id) throws MessageSendingException {
        try {
            rabbitTemplate.convertAndSend(queueName, id);
        }
        catch (AmqpException e) {
            String errorMessage = "Error while sending UserId(Long) via RabbitMQ to queue \"" + queueName + "\"";
            logger.error(errorMessage, e);
            throw new MessageSendingException(errorMessage, e);
        }
    }

    public Optional<Long> receiveUserLongId(Message message) {
        try {
            String userIdString = new String(message.getBody());
            Long userId = Long.parseLong(userIdString);

            return Optional.of(userId);
        } catch (NumberFormatException e) {
            logger.error("Error while parsing UserId(Long) from Message body", e);

            return Optional.empty();
        }
    }
}
