package org.deus.src.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.deus.src.dtos.fromModels.PerformerDTO;
import org.deus.src.dtos.fromModels.UserDTO;
import org.deus.src.dtos.helpers.PerformerPhotoConvertingDTO;
import org.deus.src.dtos.helpers.SongConvertingDTO;
import org.deus.src.dtos.helpers.SongCoverConvertingDTO;
import org.deus.src.dtos.websocket.PayloadDTO;
import org.deus.src.dtos.websocket.WebsocketMessageDTO;
import org.deus.src.exceptions.message.MessageSendingException;
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
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQService.class);

    private <Data, TClass> void serializeAndSendMessage(String queueName, Data data, Class<TClass> targetClass) throws MessageSendingException {
        try {
            String json = objectMapper.writeValueAsString(data);
            rabbitTemplate.convertAndSend(queueName, json);
        }
        catch (JsonProcessingException e) {
            String errorMessage = "Error while sending " + targetClass.getName() + " via RabbitMQ to queue \"" + queueName + "\"";
            logger.error(errorMessage, e);
            throw new MessageSendingException(errorMessage, e);
        }
    }

    private <T> Optional<T> deserializeMessage(Message message, Class<T> targetClass) {
        try {
            String json = new String(message.getBody());
            T object = objectMapper.readValue(json, targetClass);
            return Optional.of(object);
        }
        catch (JsonProcessingException e) {
            logger.error("Error while trying to convert message back from json", e);
            return Optional.empty();
        }
    }

    public void sendWebsocketMessageDTO(String queueName, String username, String websocketDestination, String payloadMessage, Object payloadData) {
        PayloadDTO payloadDTO = new PayloadDTO(payloadMessage, payloadData);
        WebsocketMessageDTO websocketMessageDTO = new WebsocketMessageDTO(websocketDestination, username, payloadDTO);

        try {
            this.serializeAndSendMessage(queueName, websocketMessageDTO, WebsocketMessageDTO.class);
        } catch (MessageSendingException e) {
            logger.error("Something went wrong", e);
        }
    }

    public Optional<WebsocketMessageDTO> receiveWebsocketMessageDTO(Message message) {
        return this.deserializeMessage(message, WebsocketMessageDTO.class);
    }

    public void sendUserDTO(String queueName, UserDTO userDTO) throws MessageSendingException {
        this.serializeAndSendMessage(queueName, userDTO, UserDTO.class);
    }

    public Optional<UserDTO> receiveUserDTO(Message message) {
        return this.deserializeMessage(message, UserDTO.class);
    }

    public void sendPerformerPhotoConvertingDTO(String queueName, PerformerPhotoConvertingDTO performerPhotoConvertingDTO) throws MessageSendingException {
        this.serializeAndSendMessage(queueName, performerPhotoConvertingDTO, PerformerDTO.class);
    }

    public Optional<PerformerPhotoConvertingDTO> receivePerformerPhotoConvertingDTO(Message message) {
        return this.deserializeMessage(message, PerformerPhotoConvertingDTO.class);
    }

    public void sendSongCoverConvertingDTO(String queueName, SongCoverConvertingDTO songCoverConvertingDTO) throws MessageSendingException {
        this.serializeAndSendMessage(queueName, songCoverConvertingDTO, PerformerDTO.class);
    }

    public Optional<SongCoverConvertingDTO> receiveSongCoverConvertingDTO(Message message) {
        return this.deserializeMessage(message, SongCoverConvertingDTO.class);
    }

    public void sendSongConvertingDTO(String queueName, SongConvertingDTO songConvertingDTO) throws MessageSendingException {
        this.serializeAndSendMessage(queueName, songConvertingDTO, SongConvertingDTO.class);
    }

    public Optional<SongConvertingDTO> receiveSongConvertingDTO(Message message) {
        return this.deserializeMessage(message, SongConvertingDTO.class);
    }
}
