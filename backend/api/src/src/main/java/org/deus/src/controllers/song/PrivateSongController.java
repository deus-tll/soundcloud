package org.deus.src.controllers.song;

import lombok.RequiredArgsConstructor;
import org.deus.src.exceptions.StatusException;
import org.deus.src.requests.song.SongStatusRequest;
import org.deus.src.services.forModels.SongService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/private-api/songs")
public class PrivateSongController {
    private final SongService songService;

    @PutMapping(value = "/{id}/status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateSongStatus(@PathVariable Long id, @RequestBody SongStatusRequest request) throws StatusException {
        songService.updateStatus(id, request);
        return ResponseEntity.noContent().build();
    }
}