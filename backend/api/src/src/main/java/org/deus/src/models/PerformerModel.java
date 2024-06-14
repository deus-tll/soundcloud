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

    public PerformerDTO mapToPerformerDTO(String photoUrl) {
        return new PerformerDTO(
                this.getId(),
                this.getName(),
                photoUrl
        );
    }
}