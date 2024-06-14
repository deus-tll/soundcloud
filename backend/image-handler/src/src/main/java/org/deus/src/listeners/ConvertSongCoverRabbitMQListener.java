package org.deus.src.listeners;

import lombok.RequiredArgsConstructor;
import org.deus.src.dtos.helpers.PerformerPhotoConvertingDTO;
import org.deus.src.dtos.helpers.SongCoverConvertingDTO;
import org.deus.src.exceptions.data.DataIsNotPresentException;
import org.deus.src.exceptions.data.DataProcessingException;
import org.deus.src.services.RabbitMQService;
import org.deus.src.services.converters.ConvertSongCoverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
@RequiredArgsConstructor
public class ConvertSongCoverRabbitMQListener {
    private final RabbitMQService rabbitMQService;
    private final ConvertSongCoverService convertSongCoverService;
    private static final Logger logger = LoggerFactory.getLogger(ConvertSongCoverRabbitMQListener.class);

    @RabbitListener(queues = "convert.song_cover")
    public void convertSongCover(Message message) {
        Optional<SongCoverConvertingDTO> optionalSongCoverConvertingDTO = this.rabbitMQService.receiveSongCoverConvertingDTO(message);

        if (optionalSongCoverConvertingDTO.isEmpty()) {
            logger.error(SongCoverConvertingDTO.class.getName() + " was not present when trying to convert avatar");
            return;
        }

        SongCoverConvertingDTO songCoverConvertingDTO = optionalSongCoverConvertingDTO.get();

        try {
            int targetWidth = 300;
            int targetHeight = 300;

            this.convertSongCoverService.convertSongCover(songCoverConvertingDTO.getSongId(), targetWidth, targetHeight);

            this.rabbitMQService.sendWebsocketMessageDTO(
                    "websocket.message.send",
                    songCoverConvertingDTO.getUploaderUsername(),
                    "/topic/song_cover.ready",
                    "Song cover is ready!",
                    null);
        }
        catch (DataIsNotPresentException | DataProcessingException e) {
            logger.error("Some problems have occurred while trying to convert song cover for song with id \"" + songCoverConvertingDTO.getSongId() + "\"", e);

            this.rabbitMQService.sendWebsocketMessageDTO(
                    "websocket.message.send",
                    songCoverConvertingDTO.getUploaderUsername(),
                    "/topic/error",
                    "Something went wrong while trying to prepare song cover. Please try later",
                    null);
        }
    }
}
