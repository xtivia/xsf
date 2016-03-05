package com.xtivia.xsf.core.web;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

public class TestBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory bf)	throws BeansException {
        BeanDefinition bd = bf.getBeanDefinition("xsf_services_controller");
        if (bd != null && bd.getBeanClassName().endsWith("LiferayServicesController")) {
        	bd.setBeanClassName("com.xtivia.xsf.core.web.MockLiferayServicesController");
        }	
	}
}
