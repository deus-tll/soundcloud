package org.deus.src.listeners;

import lombok.RequiredArgsConstructor;
import org.deus.src.dtos.helpers.PerformerPhotoConvertingDTO;
import org.deus.src.exceptions.data.DataIsNotPresentException;
import org.deus.src.exceptions.data.DataProcessingException;
import org.deus.src.services.RabbitMQService;
import org.deus.src.services.converters.ConvertPerformerPhotoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
@RequiredArgsConstructor
public class ConvertPerformerPhotoRabbitMQListener {
    private final RabbitMQService rabbitMQService;
    private final ConvertPerformerPhotoService convertPerformerPhotoService;
    private static final Logger logger = LoggerFactory.getLogger(ConvertPerformerPhotoRabbitMQListener.class);

    @RabbitListener(queues = "convert.performer_photo")
    public void convertPerformerPhoto(Message message) {
        Optional<PerformerPhotoConvertingDTO> optionalPerformerPhotoConvertingDTO = this.rabbitMQService.receivePerformerPhotoConvertingDTO(message);

        if (optionalPerformerPhotoConvertingDTO.isEmpty()) {
            logger.error(PerformerPhotoConvertingDTO.class.getName() + " was not present when trying to convert avatar");
            return;
        }

        PerformerPhotoConvertingDTO performerPhotoConvertingDTO = optionalPerformerPhotoConvertingDTO.get();

        try {
            int targetWidth = 300;
            int targetHeight = 300;

            this.convertPerformerPhotoService.convertPerformerPhoto(performerPhotoConvertingDTO.getPerformerId(), targetWidth, targetHeight);

            this.rabbitMQService.sendWebsocketMessageDTO(
                    "websocket.message.send",
                    performerPhotoConvertingDTO.getUploaderUsername(),
                    "/topic/performer_photo.ready",
                    "Performer's photo is ready!",
                    null);
        }
        catch (DataIsNotPresentException | DataProcessingException e) {
            logger.error("Some problems have occurred while trying to convert performer photo for performer with id \"" + performerPhotoConvertingDTO.getPerformerId() + "\"", e);

            this.rabbitMQService.sendWebsocketMessageDTO(
                    "websocket.message.send",
                    performerPhotoConvertingDTO.getUploaderUsername(),
                    "/topic/error",
                    "Something went wrong while trying to prepare performer's photo. Please try later",
                    null);
        }
    }
}
