package com.skjlls.aspects.debounce;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.skjlls.aspects.debounce.services.TestService01;

@RunWith(MockitoJUnitRunner.class)
public class DebouncerTest {

	@Test
	public void testCallFast() {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("contexts/config01.xml");
        TestService01 t01 = (TestService01) ctx.getBean("t01");
        TestRunnable01 tr01 = new TestRunnable01(t01, "A", 0);
        Thread t1 = new Thread(tr01,"thread1");
        t1.start();
        TestRunnable01 tr02 = new TestRunnable01(t01, "A", 0);
        Thread t2 = new Thread(tr02,"thread2");
        t2.start();
        try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Assert.assertEquals("a / A / 1", tr01.getResult());
        Assert.assertEquals("a / A / 1", tr02.getResult());
	}

	@Test
	public void testCallSlow() {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("contexts/config01.xml");
		TestService01 t01 = (TestService01) ctx.getBean("t01");
		TestRunnable01 tr01 = new TestRunnable01(t01, "A", 0);
		Thread t1 = new Thread(tr01,"thread1");
		t1.start();
		TestRunnable01 tr02 = new TestRunnable01(t01, "A", 1000);
		Thread t2 = new Thread(tr02,"thread2");
		t2.start();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Assert.assertEquals("a / A / 1", tr01.getResult());
		Assert.assertEquals("a / A / 2", tr02.getResult());
	}
	
	@Test
	public void testCallExtend() {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("contexts/config01.xml");
		TestService01 t01 = (TestService01) ctx.getBean("t01");
		TestRunnable01 tr01 = new TestRunnable01(t01, "A", 0);
		Thread t1 = new Thread(tr01,"thread1");
		t1.start();
		TestRunnable01 tr02 = new TestRunnable01(t01, "A", 1000);
		Thread t2 = new Thread(tr02,"thread2");
		t2.start();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Assert.assertEquals("a / A / 1", tr01.getResult());
		Assert.assertEquals("a / A / 2", tr02.getResult());
	}
	
	
	private class TestRunnable01 implements Runnable {
		
		private TestService01 t01;
		private String s;
		private long sleep;
		private String result;
		
		public TestRunnable01(TestService01 t01, String s, long sleep) {
			super();
			this.t01 = t01;
			this.s = s;
			this.sleep = sleep;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			result = t01.getString(s);
			System.err.println("RESULT: ===== "+result);
		}

		public String getResult() {
			return result;
		}

		public void setResult(String result) {
			this.result = result;
		}
		
	}
	
	
	
}
