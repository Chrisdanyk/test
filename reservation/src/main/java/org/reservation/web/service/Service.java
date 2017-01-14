package org.reservation.web.service;

import javax.transaction.Transactional;
import java.lang.annotation.*;

@Transactional
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Documented
public @interface Service {
}
