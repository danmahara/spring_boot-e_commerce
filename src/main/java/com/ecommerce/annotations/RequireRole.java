package com.ecommerce.annotations;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireRole {
    String[] value(); // Role names, e.g., {"ADMIN", "SUPER_ADMIN"}
}
