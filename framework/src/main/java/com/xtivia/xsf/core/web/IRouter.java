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
 * class IRouter: Defines the interface that can return a RoutingInfo object for an incoming request/method combination.
 */
public interface IRouter {

	/**
	 * getRoutingInfo: Returns the routing info that can service the given request.
	 * @param requestUri requestUri The request URI.
	 * @param httpMethod httpMethod The HTTP method.
	 * @return RoutingInfo The matched route handler information or <code>null</code> if no handler could be matched.
	 */
	RoutingInfo getRoutingInfo(String requestUri, String httpMethod);
}

