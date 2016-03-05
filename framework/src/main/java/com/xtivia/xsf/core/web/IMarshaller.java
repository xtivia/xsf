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

import com.xtivia.xsf.core.commands.CommandResult;
import com.xtivia.xsf.core.commands.IContext;

/**
 * class IMarshaller: Interface defining a transformer that can marshal requests, responses and exceptions.
 */
public interface IMarshaller {

	/**
	 * fromRequest: Marshals the request object into a format usable by the command.
	 * @param context Context for the request.
	 * @param routeUri URI for the request.
	 * @param requestBody Body of the request.
	 * @param clazz The java class for the object that will be populated from Jackson from the JSON request.
	 * @return ProcessedInput The parsed and processed input from the request.
	 */
	ProcessedInput fromRequest(IContext context,
			                   String   routeUri,
			                   byte[]   requestBody,
			                   Class<?> clazz);

	/**
	 * toResponse: Marshals into a command result object.
	 * @param context Context for the command.
	 * @param routeUri URI for the command.
	 * @param route The route for the command.
	 * @param commandResult The command result object.
	 */
	void toResponse(IContext       context,
		            String         routeUri,
		            IRoute         route,
		            CommandResult  commandResult);

	/**
	 * onRouteNotFound: Invoked when the incoming route was not found.
	 * @param context Context for the command.
	 * @param routeUri URI that was requested.
	 */
	void onRouteNotFound(IContext context,
			             String   routeUri);

	/**
	 * onException: Invoked when an exception is encountered during route processing.
	 * @param context Context for the route.
	 * @param routeUri URI for the command.
	 * @param route Route that was being processed.
	 * @param exception Exception that was encountered.
	 */
	void onException(IContext  context,
	                 String    routeUri,
	                 IRoute    route,
	                 Exception exception);

	/**
	 * onAuthorizationFailure: Invoked when the user is not authorized to invoke the route.
	 * @param context Context for the route.
	 * @param routeUri URI for the requested route.
	 * @param route Route that was requested.
	 */
    void onAuthorizationFailure(IContext context,
                                String   routeUri,
                                IRoute   route);
}
