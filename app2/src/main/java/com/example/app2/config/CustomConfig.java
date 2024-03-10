package com.example.app2.config;

import com.example.app2.components.SomeComponentExt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomConfig {
    //1st option - redefining bean (+ app.properties spring.main.allow-bean-definition-overriding=true)
    @Bean(value = "someComponent", initMethod = "init")
    SomeComponentExt someComponent() {
        return new SomeComponentExt();
    }

    //2nd option - edit bean definition - see {@link CustomBeanFactoryPostProcessor}

    @Bean
    CustomApplicationEventMulticaster applicationEventMulticaster(@Value("${app.listeners.disabled:}") String disabledBeans) {
        return new CustomApplicationEventMulticaster(disabledBeans);
    }
}
