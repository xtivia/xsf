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

import java.util.Map;

/**
 * class RoutingInfo: Container for the matched route handling information for an incoming request.
 */
public class RoutingInfo {

	/**
	 * pathParameters: The path parameter map from the request.
	 */
	private Map<String,String>   pathParameters;
	/**
	 * route: The route that matches the request.
	 */
    private IRoute               route;

	/**
	 * getPathParameters: Returns the path parameters map.
	 * @return Map The map of path parameters.
	 */
	public Map<String,String> getPathParameters() {
		return pathParameters;
	}

	/**
	 * setPathParameters: Sets the path parameters map.
	 * @param pathParameters
	 */
	public void setPathParameters(Map<String,String> pathParameters) {
		this.pathParameters = pathParameters;
	}

	/**
	 * getRoute: Returns the route instance.
	 * @return IRoute The route instance.
	 */
	public IRoute getRoute() {
		return route;
	}

	/**
	 * setRoute: Sets the route instance.
	 * @param route
	 */
	public void setRoute(IRoute route) {
		this.route = route;
	}
}

