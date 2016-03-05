package com.xtivia.xsf.core.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
/**
 * class TestConfiguration: Used to specify any unique Spring configuration elements for the test environment.
 */
public class TestConfiguration {
	
  @Bean
	/**
	 * testBeanFactoryPostProcessor: Returns a Spring factory post-processor that adjusts the controller class to use a mock.
	 */
  public TestBeanFactoryPostProcessor testBeanFactoryPostProcessor()
  {
    return new TestBeanFactoryPostProcessor();
  }

}
