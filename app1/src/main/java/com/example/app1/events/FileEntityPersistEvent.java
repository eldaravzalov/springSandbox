package com.example.app1.events;

import org.springframework.context.ApplicationEvent;

public class FileEntityPersistEvent extends ApplicationEvent {
    public FileEntityPersistEvent(Object source) {
        super(source);
    }
}
