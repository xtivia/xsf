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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xtivia.xsf.core.commands.CommandResult;
import com.xtivia.xsf.core.commands.ICommand;
import com.xtivia.xsf.core.commands.IContext;

/**
 * class Xsf: Global static utility functions.
 */
public class Xsf {
	
	private static final Logger logger = LoggerFactory.getLogger(Xsf.class);

	/**
	 * dispatch: Provide method-level dispatching for a command. Finds the method saved in 
	 * the route (stored in context) and dispatches to that method
	 * @param ctx
	 * @return CommandResult 
	 */
	public static CommandResult dispatch(ICommand targetObject, IContext ctx) {
		
		CommandResult cr = new CommandResult(false,"");
		
		RoutingInfo routingInfo = ctx.find(ICommandKeys.ROUTING_INFO);
		if (routingInfo == null) {
		  cr.setMessage("Method dispatch fails for unknown route");	
		  return cr;
		}
		
		IRoute route = routingInfo.getRoute();
		cr.setMessage("Unable to dispatch to route="+ route.getUri());
		
		Method method = route.getMethod();
		
		try {		    
			return (CommandResult) method.invoke(targetObject, ctx);	    
		} catch (InvocationTargetException e) {
			logger.error("Exception when method dispatching to " + route.getUri(),e);
			return cr;		
		} catch (IllegalAccessException e) {
			logger.error("Exception when method dispatching to " + route.getUri(),e);
			return cr;				
	    }
	}
}
