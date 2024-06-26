package org.deus.src.dtos.fromModels;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@Data
@AllArgsConstructor
public class PerformerDTO {
    private Long id;
    private String name;
    private String photoUrl;
}