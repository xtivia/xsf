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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xtivia.xsf.core.commands.CommandContext;
import com.xtivia.xsf.core.commands.IContext;

/**
 * class WebCommandContext: Extension of the simple CommandContext implementation to add support for the web objects.
 */
@SuppressWarnings({ "serial", "rawtypes" })
public class WebCommandContext extends CommandContext implements IContext {

	/**
	 * request: The request instance.
	 */
	private HttpServletRequest request;
	/**
	 * updates: Set of updated keys used to track required changes to the request attributes.
	 */
	private HashSet updates = new HashSet();

	/**
	 * WebCommandContext: Constructor.
	 * @param request
	 * @param response
	 * @param pathParameters
	 */
	@SuppressWarnings("unchecked")
	public WebCommandContext(HttpServletRequest request,
                             HttpServletResponse response,
                             Map<String,String>  pathParameters) {
		this.request = request;

		super.put(ICommandKeys.HTTP_REQUEST,request);
        super.put(ICommandKeys.HTTP_RESPONSE,response);
        super.put(ICommandKeys.HTTP_SESSION,request.getSession());
		super.put(ICommandKeys.SERVLET_CONTEXT,request.getSession().getServletContext());

		if (pathParameters != null) {
			super.put(ICommandKeys.PATH_PARAMETERS, pathParameters);
		}
	}

	/**
	 * getRequest: Returns the request object.
	 * @return HttpServletRequest The request instance.
	 */
	public HttpServletRequest getRequest() {
		return this.request;
	}

	/**
	 * get: Returns an attribute from the context or the path parameters or request attributes or request parameters or the session or servlet context.
	 * @param key
	 * @return Object The found object or <code>null</code>.
	 */
	public Object get(Object key) {
		// simple null check to avoid NPEs.
		if (key == null) return null;

		// try the super class' get method and return it if found.
		Object o = super.get(key);
		if (o != null) return o;

		// not in the super context, try the path parameters.
		Map pathParameters = (Map) super.get(ICommandKeys.PATH_PARAMETERS);
        if (pathParameters != null) {
	        // check path parameters and return if found.
            o = pathParameters.get(key);
            if (o != null) {
                return o;
            }
        }

		// not in path params, check the request attributes and return if found.
		o = request.getAttribute(key.toString());
		if (o != null) return o;

		// not in request attribs, check request parameters and return if found.
		o = request.getParameterValues(key.toString());
		if (o != null) {
			// we only want to return a single string if it is not really an array.
			String[] arr = (String[]) o;
			if (arr.length > 1) return arr;
			else return arr[0];
		}

		// not in the request parameters, check the session and return if found.
		o = request.getSession().getAttribute(key.toString());
		if (o != null) return o;

		// Not in the session, last chance is if it is in the servlet context attributes.
		o = request.getSession().getServletContext().getAttribute(key.toString());		

		// will return either the found object or <code>null</code> if it was not in the servlet context attribs.
		return o;
	}

	/**
	 * put: Puts an object into the map.
	 * @param key
	 * @param value
	 * @return Object object added to map
	 */
	@SuppressWarnings("unchecked")
	public Object put(Object key, Object value) {
		// add the key to the update list.
		updates.add(key);

		// let the super class do the add.
		return super.put(key, value);
	}

	/**
	 * getUpdates: Returns the set of keys which have been updated since the initial object creation.
	 * @return Set returns keys updated since initial object creation
	 */
	public Set getUpdates() {
		return updates;
	}

	/**
	 * unload: For any changed keys in the map, this pushes the change into the request attributes.
	 */
	public void unload() {
		// get the updates
		Set updates = getUpdates();

		// for each updated key
		for (Iterator iter = updates.iterator(); iter.hasNext();) {
			// extract the key/value objects
			String key = (String) iter.next();
			Object value = super.get(key);

			// null objects should not be handled.
			if (value == null) continue;

			// save as a request attribute.
			request.setAttribute(key,value);
		}	
	}
}

