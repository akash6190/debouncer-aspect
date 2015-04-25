package com.cinefms.aspects.debouncer.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Debounce {

	public String value() default "";
	public boolean async() default false;
	public int delay() default 100;
	
	
}
