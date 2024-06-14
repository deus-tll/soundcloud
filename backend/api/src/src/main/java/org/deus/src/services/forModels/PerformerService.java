package org.deus.src.services.forModels;

import lombok.RequiredArgsConstructor;
import org.deus.src.dtos.fromModels.PerformerDTO;
import org.deus.src.dtos.helpers.PerformerPhotoConvertingDTO;
import org.deus.src.exceptions.StatusException;
import org.deus.src.exceptions.data.DataSavingException;
import org.deus.src.exceptions.message.MessageSendingException;
import org.deus.src.models.PerformerModel;
import org.deus.src.models.auth.UserModel;
import org.deus.src.repositories.PerformerRepository;
import org.deus.src.requests.performer.PerformerCreateRequest;
import org.deus.src.requests.performer.PerformerUpdateRequest;
import org.deus.src.services.RabbitMQService;
import org.deus.src.services.auth.UserService;
import org.deus.src.services.storage.StoragePerformerPhotoService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PerformerService {
    private final PerformerRepository performerRepository;
    private final UserService userService;
    private final RabbitMQService rabbitMQService;
    private final StoragePerformerPhotoService storagePerformerPhotoService;
    private static final Logger logger = LoggerFactory.getLogger(PerformerService.class);

    @CacheEvict(value = "performers", allEntries = true)
    public PerformerDTO create(@NotNull PerformerCreateRequest request) {
        PerformerModel performer = new PerformerModel();
        performer.setName(request.getName());

        UserModel user = userService.getCurrentUser();

        if (request.isUser()) {
            performer.setUser(user);
        }

        PerformerModel savedPerformer = performerRepository.save(performer);

        convertPerformerPhoto(user, savedPerformer, request.getPhoto());

        return savedPerformer.mapToPerformerDTO(storagePerformerPhotoService.getPathToFile(savedPerformer.getId()));
    }

    @CacheEvict(value = "performers", allEntries = true)
    public PerformerDTO update(Long id, @NotNull PerformerUpdateRequest request) throws StatusException {
        PerformerModel performer = performerRepository.findById(id).orElseThrow(() -> new StatusException("Performer not found with id: " + id, HttpStatus.NOT_FOUND));

        UserModel user = userService.getCurrentUser();

        if (request.isUser()) {
            performer.setUser(user);
        }

        if (request.getName() != null && !request.getName().isEmpty()) {
            performer.setName(request.getName());
        }

        PerformerModel savedPerformer = performerRepository.save(performer);

        if (request.getPhoto() != null) {
            convertPerformerPhoto(user, savedPerformer, request.getPhoto());
        }

        return savedPerformer.mapToPerformerDTO(storagePerformerPhotoService.getPathToFile(savedPerformer.getId()));
    }

    private void convertPerformerPhoto(UserModel user, PerformerModel savedPerformer, MultipartFile photo) {
        try {
            storagePerformerPhotoService.putOriginalBytes(savedPerformer.getId(), photo.getBytes());

            rabbitMQService.sendPerformerPhotoConvertingDTO("convert.performer_photo", new PerformerPhotoConvertingDTO(savedPerformer.getId(), user.getUsername()));
        }
        catch (IOException | DataSavingException | MessageSendingException e) {
            String message = "Failed to upload performer photo file! Try later with update.";
            logger.error(message, e);
            this.rabbitMQService.sendWebsocketMessageDTO(
                    "websocket.message.send",
                    user.getUsername(),
                    "/topic/error",
                    message,
                    null);
        }
    }

    @Cacheable(value = "performers", key = "#id")
    public PerformerDTO getById(Long id) throws StatusException {
        Optional<PerformerModel> optionalPerformerModel = performerRepository.findById(id);
        return optionalPerformerModel.map(performerModel -> performerModel.mapToPerformerDTO(storagePerformerPhotoService.getPathToFile(performerModel.getId()))).orElseThrow(() -> new StatusException("Performer not found with id: " + id, HttpStatus.NOT_FOUND));
    }

    @Cacheable(value = "performers", key = "#pageable")
    public Page<PerformerDTO> getAll(Pageable pageable) {
        Page<PerformerModel> performers = performerRepository.findAll(pageable);
        return performers.map(performerModel -> performerModel.mapToPerformerDTO(storagePerformerPhotoService.getPathToFile(performerModel.getId())));
    }

    @CacheEvict(value = "performers", allEntries = true)
    public void deleteById(Long id) throws StatusException {
        if (!performerRepository.existsById(id)) {
            throw new StatusException("Performer not found with id: " + id, HttpStatus.NOT_FOUND);
        }

        performerRepository.deleteById(id);
    }

    public List<PerformerDTO> getAllPerformers() {
        List<PerformerModel> performersAll = performerRepository.findAll();
        return performersAll.stream()
                .map(performerModel -> performerModel.mapToPerformerDTO(storagePerformerPhotoService.getPathToFile(performerModel.getId())))
                .collect(Collectors.toList());
    }
}