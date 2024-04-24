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
public class WebsocketMessageDTO extends JsonSerializer {
    private String destination;
    private PayloadDTO payload;
}