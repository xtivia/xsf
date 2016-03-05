/**
 * Copyright (c) 2015 Xtivia, Inc. All rights reserved.
 *
 * This file is part of the Xtivia Services Framework (XSF) library.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.xtivia.xsf.core.config;

import java.util.Properties;

import javax.servlet.ServletConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.ServletConfigAware;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;


/**
 * class XsfConfig: The XSF configuration handler.
 */
@Configuration
public class XsfConfig implements ServletConfigAware {

	/**
	 * sc: The servlet config being wrapped.
	 */
	private ServletConfig sc;

	/**
	 * SUB_CONTEXT: The sub context initialization parameter name, the value represents the sub-context that the Liferay
	 * delegate servlet is listening for incoming requests to forward off to us.
	 */
	private static final String SUB_CONTEXT = "sub-context";

	/**
	 * setServletConfig: Sets the servlet config, injected by the ServletConfigAware interface.
	 * @param sc The servlet config.
	 */
	@Override
	public void setServletConfig(ServletConfig sc) {
		this.sc = sc;
	}

	/**
	 * geUrlMapper: Returns the URL mapper to use.  Basically this ensures that anything that is getting sent in to us
	 * by the Liferay delegate servlet is sent to our xsf_services_controller bean for processing.
	 * @return SimpleUrlHandlerMapping The handler mapping instance.
	 */
	@Bean
	public SimpleUrlHandlerMapping getUrlMapper() {

		// initialize to our subcontext as xsf, so Liferay will end up sending anything that it gets on /delegate/xsf to us
		// for processing.
		String subContext = "xsf";

		// if we have a servlet config
		if (sc != null) {
			// get the value from the servlet config init parameter.
		  String subContextFromSC = sc.getInitParameter(SUB_CONTEXT);

			// if we have a non-null value
		  if (subContextFromSC != null) {
			  // assign it as the subcontext.
			subContext = subContextFromSC;

			  // TODO: Should probably be validating the value, checking for leading slash, that kind of thing.
		  }
		}

		// create a new handler instance
	    SimpleUrlHandlerMapping mapper = new SimpleUrlHandlerMapping();

		// define the new mapping key given the sub context
		String urlMappingKey = String.format("/%s/**",subContext);

		// create a new properties instance for the mapper
		Properties mappings = new Properties();

		// Set the property to key the mapping to the XSF service controller bean,
		// basically our XSF controller.
		mappings.setProperty(urlMappingKey, "xsf_services_controller");

		// set the properties into the mapper.
		mapper.setMappings(mappings);

		// return the mapper.
		return mapper;
	}
}

