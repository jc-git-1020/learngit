package com.example.rabbitmq;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MqConsumer {
    /**
     *
     */
    int METHOD_PARAMS_COUNT = 1;

    /**
     * @return
     */
    String key() default "";
}
