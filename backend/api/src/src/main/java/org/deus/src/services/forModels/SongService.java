package org.deus.src.services.forModels;

import lombok.RequiredArgsConstructor;
import org.deus.src.dtos.creatings.SongCreatingDTO;
import org.deus.src.dtos.fromModels.SongDTO;
import org.deus.src.exceptions.StatusException;
import org.deus.src.exceptions.message.MessageSendingException;
import org.deus.src.models.PerformerModel;
import org.deus.src.models.SongModel;
import org.deus.src.models.auth.UserModel;
import org.deus.src.repositories.PerformerRepository;
import org.deus.src.repositories.SongRepository;
import org.deus.src.requests.song.SongCreateRequest;
import org.deus.src.requests.song.SongUpdateRequest;
import org.deus.src.services.RabbitMQService;
import org.deus.src.services.auth.UserService;
import org.deus.src.services.storage.StorageSongService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SongService {
    private final SongRepository songRepository;
    private final PerformerRepository performerRepository;
    private final RabbitMQService rabbitMQService;
    private final UserService userService;
    private final StorageSongService storageSongService;
    private final String songFileType = ".aac";

    @CacheEvict(value = "songs", allEntries = true)
    public SongDTO create(SongCreateRequest request) throws StatusException {
        UserModel uploader = userService.getCurrentUser();
        Set<PerformerModel> performers = getPerformersFromIds(request.getPerformerIds());

        if (performers.isEmpty()) {
            throw new StatusException("At least one performer is required", HttpStatus.BAD_REQUEST);
        }

        SongModel song = new SongModel();
        song.setName(request.getName());
        song.setName(request.getFileId());
        song.setUploader(uploader);
        song.setPerformers(performers);

        SongModel savedSong = null;

        try {
            savedSong = songRepository.save(song);
        }
        catch (Exception e) {
            throw new StatusException("Couldn't create object of song in database", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        SongCreatingDTO songCreatingDTO = new SongCreatingDTO(uploader.getId(), savedSong.getId(), request.getFileId(), uploader.getUsername());

        try {
            this.rabbitMQService.sendSongCreatingDTO("convert.song", songCreatingDTO);
        } catch (MessageSendingException e) {
            this.rabbitMQService.sendWebsocketMessageDTO(
                    "websocket.message.send",
                    uploader.getUsername(),
                    "/topic/error",
                    "Some error occurred while sending your song for preparation",
                    null);
        }

        return savedSong.mapToSongDTO(null);
    }

    @CacheEvict(value = "songs", allEntries = true)
    public SongDTO update(Long id, SongUpdateRequest request) throws StatusException {
        SongModel song = songRepository.findById(id).orElseThrow(() -> new StatusException("Song not found with id: " + id, HttpStatus.NOT_FOUND));

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

        SongModel updatedSong = songRepository.save(song);
        return updatedSong.mapToSongDTO(storageSongService.getPathToSong(updatedSong.getId(), songFileType));
    }

    @Cacheable(value = "performers", key = "#id")
    public SongDTO getById(Long id) throws StatusException {
        Optional<SongModel> optionalSongModel = songRepository.findById(id);
        return optionalSongModel.map(songModel -> songModel.mapToSongDTO(storageSongService.getPathToSong(songModel.getId(), songFileType))).orElseThrow(() -> new StatusException("Song not found with id: " + id, HttpStatus.NOT_FOUND));
    }

    @Cacheable(value = "performers", key = "#pageable")
    public Page<SongDTO> getAll(Pageable pageable) {
        Page<SongModel> songs = songRepository.findAll(pageable);
        return songs.map(songModel -> songModel.mapToSongDTO(storageSongService.getPathToSong(songModel.getId(), songFileType)));
    }

    @CacheEvict(value = "songs", allEntries = true)
    public void deleteById(Long id) throws StatusException {
        if (!songRepository.existsById(id)) {
            throw new StatusException("Song not found with id: " + id, HttpStatus.NOT_FOUND);
        }

        songRepository.deleteById(id);
    }

    private Set<PerformerModel> getPerformersFromIds(Set<Long> performerIds) {
        Set<PerformerModel> performers = new HashSet<>();
        if (performerIds != null) {
            for (Long performerId : performerIds) {
                Optional<PerformerModel> performerOptional = performerRepository.findById(performerId);
                performerOptional.ifPresent(performers::add);
            }
        }

        return performers;
    }
}