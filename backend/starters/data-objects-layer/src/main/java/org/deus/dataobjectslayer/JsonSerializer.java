package org.deus.dataobjectslayer;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;

public class JsonSerializer implements Serializable {
    public String toJson() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.writeValueAsString(this);
    }
}
