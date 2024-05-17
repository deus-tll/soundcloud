package org.deus.src.dtos.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebsocketMessageDTO {
    private String destination;
    private String username;
    private PayloadDTO payload;
}