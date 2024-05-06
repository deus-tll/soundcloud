package org.deus.src.controllers.performer;

import lombok.RequiredArgsConstructor;
import org.deus.datalayerstarter.dtos.PerformerDTO;
import org.deus.src.services.PerformerService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class PerformerGraphQLController {
    private final PerformerService performerService;

    @QueryMapping
    public PerformerDTO performerById(@Argument Long id) {
        return performerService.getPerformerById(id);
    }

    @QueryMapping
    public List<PerformerDTO> allPerformers() {
        return performerService.getAllPerformers();
    }
}