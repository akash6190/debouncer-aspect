package com.cinefms.metrics;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@RunWith(MockitoJUnitRunner.class)
public class DebouncerTest {

	@Test
	public void testContextBoot() {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("contexts/config01.xml");
	}
	
	
}
