package org.deus.src.controllers.song;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.deus.src.dtos.fromModels.SongDTO;
import org.deus.src.exceptions.StatusException;
import org.deus.src.requests.song.SongCreateRequest;
import org.deus.src.requests.song.SongUpdateRequest;
import org.deus.src.services.forModels.SongService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/songs")
public class SongController {
    private final SongService songService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SongDTO> createSong(@ModelAttribute @Valid SongCreateRequest request) throws StatusException {
        SongDTO songDTO = this.songService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(songDTO);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SongDTO> updateSong(@PathVariable Long id, @ModelAttribute @Valid SongUpdateRequest request) throws StatusException {
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