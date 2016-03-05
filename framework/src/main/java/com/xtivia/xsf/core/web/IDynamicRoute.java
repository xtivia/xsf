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

import java.util.List;

/**
 * class IDynamicRoute: An interface that can be implemented by commands to indicate to the XSF framework that the
 * route(s) for this command are computed dynamically.  Instead of using the @Route annotation the framework will
 * invoke the getRoutes() method to return a list of Routes supported by the command.
 */
public interface IDynamicRoute {

	/**
	 * getRoutes: Returns the list of registered routes.
	 * @return List The list of registered routes.
	 */
    List<IRoute> getRoutes();
}
