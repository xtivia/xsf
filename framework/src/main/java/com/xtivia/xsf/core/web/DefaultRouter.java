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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.AntPathMatcher;

import com.xtivia.xsf.core.annotation.Route;
import com.xtivia.xsf.core.commands.CommandResult;
import com.xtivia.xsf.core.commands.ICommand;

/**
 * class DefaultRouter: Default router implementation.
 */
public class DefaultRouter implements IRouter  {
	
	private static final Logger logger = LoggerFactory.getLogger(DefaultRouter.class);

	/**
	 * _routes: List of defined routes.
	 */
	private List<IRoute> _routes = new ArrayList<IRoute> ();

	/**
	 * DefaultRouter: Constructor.
	 * @param commands
	 */
	@Autowired
	public DefaultRouter(Map<String,ICommand> commands) {
	  loadRoutes(commands);
	}

	/**
	 * getRoutingInfo: Returns the routing info for the given request and method.
	 * @param requestUri
	 * @param httpMethod
	 * @return RoutingInfo The routing info for the matching route or <code>null</code> 
	 * if no match was found.
	 */
	public RoutingInfo getRoutingInfo(String requestUri, String httpMethod) {

		// create a new path matcher instance.
		AntPathMatcher matcher = new AntPathMatcher();

		// define a map to hold parameter versions.
		Map<String, String> pathParams = null;

		// for each defined route
		for (IRoute route : _routes) {

			// extract the URI.
			String routeUri = route.getUri();

			// if the defined route uses the given http method and the URIs match
			if (httpMethod.equals(route.getHttpMethod()) && matcher.match(routeUri, requestUri)) {

				// extract the template parameters from the uri.
				pathParams = matcher.extractUriTemplateVariables(routeUri, requestUri);

				// define a new routing info object
				RoutingInfo routingInfo = new RoutingInfo();

				// set the values
				routingInfo.setRoute(route);
				routingInfo.setPathParameters(pathParams);

				// return the instance
				return routingInfo;
			}
		}

		// if we get here, no match.
		return null;
	}

	/**
	 * loadRoutes: Loads the routes from the command map.
	 * @param commands
	 */
	protected void loadRoutes(Map<String,ICommand> commands) {
		
		// for each entry in the map
		for (Entry<String, ICommand> entry : commands.entrySet()) {

			// extract the command object from the entry
			ICommand command = (ICommand) entry.getValue();

			// extract the command name (the spring bean name) for the entry
			String commandName = entry.getKey();
			
			// if no method-level @Route annotations exist for this class
			if (!this.scanMethods(commandName, command)) {

				// if the command has a @Route annotation at the class level
				if (command.getClass().isAnnotationPresent(Route.class)) {
					
					// extract the route from the annotation in the command.
					Route route = command.getClass().getAnnotation(Route.class);
	
					// load the annotated route data
					loadAnnotatedRoute(commandName, route);
					
				} else {
					// if the command is an instance of the IDynamicRoute interface
	                if (command instanceof IDynamicRoute) {
		                // load the dynamic route
	                    loadDynamicRoute((IDynamicRoute)command);
	                } else if (logger.isDebugEnabled()) {
		                logger.debug("Command object Spring bean named [" + commandName + 
		                		     "] has no routing information and will not be processed.");
	                }
	            }
			}
		}
	}

