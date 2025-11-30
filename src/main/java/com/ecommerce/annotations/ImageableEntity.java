package com.ecommerce.annotations;

import jakarta.persistence.EntityListeners;
import java.lang.annotation.*;

import com.ecommerce.listeners.ImageableEntityListener;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@EntityListeners(ImageableEntityListener.class)
public @interface ImageableEntity {
}
