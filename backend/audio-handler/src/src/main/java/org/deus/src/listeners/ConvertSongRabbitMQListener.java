package org.deus.src.listeners;

import lombok.RequiredArgsConstructor;
import org.deus.src.dtos.creatings.SongCreatingDTO;
import org.deus.src.enums.SongStatus;
import org.deus.src.exceptions.data.DataIsNotPresentException;
import org.deus.src.exceptions.data.DataProcessingException;
import org.deus.src.requests.SongStatusRequest;
import org.deus.src.services.ConvertSongService;
import org.deus.src.services.RabbitMQService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Configuration
@RequiredArgsConstructor
public class ConvertSongRabbitMQListener {
    private final RabbitMQService rabbitMQService;
    private final ConvertSongService convertSongService;
    private final RestTemplate restTemplate;
    private static final String UPDATE_SONG_STATUS_URL = "http://soundcloud.api/private-api/songs/%d/status";

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

            boolean isUpdated = updateSongStatus(songCreatingDTO.getSongId(), SongStatus.READY);

            if (isUpdated) {
                this.rabbitMQService.sendWebsocketMessageDTO(
                        "websocket.message.send",
                        songCreatingDTO.getUploaderUsername(),
                        "/topic/song.ready." + songCreatingDTO.getSongId(),
                        "Your song is ready!",
                        null);
            } else {
                logger.error("Failed to update song status to READY for song with id \"" + songCreatingDTO.getSongId() + "\"");
            }
        }
        catch (DataIsNotPresentException | DataProcessingException e) {
            logger.error("Some problems have occurred while trying to convert song with id \"" + songCreatingDTO.getSongId() + "\"", e);

            boolean isUpdated = updateSongStatus(songCreatingDTO.getSongId(), SongStatus.ERROR);

            if (isUpdated) {
                this.rabbitMQService.sendWebsocketMessageDTO(
                        "websocket.message.send",
                        songCreatingDTO.getUploaderUsername(),
                        "/topic/error",
                        "Something went wrong while trying to prepare song. Please try later",
                        null);
            } else {
                logger.error("Failed to update song status to ERROR for song with id \"" + songCreatingDTO.getSongId() + "\"");
            }
        }
    }

    private boolean updateSongStatus(Long songId, SongStatus status) {
        try {
            String url = String.format(UPDATE_SONG_STATUS_URL, songId);

            SongStatusRequest request = new SongStatusRequest();
            request.setStatus(status);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<SongStatusRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<Void> response = restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    entity,
                    Void.class
            );

            return response.getStatusCode().is2xxSuccessful();
        }
        catch (Exception e) {
            logger.error("Failed to update song status for song with id \"" + songId + "\"", e);
            return false;
        }
    }
}