	/**
	 * loadAnnotatedRoute: Processing of an annotation-based command route.
	 * @param commandName Name of the spring bean that implements the ICommand interface for the route.
	 * @param annotation
	 * @return IRoute the newly created/loaded route
	 */
	protected IRoute loadAnnotatedRoute(String commandName, Route annotation) {
		// create a new default route instance to hold the route details.
		DefaultRoute newRoute = new DefaultRoute();

		// set the information into the default route object
		newRoute.setCommandName(commandName);
		newRoute.setCached(annotation.cached());
		newRoute.setHttpMethod(annotation.method());
		newRoute.setUri(annotation.uri());
        newRoute.setAuthenticated(annotation.authenticated());

		String inputKey = annotation.inputKey();
        String inputClass = annotation.inputClass();

		// If the input key and class are defined...
        if (inputKey != null && inputClass != null && inputKey.length() > 0 && inputClass.length() > 0) {
	        try {
		        // save the key
		        newRoute.setInputName(inputKey);

		        // let the class loader load the class for the input holder.
		        newRoute.setInputClass(Class.forName(annotation.inputClass()));
	        } catch (ClassNotFoundException e) {
		        logger.error("Class [" + inputClass + "] not found for command bean [" + 
	                          commandName + "] specified in annotation: " + e.getMessage(), e);
		        throw new IllegalArgumentException(
				        String.format("Class not found=%s for bean=%s", inputClass, commandName));
	        }
        }

		_routes.add(newRoute);
		return newRoute;
	}

	/**
	 * loadDynamicRoute: Loads a dynamic route (runtime defined routes).
	 * @param command
	 */
    protected void loadDynamicRoute(IDynamicRoute command) {
    	
	    // for each route exported from the command
        for (IRoute route : command.getRoutes()) {
	        // add the route to the list.
            _routes.add(route);
        }
    }
    
    /**
     * scanMethods: scans all methods in a command class looking for methods that match the
     * signature of ICommand and are annotated with @Route. Such methods become eligible for
     * dispatching to from the framework.
     * @param commandName the name of the Spring bean
     * @param command the Spring bean that implements ICommand
     * @return a boolean indicating whether or not this command includes at least one dispatchable method
     */
    protected boolean scanMethods(String commandName,ICommand command) {
    	boolean foundAnnotatedMethod = false;
    	for (Method method : command.getClass().getDeclaredMethods()) {
			if (isDispatchable(method)) {
				Route annotation = method.getAnnotation(Route.class);
				if (annotation != null) {
					IRoute route = loadAnnotatedRoute(commandName,annotation);
					route.setMethod(method);
					Route classAnnotation = command.getClass().getAnnotation(Route.class);
					if (classAnnotation != null) {
						mergeAnnotations(route,classAnnotation,annotation);
					}
					foundAnnotatedMethod = true;
				}
			}
    	}
    	return foundAnnotatedMethod;
    }
    
    /**
     * isDispatchable : determines whether a method can be dispatched to from XSF. In essence this
     * means that the method must implement the same signature as execute()from ICommand, i.e.,
     * accepts a single IContext parameter and returns a result of type CommandResult
     * @param method a method from a command to test for being dispatchable from XSF
     * @return a boolean indicating whether the method is dispatchable from XSF
     */
	protected boolean isDispatchable(Method method) {
		
		Class<?>[] paramTypes = method.getParameterTypes();
		if (paramTypes.length > 1) return false;
		if (!paramTypes[0].getName().equals("com.xtivia.xsf.core.commands.IContext")) return false;

		Class<?> returnType = method.getReturnType();
		if (returnType.isAssignableFrom(CommandResult.class)) return true;
		else return false;
	}
	
	/**
	 * mergeAnnotation : merges the method and class @Route annotations into a single composite 
	 * form in the IRoute object
	 * @param route the route for the method
	 * @param classAnnotation
	 * @param methodAnnotation
	 */
	protected void mergeAnnotations(IRoute route, Route classAnnotation, Route methodAnnotation) {
		
		// merge the uri from the two annotations into a composite form
		String childUri = route.getUri();
		String parentUri = classAnnotation.uri();
		if (parentUri != null) route.setUri(parentUri+childUri);
		
		// if the method annotation is non-default (false) use that, else use the class annotation
		if (methodAnnotation.authenticated() == false) {
			route.setAuthenticated(false);
		} else {
			route.setAuthenticated(classAnnotation.authenticated());
		}
	}
}