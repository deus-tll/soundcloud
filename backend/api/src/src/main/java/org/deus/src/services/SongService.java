package org.deus.src.services;

import lombok.RequiredArgsConstructor;
import org.deus.datalayerstarter.dtos.SongDTO;
import org.deus.src.models.SongModel;
import org.deus.src.repositories.SongRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SongService {
    private final SongRepository songRepository;

    public SongDTO getSongById(Long id) {
        Optional<SongModel> optionalSongModel = songRepository.findById(id);
        return optionalSongModel.map(SongModel::mapToSongDTO).orElse(null);
    }

    public List<SongDTO> getAllSongs() {
        List<SongModel> songs = songRepository.findAll();

        return songs.stream()
                .map(SongModel::mapToSongDTO)
                .collect(Collectors.toList());
    }
}