package com.cinefms.aspects.debouncer.impl;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import com.cinefms.aspects.debouncer.annotations.Debounce;

@Aspect
public class DebounceAspect {

	@Pointcut("execution(public * * (..))")  
	private void anyPublicMethod() {}

	@Around("anyPublicMethod() && @annotation(debounce)")
	public Object all(ProceedingJoinPoint proceedingJoinPoint, Debounce debounce) throws Throwable {
		return null;
	}

	
}
