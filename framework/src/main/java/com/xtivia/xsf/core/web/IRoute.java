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

import java.lang.reflect.Method;

/**
 * class IRoute: Defines a route, basically a handler for an HTTP request URI.
 */
public interface IRoute {

	/**
	 * getUri: Returns the URI for the route.
	 * @return String The URI.
	 */
	 String getUri();

	/**
	 * setUri: Sets the URI for the route.
	 * @param uri The URI.
	 */
	 void setUri(String uri);

	/**
	 * getHttpMethod: Returns the HTTP method for the route.
	 * @return HttpMethod The HTTP method for the route.
	 */
	 String getHttpMethod();

	/**
	 * setHttpMethod: Sets the HTTP method for the route.
	 * @param httpMethod The method to use.
	 */
	 void setHttpMethod(String httpMethod);

	/**
	 * getCommandName: Returns the command name (the Spring bean name) for the route.
	 * @return String The command name.
	 */
	 String getCommandName();

	/**
	 * setCommandName: Sets the command name (the Spring bean name) for the route.
	 * @param commandName The command name.
	 */
	 void setCommandName(String commandName);

	/**
	 * getInputClass: Returns the input class for the route.
	 * @return Class The input class for the route.
	 */
	 Class<?> getInputClass();

	/**
	 * setInputClass: Sets the input class for the route.
	 * @param inputClass The input class.
	 */
	 void setInputClass(Class<?> inputClass);

	/**
	 * getInputName: Returns the input name for the route.
	 * @return String The input name for the route.
	 */
	 String getInputName();

	/**
	 * setInputName: Sets the input name for the route.
	 * @param inputName The input name.
	 */
	 void setInputName(String inputName);

	/**
	 * isCached: Returns whether the route is cached or not.
	 * @return boolean <code>true</code> if it is cached, otherwise it is not.
	 */
	 boolean isCached();

	/**
	 * setCached: Sets the cached flag.
	 * @param val The new cached flag value.
	 */
	 void setCached(boolean val);

	/**
	 * isAuthenticated: Returns indicator whether the route requires authentication or not.
	 * @return boolean <code>true</code> if the route requires authentication otherwise it does not.
	 */
     boolean isAuthenticated();

	/**
	 * setAuthenticated: Returns the authenticated flag for the route.
	 * @param val The new authenticated flag value.
	 */
     void setAuthenticated(boolean val);
     
 	/**
 	 * getMethod: Returns the method (if any) associated with this route
 	 * @return String The input name.
 	 */  
 	Method getMethod();

 	/**
 	 * setMethod: Sets the method when the route is attached to a method vs a class.
 	 * @param method
 	 */	
 	void setMethod(Method method);
}

