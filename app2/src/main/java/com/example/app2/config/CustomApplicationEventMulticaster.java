package com.example.app2.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationListenerMethodAdapter;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.lang.NonNull;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.*;

import static java.util.Optional.empty;
import static org.springframework.util.ReflectionUtils.findField;
import static org.springframework.util.ReflectionUtils.makeAccessible;

public class CustomApplicationEventMulticaster extends SimpleApplicationEventMulticaster {

    private static final Logger LOG = LoggerFactory.getLogger(CustomApplicationEventMulticaster.class);

    private final Field beanNameField;
    private final Set<String> disabledListeners = new HashSet<>();

    CustomApplicationEventMulticaster(String disabledListenerBeans) {
        beanNameField = findField(ApplicationListenerMethodAdapter.class, "beanName");
        assert beanNameField != null;
        makeAccessible(beanNameField);
        if (StringUtils.hasLength(disabledListenerBeans)) {
            disabledListeners.addAll(Arrays.asList(disabledListenerBeans.split(",")));
        }
    }

    @Override
    protected void invokeListener(@NonNull ApplicationListener<?> listener, @NonNull ApplicationEvent event) {
        if (!filter(listener)) {
            super.invokeListener(listener, event);
        } else {
            LOG.info("Listener " + listener.toString() + " disabled");
        }
    }

    private boolean filter(ApplicationListener<?> listener) {
        return getBeanName(listener).filter(disabledListeners::contains).isPresent();
    }

    @SuppressWarnings("ConstantConditions")
    private Optional<String> getBeanName(ApplicationListener<?> listener) {
        return (listener instanceof ApplicationListenerMethodAdapter)
                ? Optional.of((String) ReflectionUtils.getField(beanNameField, listener)) : empty();
    }

    public void enable(String... listenerBeans) {
        disabledListeners.removeAll(List.of(listenerBeans));
    }

    public void disable(String... listenerBeans) {
        disabledListeners.addAll(List.of(listenerBeans));
    }
}
