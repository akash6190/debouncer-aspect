package com.cinefms.metrics;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cinefms.metrics.services.TestService01;

@RunWith(MockitoJUnitRunner.class)
public class DebouncerTest {

	@Test
	public void testContextBoot() {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("contexts/config01.xml");
        TestService01 t01 = (TestService01) ctx.getBean("t01");
        
        Thread t1 = new Thread(new TestRunnable01(t01, "A"),"thread1");
        t1.start();
        Thread t2 = new Thread( new TestRunnable01(t01, "A"),"thread2");
        t2.start();
        try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
	private class TestRunnable01 implements Runnable {
		
		private TestService01 t01;
		private String s;
		
		public TestRunnable01(TestService01 t01, String s) {
			super();
			this.t01 = t01;
			this.s = s;
		}

		@Override
		public void run() {
			System.err.println("RESULT: ===== "+t01.getString(s));
		}
		
	}
	
	
	
}
