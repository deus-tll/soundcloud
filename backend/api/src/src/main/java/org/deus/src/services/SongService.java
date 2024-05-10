package org.deus.src.services;

import lombok.RequiredArgsConstructor;
import org.deus.src.dtos.fromModels.SongDTO;
import org.deus.src.exceptions.StatusException;
import org.deus.src.models.PerformerModel;
import org.deus.src.models.SongModel;
import org.deus.src.models.auth.UserModel;
import org.deus.src.repositories.PerformerRepository;
import org.deus.src.repositories.SongRepository;
import org.deus.src.requests.song.SongCreateRequest;
import org.deus.src.requests.song.SongUpdateRequest;
import org.deus.src.services.auth.UserService;
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

    @CacheEvict(value = "songs", allEntries = true)
    public SongDTO create(SongCreateRequest request) throws StatusException {
        UserModel uploader = userService.getCurrentUser();
        Set<PerformerModel> performers = getPerformersFromIds(request.getPerformerIds());

        if (performers.isEmpty()) {
            throw new StatusException("At least one performer is required", HttpStatus.BAD_REQUEST);
        }


        // process audio file here
        //
        // this.rabbitMQService.sendSongCreateDTO("convert.song", request.getFileId(), uploader.getId());
        //////

        SongModel song = new SongModel();
        song.setName(request.getName());
        song.setUploader(uploader);
        song.setPerformers(performers);

        SongModel savedSong = songRepository.save(song);
        return savedSong.mapToSongDTO();
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
        return updatedSong.mapToSongDTO();
    }

    @Cacheable(value = "performers", key = "#id")
    public SongDTO getById(Long id) throws StatusException {
        Optional<SongModel> optionalSongModel = songRepository.findById(id);
        return optionalSongModel.map(SongModel::mapToSongDTO).orElseThrow(() -> new StatusException("Song not found with id: " + id, HttpStatus.NOT_FOUND));
    }

    @Cacheable(value = "performers", key = "#pageable.pageNumber")
    public Page<SongDTO> getAll(Pageable pageable) {
        Page<SongModel> songs = songRepository.findAll(pageable);
        return songs.map(SongModel::mapToSongDTO);
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