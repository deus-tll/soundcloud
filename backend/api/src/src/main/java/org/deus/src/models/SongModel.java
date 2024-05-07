package org.deus.src.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.deus.src.dtos.fromModels.PerformerDTO;
import org.deus.src.dtos.fromModels.SongDTO;
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

    @ManyToOne
    @JoinColumn(name="uploader_id", nullable=false)
    @Schema(description = "Uploader")
    private UserModel uploader;

    @ManyToMany(mappedBy = "songs", fetch = FetchType.EAGER)
    @Schema(description = "Performers")
    private Set<PerformerModel> performers;

    public SongDTO mapToSongDTO() {
        return new SongDTO(
                this.getId(),
                this.getName(),
                this.getUploader().mapUserToDTO(),
                this.getPerformers().stream()
                        .map(performerModel -> new PerformerDTO(
                                performerModel.getId(),
                                performerModel.getName(),
                                UserModel.mapUserToDTO(performerModel.getUser()),
                                null))
                        .collect(Collectors.toSet())
        );
    }
}