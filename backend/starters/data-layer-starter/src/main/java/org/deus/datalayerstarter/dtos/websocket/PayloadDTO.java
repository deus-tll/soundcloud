package org.deus.datalayerstarter.dtos.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.deus.datalayerstarter.JsonSerializer;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayloadDTO extends JsonSerializer{
    private String message;
    private Object data;
}
