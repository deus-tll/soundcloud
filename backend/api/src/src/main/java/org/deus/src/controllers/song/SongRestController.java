package org.deus.src.controllers.song;

import org.deus.src.dtos.fromModels.SongDTO;
import org.deus.src.models.SongModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/songs")
public class SongRestController {
    @PostMapping
    public ResponseEntity<SongDTO> createSong(@RequestBody SongModel song) {

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SongDTO> updateSong(@PathVariable Long id, @RequestBody SongModel songDetails) {

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSong(@PathVariable Long id) {

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}