package com.example.rabbitmq;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MqConsumerDispatcher implements BeanPostProcessor, ApplicationContextAware {

    private ApplicationContext applicationContext;
    private Map<String, TargetWrapper> registry = new ConcurrentHashMap<>();

    public void dispatcher(String key, String arg) {
        TargetWrapper targetWrapper = registry.getOrDefault(key, null);
        if (targetWrapper == null) {
            throw new RuntimeException("test");
        }
        Object bean = applicationContext.getBean(targetWrapper.getBeanName());
        try {
            targetWrapper.getMethod().invoke(bean, arg);
        } catch (Exception e) {
            throw new RuntimeException("test");
        }
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Method[] methods = bean.getClass().getDeclaredMethods();
        for (Method method : methods) {
            Annotation[] annotations = method.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                if (!(annotation instanceof MqConsumer)) {
                    continue;
                }
                if (method.getParameterCount() != MqConsumer.METHOD_PARAMS_COUNT) {
                    throw new RuntimeException("test");
                }
                MqConsumer mqConsumer = (MqConsumer) annotation;
                if (registry.containsKey(mqConsumer.key())) {
                    throw new RuntimeException("test");
                }
                registry.putIfAbsent(mqConsumer.key(), new TargetWrapper(beanName, method));
            }
        }
        return bean;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Data
    @AllArgsConstructor
    private class TargetWrapper {
        private String beanName;
        private Method method;
    }
}
