package org.deus.src.controllers.song;

import lombok.RequiredArgsConstructor;
import org.deus.src.dtos.fromModels.SongDTO;
import org.deus.src.exceptions.StatusException;
import org.deus.src.requests.song.SongCreateRequest;
import org.deus.src.requests.song.SongUpdateRequest;
import org.deus.src.services.SongService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/songs")
@RequiredArgsConstructor
public class SongController {
    private final SongService songService;

    @PostMapping
    public ResponseEntity<SongDTO> createSong(@RequestBody SongCreateRequest request) throws StatusException {
        SongDTO songDTO = this.songService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(songDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SongDTO> updateSong(@PathVariable Long id, @RequestBody SongUpdateRequest request) throws StatusException {
        SongDTO songDTO = this.songService.update(id, request);
        return ResponseEntity.ok(songDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SongDTO> getSongById(@PathVariable Long id) throws StatusException {
        SongDTO songDTO = this.songService.getById(id);
        return ResponseEntity.ok(songDTO);
    }

    @GetMapping
    public ResponseEntity<Page<SongDTO>> getAllSongs(Pageable pageable) {
        Page<SongDTO> songs = this.songService.getAll(pageable);
        return ResponseEntity.ok(songs);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSong(@PathVariable Long id) throws StatusException {
        this.songService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}