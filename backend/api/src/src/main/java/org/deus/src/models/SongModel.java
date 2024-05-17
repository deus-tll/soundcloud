package org.deus.src.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.deus.src.dtos.fromModels.PerformerDTO;
import org.deus.src.dtos.fromModels.SongDTO;
import org.deus.src.enums.SongStatus;
import org.deus.src.models.auth.UserModel;

import java.util.Set;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "songs")
@Schema(description = "Song Model")
public class SongModel extends BaseEntity {
    @Schema(description = "The title of the song", example = "The Show Must Go On")
    private String name;

    @Schema(description = "Temporary fileId in case something goes wrong during first preparation of song. Later this will allow to access file in tempStorage to try again", example = "21be7c86-8a74-49ad-920f-c788aae6a4e1")
    @Nullable
    private String tempFileId;

    @ManyToOne
    @JoinColumn(name="uploader_id", nullable=false)
    @Schema(description = "Uploader")
    private UserModel uploader;

    @ManyToMany(mappedBy = "songs")
    @Schema(description = "Performers")
    private Set<PerformerModel> performers;

    @Schema(description = "Status of the song", example = "Processing")
    @Enumerated(EnumType.STRING)
    private SongStatus status = SongStatus.PROCESSING;

    public SongDTO mapToSongDTO(String url) {
        return new SongDTO(
                this.getId(),
                this.getName(),
                this.getUploader().mapUserToDTO(),
                this.getPerformers().stream()
                        .map(performerModel -> new PerformerDTO(
                                performerModel.getId(),
                                performerModel.getName(),
                                UserModel.mapUserToDTO(performerModel.getUser())))
                        .collect(Collectors.toSet()),
                this.getStatus(),
                url
        );
    }
}