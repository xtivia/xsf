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

import javax.servlet.http.HttpServletRequest;

import com.xtivia.xsf.core.commands.ICommand;
import com.xtivia.xsf.core.commands.IContext;
import com.xtivia.xsf.core.web.ICommandKeys;
import com.xtivia.xsf.core.web.IRoute;

/**
 * class DefaultAuthorizer: The default authorizer used in the application.
 */
public class DefaultAuthorizer implements IAuthorizer {

	/**
	 * authorize: Default authorizer that allows access to public routes or proxies check to the command.
	 * @param route Route to be authorized.
	 * @param command Command to be authorized.
	 * @param context Context for the authorization.
	 * @return boolean <code>true</code> if authorized otherwise not allowed.
	 */
    @Override
    public boolean authorize(IRoute route, ICommand command, IContext context) {
        
        if (!route.isAuthenticated()) {
            return true;  // public routes always pass authentication check
        } else {
    		HttpServletRequest request = context.find(ICommandKeys.HTTP_REQUEST);
            if (request.getRemoteUser() == null) {
            	return false;
            }
        }

	    // if the command implements IAuthorized, proxy a call to it to let it evaluate the authorization.
        if (command instanceof IAuthorized) {
            return ((IAuthorized) command).authorize(context);
        }

	    // if we get here then we are allowing access.
        return true;
    }
}
