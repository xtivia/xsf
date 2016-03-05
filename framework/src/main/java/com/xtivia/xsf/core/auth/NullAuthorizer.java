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

import com.xtivia.xsf.core.commands.ICommand;
import com.xtivia.xsf.core.commands.IContext;
import com.xtivia.xsf.core.web.IRoute;

/**
 * class NullAuthorizer: Basically a no-op authorizer that approves access to everyone.
 */
public class NullAuthorizer implements IAuthorizer {

	/**
	 * authorize: Implementation.
	 * @param route Route to be authorized.
	 * @param command Command to be authorized.
	 * @param context Context for the authorization.
	 * @return boolean Always returns <code>true</code>.
	 */
    @Override
    public boolean authorize(IRoute route, ICommand command, IContext context) {
        return true;
    }
}
