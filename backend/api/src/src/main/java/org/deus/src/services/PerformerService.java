package org.deus.src.services;

import lombok.RequiredArgsConstructor;
import org.deus.src.dtos.fromModels.PerformerDTO;
import org.deus.src.models.PerformerModel;
import org.deus.src.repositories.PerformerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PerformerService {
    private final PerformerRepository performerRepository;

    public PerformerDTO getPerformerById(Long id) {
        Optional<PerformerModel> optionalPerformerModel = performerRepository.findById(id);
        return optionalPerformerModel.map(PerformerModel::mapToPerformerDTO).orElse(null);
    }

    public List<PerformerDTO> getAllPerformers() {
        List<PerformerModel> performers = performerRepository.findAll();
        return performers.stream()
                .map(PerformerModel::mapToPerformerDTO)
                .collect(Collectors.toList());
    }
}