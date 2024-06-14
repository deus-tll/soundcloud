package org.deus.src.seeds;

import org.deus.src.dtos.helpers.PerformerPhotoConvertingDTO;
import org.deus.src.exceptions.data.DataSavingException;
import org.deus.src.exceptions.message.MessageSendingException;
import org.deus.src.models.PerformerModel;
import org.deus.src.repositories.PerformerRepository;
import org.deus.src.services.RabbitMQService;
import org.deus.src.services.storage.StoragePerformerPhotoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class PerformerSeed implements CommandLineRunner {
    private final PerformerRepository performerRepository;
    private final RabbitMQService rabbitMQService;
    private final StoragePerformerPhotoService storagePerformerPhotoService;
    private final ResourceLoader resourceLoader;

    private static final Logger logger = LoggerFactory.getLogger(PerformerSeed.class);

    @Autowired
    public PerformerSeed(PerformerRepository performerRepository, RabbitMQService rabbitMQService, StoragePerformerPhotoService storagePerformerPhotoService, ResourceLoader resourceLoader) {
        this.performerRepository = performerRepository;
        this.rabbitMQService = rabbitMQService;
        this.storagePerformerPhotoService = storagePerformerPhotoService;
        this.resourceLoader = resourceLoader;
    }


    @Override
    public void run(String... args) {
        String name = "Unknown";

        if(!performerRepository.existsByName(name)) {
            PerformerModel performer = new PerformerModel();
            performer.setName(name);

            performerRepository.save(performer);

            try {
                Resource resource = resourceLoader.getResource("classpath:static_files/unknown_performer_photo.jpg");

                byte[] photoBytes;
                try (var inputStream = resource.getInputStream()) {
                    photoBytes = inputStream.readAllBytes();
                } catch (IOException e) {
                    logger.error("Failed to read unknown_performer_photo file", e);
                    throw e;
                }

                storagePerformerPhotoService.putOriginalBytes(performer.getId(), photoBytes);

                rabbitMQService.sendPerformerPhotoConvertingDTO("convert.performer_photo", new PerformerPhotoConvertingDTO(performer.getId(), ""));
            }
            catch (DataSavingException | MessageSendingException | IOException e) {
                String message = "Failed to upload performer photo file! Try later with update.";
                logger.error(message, e);
            }
        }
        else {
            String message = "Performer [" + name + "] already exists";
            logger.info(message);
        }
    }
}
