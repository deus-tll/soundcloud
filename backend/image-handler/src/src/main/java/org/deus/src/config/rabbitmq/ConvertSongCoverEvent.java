package org.deus.src.config.rabbitmq;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConvertSongCoverEvent {
    @Bean
    public Queue convertSongCover() {
        return new Queue("convert.song_cover", true);
    }
}
