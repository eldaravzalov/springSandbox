package com.example.app2.components;

import com.example.app1.components.SomeComponent2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SomeComponent2Ext extends SomeComponent2 {

    private static final Logger LOG = LoggerFactory.getLogger(SomeComponentExt.class);

   @Override
    public void init() {
        LOG.warn("someComponent2Ext created");
    }
}

