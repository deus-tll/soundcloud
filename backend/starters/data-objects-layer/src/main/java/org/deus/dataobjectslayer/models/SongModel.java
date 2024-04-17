package org.deus.dataobjectslayer.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.deus.dataobjectslayer.models.auth.UserModel;

import java.util.Set;

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

    @ManyToMany(mappedBy = "songs")
    @Schema(description = "Performers")
    private Set<PerformerModel> performers;
}
