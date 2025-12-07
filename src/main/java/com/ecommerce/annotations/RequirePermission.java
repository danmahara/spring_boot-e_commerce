package com.ecommerce.annotations;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {
    String value(); // Permission name, e.g., "CREATE_PRODUCT"
}