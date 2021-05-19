package com.suyh38.spi;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface Spi {

    /**
     * Alias name of Provider class
     */
    String value() default "";

    /**
     * Whether create singleton instance
     */
    boolean isSingleton() default true;

    /**
     * Whether is the default Provider
     */
    boolean isDefault() default false;

    /**
     * Order priority of Provider class
     */
    int order() default 0;

    int ORDER_HIGHEST = Integer.MIN_VALUE;

    int ORDER_LOWEST = Integer.MAX_VALUE;
}
