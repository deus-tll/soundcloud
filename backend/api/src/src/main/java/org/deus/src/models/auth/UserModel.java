package org.deus.src.models.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.deus.src.dtos.fromModels.UserDTO;
import org.deus.src.enums.RoleEnum;
import org.deus.src.models.BaseEntity;
import org.deus.src.models.PerformerModel;
import org.deus.src.models.SongModel;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@Schema(description = "User Model")
public class UserModel  extends BaseEntity implements UserDetails {
    @Size(min = 5, max = 50, message = "The username must be between 5 and 50 characters long")
    @NotBlank(message = "The username cannot be empty")
    @Pattern(regexp = "^[a-z0-9_.]+$", message = "The username must only contain lowercase letters, numbers, underscores and dots")
    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Schema(description = "E-mail address", example = "johndoe@gmail.com")
    @Size(min = 5, max = 255, message = "The email address must contain between 5 and 255 characters")
    @NotBlank(message = "The e-mail address cannot be empty")
    @Email(message = "E-mail address should be in the format user@example.com")
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Schema(description = "Password", example = "my_1secret1_password")
    @Size(min = 5, max = 60, message = "The password must contain between 5 and 60 characters")
    @NotBlank(message = "The password address cannot be empty")
    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 15, nullable = false)
    private RoleEnum role;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "performer_id", referencedColumnName = "id")
    @Nullable
    @Schema(description = "Indicator of whether the user is performer")
    private PerformerModel performer;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "uploader")
    @Schema(description = "User's songs")
    protected Set<SongModel> songs;

    public static UserDTO mapUserToDTO(UserModel userModel) {
        UserDTO userDTO = null;

        if (userModel != null) {
            userDTO = userModel.mapUserToDTO((String) null);
        }

        return userDTO;
    }

    public UserDTO mapUserToDTO(String avatarUrl) {
        return new UserDTO(this.getId(), this.getUsername(), this.getEmail(), this.getRole(), avatarUrl);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
