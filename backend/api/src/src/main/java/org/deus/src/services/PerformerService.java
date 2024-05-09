package org.deus.src.services;

import lombok.RequiredArgsConstructor;
import org.deus.src.dtos.fromModels.PerformerDTO;
import org.deus.src.exceptions.StatusException;
import org.deus.src.models.PerformerModel;
import org.deus.src.models.auth.UserModel;
import org.deus.src.repositories.PerformerRepository;
import org.deus.src.requests.performer.PerformerCreateRequest;
import org.deus.src.requests.performer.PerformerUpdateRequest;
import org.deus.src.services.auth.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PerformerService {
    private final PerformerRepository performerRepository;
    private final UserService userService;

    public PerformerDTO create(PerformerCreateRequest request) {
        UserModel user = null;
        if (request.isUser()) {
            user = userService.getCurrentUser();
        }

        PerformerModel performer = new PerformerModel();
        performer.setName(request.getName());
        performer.setUser(user);

        PerformerModel savedPerformer = performerRepository.save(performer);
        return savedPerformer.mapToPerformerDTO();
    }

    public PerformerDTO update(Long id, PerformerUpdateRequest request) throws StatusException {
        PerformerModel performer = performerRepository.findById(id).orElseThrow(() -> new StatusException("Performer not found with id: " + id, HttpStatus.NOT_FOUND));

        UserModel user = null;

        if (request.isUser()) {
            user = userService.getCurrentUser();
        }

        performer.setUser(user);

        if (request.getName() != null && !request.getName().isEmpty()) {
            performer.setName(request.getName());
        }

        PerformerModel updatedPerformer = performerRepository.save(performer);
        return updatedPerformer.mapToPerformerDTO();
    }

    public PerformerDTO getById(Long id) throws StatusException {
        Optional<PerformerModel> optionalPerformerModel = performerRepository.findById(id);
        return optionalPerformerModel.map(PerformerModel::mapToPerformerDTO).orElseThrow(() -> new StatusException("Performer not found with id: " + id, HttpStatus.NOT_FOUND));
    }

    public Page<PerformerDTO> getAll(Pageable pageable) {
        Page<PerformerModel> performers = performerRepository.findAll(pageable);
        return performers.map(PerformerModel::mapToPerformerDTO);
    }

    public void deleteById(Long id) throws StatusException {
        if (!performerRepository.existsById(id)) {
            throw new StatusException("Performer not found with id: " + id, HttpStatus.NOT_FOUND);
        }

        performerRepository.deleteById(id);
    }
}