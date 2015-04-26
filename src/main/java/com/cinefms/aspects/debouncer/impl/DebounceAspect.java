package com.cinefms.aspects.debouncer.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import com.cinefms.aspects.debouncer.annotations.Debounce;

@Aspect
public class DebounceAspect {

	private Map<String,ScheduledExecutorService> scheduledExecutors = new HashMap<String, ScheduledExecutorService>(); 	
	private ConcurrentHashMap<String,TimerTask> tasks = new ConcurrentHashMap<String, TimerTask>(); 	
	
	@Pointcut("execution(public * * (..))")  
	private void anyPublicMethod() {}
	
	@Around("anyPublicMethod() && @annotation(debounce)")
	public Object all(ProceedingJoinPoint proceedingJoinPoint, Debounce debounce) throws Throwable {

		Key key = (Key)debounce.key().newInstance();
		key.setArgs(proceedingJoinPoint.getArgs());
		
		TimerTask callable = new TimerTask(debounce, key, proceedingJoinPoint);
		
		TimerTask actual = tasks.putIfAbsent(key.toString(), callable);
		System.err.println(actual);
		if(actual == null) {
			actual = callable;
			ScheduledFuture<Object> f = getExecutor(debounce, proceedingJoinPoint).schedule(actual, debounce.delay(), TimeUnit.MILLISECONDS);
			callable.setFuture(f);
			System.err.println("new future ... key: "+key);
		} else {
			actual.extend();
			System.err.println("existing future ... ");
			
		}

		while(actual.getFuture()==null) {
			Thread.yield();
		}

		System.err.println(Thread.currentThread().getName()+" calling GET ... ");
		 
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
				System.err.println("extending ... ");
				if (dueTime < 0) {
					return false;
				}
				dueTime = System.currentTimeMillis() + debounce.delay();
				return true;
			}
		}
		
		public Object call() {
			System.err.println(" calling ... ");
			synchronized (lock) {
				long remaining = dueTime - System.currentTimeMillis();
				if (remaining > 0) { 
					this.future = getExecutor(debounce, proceedingJoinPoint).schedule(this, remaining, TimeUnit.MILLISECONDS);
				} else {
					dueTime = -1;
				}
				try {
					System.err.println(" calling ... proceeding");
					Object o = proceedingJoinPoint.proceed();
					System.err.println(" calling ... proceeding - got "+o);
					return o;
				} catch (Throwable e) {
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
