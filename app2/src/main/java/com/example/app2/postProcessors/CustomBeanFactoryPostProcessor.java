package com.example.app2.postProcessors;

import com.example.app2.components.SomeComponent2Ext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class CustomBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

        if (Arrays.asList(beanFactory.getBeanDefinitionNames()).contains("someComponent2")) {
            ((DefaultListableBeanFactory) beanFactory).removeBeanDefinition("someComponent2");

            GenericBeanDefinition genericBeanDefinition = new GenericBeanDefinition();
            genericBeanDefinition.setBeanClass(SomeComponent2Ext.class);
            genericBeanDefinition.setInitMethodName("init");

            ((DefaultListableBeanFactory) beanFactory)
                    .registerBeanDefinition("someComponent2", genericBeanDefinition);
        }

        if (Arrays.asList(beanFactory.getBeanDefinitionNames()).contains("someComponent3")) {
            ((DefaultListableBeanFactory) beanFactory).removeBeanDefinition("someComponent3");
        }
        
    }
}
