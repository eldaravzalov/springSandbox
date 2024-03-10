package com.example.app1.listeners;

import com.example.app1.events.SomeEvent;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class SomeListener {

    private static final Logger LOG = LoggerFactory.getLogger(SomeListener.class);

    @PostConstruct
    public void init() {
        LOG.warn("someListener created");
    }

    @EventListener
    public void onApplicationEvent(SomeEvent event) {
        LOG.info("SomeEvent handled by someListener");
    }
}
