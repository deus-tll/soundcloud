package org.deus.src.controllers.performer;

import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;
import org.deus.src.dtos.fromModels.PerformerDTO;
import org.deus.src.services.PerformerService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PerformerGraphQLController implements GraphQLQueryResolver {
    private final PerformerService performerService;

    public PerformerDTO performerById(@Argument Long id) {
        return performerService.getPerformerById(id);
    }

    public Iterable<PerformerDTO> allPerformers() {
        return performerService.getAllPerformers();
    }
}