package org.deus.src.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.deus.datalayerstarter.dtos.PerformerDTO;
import org.deus.datalayerstarter.dtos.SongDTO;
import org.deus.src.models.auth.UserModel;

import java.util.Set;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "performers")
@Schema(description = "Performer Model")
public class PerformerModel extends BaseEntity {
    @Column(unique = true)
    @Schema(description = "Performer name", example = "Jon Bon Jovi")
    private String name;

    @OneToOne(mappedBy = "performer")
    @Schema(description = "Indicator of whether the performer is user")
    private UserModel user;

    @ManyToMany
    @JoinTable(
            name = "performers_songs",
            joinColumns = { @JoinColumn(name = "performer_id") },
            inverseJoinColumns = @JoinColumn(name = "song_id")
    )
    @Schema(description = "Performer's songs")
    private Set<SongModel> songs;

    public PerformerDTO mapToPerformerDTO() {
        return new PerformerDTO(
                this.getId(),
                this.getName(),
                this.getUser().mapUserToDTO(),
                this.getSongs().stream()
                        .map(songModel -> new SongDTO(
                                songModel.getId(),
                                songModel.getName(),
                                songModel.getUploader().mapUserToDTO(),
                                null))
                        .collect(Collectors.toSet())
        );
    }
}