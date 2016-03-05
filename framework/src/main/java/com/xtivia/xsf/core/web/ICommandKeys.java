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
package com.xtivia.xsf.core.web;

/**
 * class ICommandKeys: Defines constant key values for the objects that may be in the context.
 */
public interface ICommandKeys {

	/**
	 * HTTP_SESSION: Key for the HTTP session object.
	 */
	String HTTP_SESSION     = "_session_";
	
	/**
	 * HTTP_REQUEST: Key for the HTTP request object.
	 */
	String HTTP_REQUEST     = "_request_";
	
	/**
	 * HTTP_RESPONSE: Key for the HTTP response object.
	 */
    String HTTP_RESPONSE    = "_response_";
	
    /**
	 * SERVLET_CONTEXT: Key for the servlet context object.
	 */
	String SERVLET_CONTEXT  = "_servlet_context_";
	
	/**
	 * PATH_PARAMETERS: Key for the path parameters map object.
	 */
	String PATH_PARAMETERS  = "_pathparams_";
	
	/**
	 * ROUTING_INFO: Key for the routing info object. Contains matched IRoute and path parameters.
	 */
	String ROUTING_INFO  = "_routing_info_";
}
