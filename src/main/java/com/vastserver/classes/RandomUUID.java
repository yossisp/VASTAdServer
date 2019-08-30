package com.vastserver.classes;

import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class RandomUUID {
    private String uuid;

    public String getUuid() {
        return UUID.randomUUID().toString();
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
