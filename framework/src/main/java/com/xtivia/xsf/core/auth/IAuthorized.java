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
package com.xtivia.xsf.core.auth;

import com.xtivia.xsf.core.commands.IContext;

/**
 * class IAuthorized: Interface used to allow command beans to check whether the user is authorized.
 */
public interface IAuthorized {

	/**
	 * authorize: Checks whether the user is authorized.
	 * @param context Context for the authorization check.
	 * @return boolean <code>true</code> if authorized, otherwise not authorized.
	 */
	boolean authorize(IContext context);
}
