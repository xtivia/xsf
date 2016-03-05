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
package com.xtivia.xsf.liferay;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xtivia.xsf.core.commands.IContext;
import com.xtivia.xsf.core.web.ServicesController;

/**
 * class LiferayServicesController: An extension controller for use under Liferay.
 */
public class LiferayServicesController extends ServicesController {
	
	public LiferayServicesController() {
		super();
		this.authorizer = new LiferayAuthorizer();
	}
	
	
	/**
	 * getWebContext: Returns a context instance given the params.
	 * @param request
	 * @param response
	 * @param pathParameters
	 * @return IContext The context instance.
	 */
    @Override
    protected IContext getWebContext(HttpServletRequest  request,
                                              HttpServletResponse response,
                                              Map<String,String>  pathParameters) {

        return new LiferayCommandContext(request,response,pathParameters);
    }
}
