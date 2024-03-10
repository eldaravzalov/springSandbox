package com.example.app1.components;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SomeComponent3 {

    private static final Logger LOG = LoggerFactory.getLogger(SomeComponent3.class);

    @PostConstruct
    public void init() {
        LOG.warn("someComponent3 created");
    }
}
