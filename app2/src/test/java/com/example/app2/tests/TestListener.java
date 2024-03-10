package com.example.app2.tests;

import com.example.app1.events.SomeEvent;
import com.example.app1.listeners.SomeListener;
import com.example.app1.listeners.SomeListener2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SmartApplicationListener;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class TestListener {
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private ApplicationEventMulticaster applicationEventMulticaster;

    @SpyBean
    private SomeListener someListener;

    @SpyBean
    private SomeListener2 someListener2;

    @Test
    public void test() {
        mockingDetails(someListener).getInvocations().clear();
        applicationEventPublisher.publishEvent(new SomeEvent(this));
        verify(someListener, times(1)).onApplicationEvent(any());
    }

    @Test
    public void testCustomApplicationEventMulticaster() {
        mockingDetails(someListener2).getInvocations().clear();
        applicationEventPublisher.publishEvent(new SomeEvent(this));
        verify(someListener2, times(0)).onApplicationEvent(any());
    }

    @Test
    public void testRuntimeRemovedListener() throws NoSuchMethodException {
        mockingDetails(someListener).getInvocations().clear();
//        applicationEventMulticaster.removeApplicationListeners(l -> l instanceof ApplicationListenerMethodAdapter
//                &&((ApplicationListenerMethodAdapter) l).getListenerId().contains("SomeListener.")
//                &&((ApplicationListenerMethodAdapter) l).getListenerId().contains("onApplicationEvent"));
        applicationEventMulticaster.removeApplicationListeners(l -> l instanceof SmartApplicationListener
                &&((SmartApplicationListener) l).getListenerId().contains("SomeListener.onApplicationEvent"));
        applicationEventPublisher.publishEvent(new SomeEvent(this));
        verify(someListener, times(0)).onApplicationEvent(any());
//        applicationEventMulticaster.addApplicationListener(
//                new ApplicationListenerMethodAdapter("someListener",
//                        SomeListener.class,
//                        SomeListener.class.getMethod("onApplicationEvent", SomeEvent.class)));
    }
}
