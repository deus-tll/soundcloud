package org.deus.dataobjectslayer;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonSerializer {
    public String toJson() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.writeValueAsString(this);
    }
}
