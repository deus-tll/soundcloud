package org.deus.src.listeners;

import lombok.RequiredArgsConstructor;
import org.deus.src.dtos.creatings.SongCreatingDTO;
import org.deus.src.exceptions.data.DataIsNotPresentException;
import org.deus.src.exceptions.data.DataProcessingException;
import org.deus.src.services.ConvertSongService;
import org.deus.src.services.RabbitMQService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
@RequiredArgsConstructor
public class ConvertSongRabbitMQListener {
    private final RabbitMQService rabbitMQService;
    private final ConvertSongService convertSongService;

    private static final Logger logger = LoggerFactory.getLogger(ConvertSongRabbitMQListener.class);

    @RabbitListener(queues = "convert.song")
    public void convertSong(Message message) {
        Optional<SongCreatingDTO> optionalSongCreatingDTO = this.rabbitMQService.receiveSongCreatingDTO(message);

        if (optionalSongCreatingDTO.isEmpty()) {
            logger.error(SongCreatingDTO.class.getName() + " was not present when trying to convert song");
            return;
        }

        SongCreatingDTO songCreatingDTO = optionalSongCreatingDTO.get();

        try {
            this.convertSongService.convertSong(songCreatingDTO.getUserId(), songCreatingDTO.getSongId(), songCreatingDTO.getFileId());

            //http request to change song status here
            //
            //////

            this.rabbitMQService.sendWebsocketMessageDTO(
                    "websocket.message.send",
                    songCreatingDTO.getUploaderUsername(),
                    "/topic/song.ready." + songCreatingDTO.getSongId(),
                    "Your song is ready!",
                    null);
        }
        catch (DataIsNotPresentException | DataProcessingException e) {
            logger.error("Some problems have occurred while trying to convert song with id \"" + songCreatingDTO.getSongId() + "\"", e);

            this.rabbitMQService.sendWebsocketMessageDTO(
                    "websocket.message.send",
                    songCreatingDTO.getUploaderUsername(),
                    "/topic/error",
                    "Something went wrong while trying to prepare song. Please try later",
                    null);
        }
    }
}
