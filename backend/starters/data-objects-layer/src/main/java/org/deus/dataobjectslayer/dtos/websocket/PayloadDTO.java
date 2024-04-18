package org.deus.dataobjectslayer.dtos.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.deus.dataobjectslayer.JsonSerializer;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayloadDTO extends JsonSerializer implements Serializable {
    private String message;
    private Object data;
}
