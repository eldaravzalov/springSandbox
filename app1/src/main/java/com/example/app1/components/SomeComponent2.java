package com.example.app1.components;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SomeComponent2 {

    private static final Logger LOG = LoggerFactory.getLogger(SomeComponent2.class);

    @PostConstruct
    public void init() {
        LOG.warn("someComponent2 created");
    }
}
