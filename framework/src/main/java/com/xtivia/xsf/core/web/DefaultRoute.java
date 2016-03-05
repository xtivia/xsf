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

import org.apache.commons.lang.Validate;

/**
 * class DefaultRoute: The default route.
 */
public class DefaultRoute implements IRoute {

	/**
	 * uri: URI for the route that this object manages.
	 */
	private String     uri;
	/**
	 * httpMethod: The method for the route.
	 */
	private String httpMethod;
	/**
	 * commandName: The name of the Spring bean that implements the ICommand interface for the route handler.
	 */
	private String     commandName;
	/**
	 * inputClass: The java class which will hold the data parsed by the Jackson JSON parser for incoming requests.
	 */
	private Class<?>   inputClass;
	/**
	 * inputName: The input name.
	 */
	private String     inputName;
	/**
	 * cached: Flag indicating whether the results of this command should be cached by the browser.  When <code>false</code> the browser will invoke server every time.
	 */
	private boolean    cached=false;
	/**
	 * authenticated: Flag indicating whether the user must be authenticated to invoke the command or not.
	 */
    private boolean    authenticated=true;
    /*
     *  method: Optional method associated with the Route if the route is attaced to a specific method
     *  and not to the entire class.
     */
    
    private Method     method=null;

	/**
	 * DefaultRoute: Default constructor.
	 */
	public DefaultRoute() {
	}

	/**
	 * DefaultRoute: Constructor w/ initial values.
	 * @param uri
	 * @param httpMethod
	 * @param commandName
	 */
	public DefaultRoute(String     uri, 
			            String httpMethod,
			            String     commandName) {
		
		Validate.notNull(uri,"URI cannot be null in route definition");
		Validate.notNull(httpMethod,"Http Method cannot be null in route definition");
		Validate.notNull(commandName,"ICommand bean name cannot be null in route definition");
		
		this.uri = uri;
		this.httpMethod = httpMethod;
		this.commandName = commandName;
	}
	
	/**
	 * DefaultRoute: Constructor used for routes that accept an input object via the POSTed
	 *  request body. Needs to indicate what class to marshal that input into
	 *  and what to store it under (name) in the command context.
	 * @param uri
	 * @param httpMethod
	 * @param commandName
	 * @param inputClass
	 * @param inputName
	 */
	public DefaultRoute(String     uri, 
			            String httpMethod,
			            String     commandName, 
			            Class<?>   inputClass, 
			            String     inputName) {
		this(uri,httpMethod,commandName);
		Validate.notNull(inputClass);
		Validate.notNull(inputName);
		this.inputClass = inputClass;
		this.inputName = inputName;
	}

	/**
	 * getUri: Returns the URI for the route.
	 * @return String The URI.
	 */
	@Override
	public String getUri() {
		return uri;
	}

	/**
	 * setUri: Sets the URI.
	 * @param uri The URI.
	 */
	@Override
	public void setUri(String uri) {
		this.uri = uri;
	}

	/**
	 * getHttpMethod: Returns the HTTP method for the route.
	 * @return String The method for the route.
	 */
	public String getHttpMethod() {
		return httpMethod;
	}

	/**
	 * setHttpMethod: Sets the HTTP method for the route.
	 * @param httpMethod The method to use.
	 */
	@Override
	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}

	/**
	 * getCommandName: Returns the command name.
	 * @return String The command name.
	 */
	@Override
	public String getCommandName() {
		return commandName;
	}

	/**
	 * setCommandName: Sets the command name.
	 * @param commandName The command name.
	 */
	@Override
	public void setCommandName(String commandName) {
		this.commandName = commandName;
	}

	/**
	 * getInputClass: Returns the input class used to contain the JSON object in a request.  Basically what object Jackson will be reading the JSON into.
	 * @return Class The class for the container for the input data.
	 */
	@Override
	public Class<?> getInputClass() {
		return inputClass;
	}

	/**
	 * setInputClass: Sets the input class for the route.
	 * @param inputClass The input class.
	 */
	@Override
	public void setInputClass(Class<?> inputClass) {
		this.inputClass = inputClass;
	}

	/**
	 * getInputName: Returns the name of the input element from the JSON, the name is the key and the value of the key will be parsed into an InputClass instance.
	 * @return String The input name.
	 */
	@Override
	public String getInputName() {
		return inputName;
	}

	/**
	 * setInputName: Sets the input name.
	 * @param inputName The input name.
	 */
	@Override
	public void setInputName(String inputName) {
		this.inputName = inputName;
	}

	/**
	 * isCached: Returns the value of the cached flag.  If true, the response can be cached by the browser (or intermediate cache appliance).
	 * @return boolean <code>true</code> if caching is allowed, otherwise it is not.
	 */
	@Override
	public boolean isCached() {
      return cached;
	}

	/**
	 * setCached: Sets the value of the caching flag.
	 * @param val The new cached flag value.
	 */
	@Override
	public void setCached(boolean val) {
		this.cached=val;
	}

	/**
	 * isAuthenticated: Returns the value of the authenticated flag.  If true, user must be authenticated to access the route, otherwise it is public guest route.
	 * @return boolean <code>true</code> if authentication is required, otherwise it is not.
	 */
    @Override
    public boolean isAuthenticated() {return authenticated;}

	/**
	 * setAuthenticated: Sets the authenticated flag.
	 * @param authenticated
	 */
    @Override
    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

	/**
	 * getMethod: Returns the method (if any) associated with this route
	 * @return String The input name.
	 */  
	@Override
	public Method getMethod() {
		return this.method;
	}

	/**
	 * setMethod: Sets the method when the route is attached to a method vs a class.
	 * @param method
	 */	
	@Override
	public void setMethod(Method method) {
        this.method = method;		
	}
}
