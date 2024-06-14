package org.deus.src.services.forModels;

import lombok.RequiredArgsConstructor;
import org.deus.src.dtos.fromModels.SongDTO;
import org.deus.src.dtos.helpers.SongConvertingDTO;
import org.deus.src.dtos.helpers.SongCoverConvertingDTO;
import org.deus.src.exceptions.StatusException;
import org.deus.src.exceptions.data.DataSavingException;
import org.deus.src.exceptions.message.MessageSendingException;
import org.deus.src.models.PerformerModel;
import org.deus.src.models.SongModel;
import org.deus.src.models.auth.UserModel;
import org.deus.src.repositories.PerformerRepository;
import org.deus.src.repositories.SongRepository;
import org.deus.src.requests.song.SongCreateRequest;
import org.deus.src.requests.song.SongStatusRequest;
import org.deus.src.requests.song.SongUpdateRequest;
import org.deus.src.services.RabbitMQService;
import org.deus.src.services.auth.UserService;
import org.deus.src.services.storage.StorageSongCoverService;
import org.deus.src.services.storage.StorageSongService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SongService {
    private final SongRepository songRepository;
    private final PerformerRepository performerRepository;
    private final RabbitMQService rabbitMQService;
    private final UserService userService;
    private final StorageSongService storageSongService;
    private final StorageSongCoverService storageSongCoverService;
    private static final Logger logger = LoggerFactory.getLogger(SongService.class);

    @CacheEvict(value = "songs", allEntries = true)
    public SongDTO create(SongCreateRequest request) throws StatusException {
        UserModel uploader = userService.getCurrentUser();

        System.out.println(request.getPerformerIds());

        Set<PerformerModel> performers = getPerformersFromIds(request.getPerformerIds());

        if (performers.isEmpty()) {
            throw new StatusException("At least one performer is required", HttpStatus.BAD_REQUEST);
        }

        SongModel song = new SongModel();
        song.setName(request.getName());
        song.setUploader(uploader);
        song.setPerformers(performers);

        SongModel savedSong = null;

        try {
            savedSong = songRepository.save(song);
        }
        catch (Exception e) {
            throw new StatusException("Couldn't create object of song in database", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        SongConvertingDTO songConvertingDTO = new SongConvertingDTO(uploader.getId(), savedSong.getId(), request.getFileId(), uploader.getUsername());

        convertSongCover(uploader, savedSong.getId(), request.getCover());

        try {
            this.rabbitMQService.sendSongConvertingDTO("convert.song", songConvertingDTO);
        } catch (MessageSendingException e) {
            this.rabbitMQService.sendWebsocketMessageDTO(
                    "websocket.message.send",
                    uploader.getUsername(),
                    "/topic/error",
                    "Some error occurred while sending your song for preparation",
                    null);
        }

        return savedSong.mapToSongDTO(this.getSongUrl(savedSong.getId()), storageSongCoverService.getPathToFile(savedSong.getId()));
    }

    @CacheEvict(value = "songs", allEntries = true)
    public SongDTO update(Long id, SongUpdateRequest request) throws StatusException {
        SongModel song = songRepository.findById(id).orElseThrow(() -> new StatusException("Song not found with id: " + id, HttpStatus.NOT_FOUND));
        UserModel uploader = userService.getCurrentUser();

        if (request.getName() != null && !request.getName().isEmpty()) {
            song.setName(request.getName());
        }

        if (request.getPerformerIds() != null && !request.getPerformerIds().isEmpty()) {
            Set<PerformerModel> performers = getPerformersFromIds(request.getPerformerIds());
            if (performers.isEmpty()) {
                throw new StatusException("At least one performer is required for updating the song", HttpStatus.BAD_REQUEST);
            }
            song.setPerformers(performers);
        }

        if (request.getFileId() != null && !request.getFileId().isEmpty()) {
            SongConvertingDTO songConvertingDTO = new SongConvertingDTO(uploader.getId(), song.getId(), request.getFileId(), uploader.getUsername());
            try {
                this.rabbitMQService.sendSongConvertingDTO("convert.song", songConvertingDTO);
            } catch (MessageSendingException e) {
                this.rabbitMQService.sendWebsocketMessageDTO(
                        "websocket.message.send",
                        uploader.getUsername(),
                        "/topic/error",
                        "Some error occurred while sending your song for preparation",
                        null);
            }
        }

        SongModel updatedSong = songRepository.save(song);

        Long songId = song.getId();

        if (request.getCover() != null) {
            convertSongCover(uploader, songId, request.getCover());
        }

        return updatedSong.mapToSongDTO(this.getSongUrl(songId), storageSongCoverService.getPathToFile(songId));
    }

    private void convertSongCover(UserModel uploader, Long songId, MultipartFile cover) {
        try {
            storageSongCoverService.putOriginalBytes(songId, cover.getBytes());

            rabbitMQService.sendSongCoverConvertingDTO("convert.song_cover", new SongCoverConvertingDTO(songId, uploader.getUsername()));
        }
        catch (IOException | DataSavingException | MessageSendingException e) {
            String message = "Failed to upload song cover file! Try later with update.";
            logger.error(message, e);
            this.rabbitMQService.sendWebsocketMessageDTO(
                    "websocket.message.send",
                    uploader.getUsername(),
                    "/topic/error",
                    message,
                    null);
        }
    }

    @Cacheable(value = "songs", key = "#id")
    public SongDTO getById(Long id) throws StatusException {
        Optional<SongModel> optionalSongModel = songRepository.findById(id);
        return optionalSongModel.map(songModel -> songModel.mapToSongDTO(this.getSongUrl(songModel.getId()), storageSongCoverService.getPathToFile(songModel.getId()))).orElseThrow(() -> new StatusException("Song not found with id: " + id, HttpStatus.NOT_FOUND));
    }

    @Cacheable(value = "songs", key = "#pageable")
    public Page<SongDTO> getAll(Pageable pageable) {
        Page<SongModel> songs = songRepository.findAll(pageable);
        return songs.map(songModel -> songModel.mapToSongDTO(this.getSongUrl(songModel.getId()), storageSongCoverService.getPathToFile(songModel.getId())));
    }

    @CacheEvict(value = "songs", allEntries = true)
    public void deleteById(Long id) throws StatusException {
        if (!songRepository.existsById(id)) {
            throw new StatusException("Song not found with id: " + id, HttpStatus.NOT_FOUND);
        }

        songRepository.deleteById(id);
    }

    @CacheEvict(value = "songs", allEntries = true)
    public void updateStatus(Long id, SongStatusRequest request) throws StatusException {
        SongModel song = songRepository.findById(id)
                .orElseThrow(() -> new StatusException("Song not found with id: " + id, HttpStatus.NOT_FOUND));

        song.setStatus(request.getStatus());
        songRepository.save(song);
    }

    private Set<PerformerModel> getPerformersFromIds(Set<Long> performerIds) {
        Set<PerformerModel> performers = new HashSet<>();
        if (performerIds != null) {
            for (Long performerId : performerIds) {
                System.out.println("performerId: " + performerId);
                Optional<PerformerModel> performerOptional = performerRepository.findById(performerId);
                performerOptional.ifPresent(performers::add);
            }
        }

        return performers;
    }

    private String getSongUrl(Long songId) {
        String songFileType = ".aac";
        return storageSongService.getPathToSong(songId, songFileType);
    }
}