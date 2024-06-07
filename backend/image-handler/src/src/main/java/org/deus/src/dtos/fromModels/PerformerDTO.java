package org.deus.src.dtos.fromModels;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PerformerDTO {
    private Long id;
    private String name;
    private String photoUrl;
}