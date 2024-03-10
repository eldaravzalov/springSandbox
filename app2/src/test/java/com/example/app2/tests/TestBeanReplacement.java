package com.example.app2.tests;

import com.example.app1.components.SomeComponent;
import com.example.app1.components.SomeComponent2;
import com.example.app1.components.SomeComponent3;
import com.example.app2.components.SomeComponent2Ext;
import com.example.app2.components.SomeComponentExt;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
public class TestBeanReplacement {

    @Autowired
    private List<SomeComponent> someComponents;

    @Autowired
    private List<SomeComponent2> someComponents2;

    @Autowired(required = false)
    private List<SomeComponent3> someComponents3;

    @Test
    @DisplayName("Test if bean overriden correctly via @Bean annotation")
    void testBeanOverriding() {
        assertThat(someComponents.size() == 1);
        assertThat(someComponents.get(0) instanceof SomeComponentExt);
    }

    @Test
    @DisplayName("Test if bean was removed")
    void testBfpp() {

        assertThat(someComponents2.size() == 1);
        assertThat(someComponents2.get(0) instanceof SomeComponent2Ext);

        assertNull(someComponents3);
    }
}
