package com.example.app2.components;

import com.example.app1.components.SomeComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SomeComponentExt extends SomeComponent {

    private static final Logger LOG = LoggerFactory.getLogger(SomeComponentExt.class);

    @Override
    public void init() {
        LOG.warn("someComponentExt created");
    }
}
