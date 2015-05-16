package com.skjlls.aspects.debounce.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import com.skjlls.aspects.debounce.Debounce;

@Aspect
public class DebounceAspect {

	private static Log log = LogFactory.getLog(DebounceAspect.class);
	
	private Map<String,ScheduledExecutorService> scheduledExecutors = new HashMap<String, ScheduledExecutorService>(); 	
	private ConcurrentHashMap<String,TimerTask> tasks = new ConcurrentHashMap<String, TimerTask>(); 	
	
	@Pointcut("execution(public * * (..))")  
	private void anyPublicMethod() {}
	
	@Around("anyPublicMethod() && @annotation(debounce)")
	public Object all(ProceedingJoinPoint proceedingJoinPoint, Debounce debounce) throws Throwable {

		log.debug("DebounceAspect triggered for: "+proceedingJoinPoint.getSignature());
		
		if(log.isDebugEnabled()) {
			Thread.currentThread().dumpStack();
		}
		
		Key key = (Key)debounce.key().newInstance();
		key.setArgs(proceedingJoinPoint.getArgs());
		
		TimerTask callable = new TimerTask(debounce, key, proceedingJoinPoint);
		
		TimerTask actual = tasks.putIfAbsent(key.toString(), callable);
		if(actual == null) {
			actual = callable;
			ScheduledFuture<Object> f = getExecutor(debounce, proceedingJoinPoint).schedule(actual, debounce.delay(), TimeUnit.MILLISECONDS);
			callable.setFuture(f);
		} else {
			if(debounce.extend()) {
				actual.extend();
			}
		}
		
		if(debounce.async()) {
			log.debug("DebounceAspect is async, returning null!");
			return null;
		}

		while(actual.getFuture()==null) {
			Thread.yield();
		}

		log.debug("DebounceAspect is async, returning value!");
		return actual.
				getFuture().
				get();
		
	}
	
	
	
	
	protected ScheduledExecutorService getExecutor(Debounce d, ProceedingJoinPoint point) {
		String s = d.threadPool();
		if(d.threadPool()==Debounce.PER_METHOD) {
			s = point.getSignature().toLongString();
		}
		ScheduledExecutorService out = scheduledExecutors.get(s);
		if(out==null) {
			out = Executors.newScheduledThreadPool(d.threads());
			scheduledExecutors.put(s, out);
		}
		return out;
	}
	

	private class TimerTask implements Callable<Object> {

		private Debounce debounce;
		private ProceedingJoinPoint proceedingJoinPoint;
		private final Key key;
		private ScheduledFuture<Object> future;
		private long dueTime;
		private final Object lock = new Object();
		
		public TimerTask(Debounce debounce, Key key, ProceedingJoinPoint proceedingJoinPoint) {
			this.debounce = debounce;
			this.proceedingJoinPoint = proceedingJoinPoint;
			this.key = key;
			extend();
		}
		
		public boolean extend() {
			synchronized (lock) {
				if (dueTime < 0) {
					return false;
				}
				log.debug("Debounce aspect - extending ... ");
				dueTime = System.currentTimeMillis() + debounce.delay();
				return true;
			}
		}
		
		public Object call() {
			synchronized (lock) {
				long remaining = dueTime - System.currentTimeMillis();
				if (remaining > 0) { 
					log.debug("Debounce aspect - timeout not reached ... ");
					this.future = getExecutor(debounce, proceedingJoinPoint).schedule(this, remaining, TimeUnit.MILLISECONDS);
				} else {
					dueTime = -1;
				}
				try {
					log.debug("Debounce aspect - timeout reached!");
					Object o = proceedingJoinPoint.proceed();
					log.debug("Debounce aspect - timeout reached .. returning: "+o);
					return o;
				} catch (Throwable e) {
					log.error("error in CALL() ... ",e);
					throw new RuntimeException(e);
				} finally {
					tasks.remove(key.toString());
				}
			}
		}

		public ScheduledFuture<Object> getFuture() {
			return future;
		}

		public void setFuture(ScheduledFuture<Object> future) {
			this.future = future;
		}
	}
	
	
}
