package org.deus.src.services;

import lombok.RequiredArgsConstructor;
import org.deus.src.exceptions.data.DataIsNotPresentException;
import org.deus.src.exceptions.data.DataProcessingException;
import org.deus.src.exceptions.data.DataSavingException;
import org.deus.src.services.storage.StorageSongService;
import org.deus.src.services.storage.StorageTempService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConvertSongService {
    private final StorageTempService storageTempService;
    private final StorageSongService storageSongService;
    private final AudioConverterService audioConverterService;
    private static final Logger logger = LoggerFactory.getLogger(ConvertSongService.class);

    public void convertSong(Long userId, Long songId, String fileId) throws DataIsNotPresentException, DataProcessingException {
        Optional<byte[]> optionalOriginalBytes = storageTempService.getOriginalBytes(userId, fileId);

        if (optionalOriginalBytes.isEmpty()) {
            String errorMessage = "OriginalBytes of requested song file was not present when trying to convert";
            logger.error(errorMessage);
            throw new DataIsNotPresentException(errorMessage);
        }

        int bitRate = 320000;

        Optional<byte[]> optionalConvertedBytes = audioConverterService.convertToAAC(optionalOriginalBytes.get(), bitRate);

        if (optionalConvertedBytes.isEmpty()) {
            String errorMessage = "ConvertedBytes of song file was not present after attempt to convert";
            logger.error(errorMessage);
            throw new DataIsNotPresentException(errorMessage);
        }

        try {
            storageSongService.putConvertedBytes(songId, optionalConvertedBytes.get(), ".aac");
        }
        catch (DataSavingException e) {
            String errorMessage = "Error while saving converted song";
            logger.error(errorMessage, e);
            throw new DataProcessingException(errorMessage, e);
        }
    }
}
