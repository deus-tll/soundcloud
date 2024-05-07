package org.deus.src.controllers.song;

import lombok.RequiredArgsConstructor;
import org.deus.src.dtos.fromModels.SongDTO;
import org.deus.src.services.SongService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SongGraphQLController {
    private final SongService songService;

    @QueryMapping
    public SongDTO songById(@Argument Long id) {
        return songService.getSongById(id);
    }

    @QueryMapping
    public List<SongDTO> allSongs() {
        return songService.getAllSongs();
    }
}