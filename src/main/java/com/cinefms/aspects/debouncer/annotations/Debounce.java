package com.cinefms.aspects.debouncer.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.cinefms.aspects.debouncer.impl.CompoundKey;
import com.cinefms.aspects.debouncer.impl.Key;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Debounce {
	
	public static final String PER_METHOD = "PER_METHOD";
	public static final String PER_CLASS = "PER_CLASS";

	public String value() default "";
	public boolean async() default false;
	public int delay() default 100;
	public Class<? extends Key> key() default CompoundKey.class;
	public int threads() default 1;
	public String threadPool() default PER_METHOD;
	
	
}
