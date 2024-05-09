package org.deus.src.dtos.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.deus.src.dtos.JsonSerializer;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebsocketMessageDTO extends JsonSerializer {
    private String destination;
    private String username;
    private PayloadDTO payload;
}