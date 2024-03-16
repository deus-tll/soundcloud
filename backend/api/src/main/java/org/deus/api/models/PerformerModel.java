package org.deus.api.models;

import org.deus.api.models.auth.UserModel;

import java.util.Set;

import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.OneToOne;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;

import io.swagger.v3.oas.annotations.media.Schema;

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
}
