/**
 * Copyright (c) 2015 Xtivia, Inc. All rights reserved.
 * <p/>
 * This file is part of the Xtivia Services Framework (XSF) library.
 * <p/>
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.xtivia.xsf.liferay;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.User;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.util.PortalUtil;
import com.xtivia.xsf.core.web.ICommandKeys;
import com.xtivia.xsf.core.web.WebCommandContext;

/**
 * LiferayCommandContext: An extension to the WebCommandContext to handle Liferay objects.  This should not be implemented
 * as an IContextDecorator interface because we really want this to be defined as part of the initial context where we
 * can control the ordering.
 */
@SuppressWarnings("serial")
public class LiferayCommandContext extends WebCommandContext {
	
	private static final Logger _logger = LoggerFactory.getLogger(LiferayCommandContext.class);

	/**
	 * LiferayCommandContext: Constructor.
	 * @param request
	 * @param response
	 * @param pathParameters
	 */
	public LiferayCommandContext(HttpServletRequest request,
	                             HttpServletResponse response,
	                             Map<String, String> pathParameters) {

		// let the super class do the construction
		super(request, response, pathParameters);

		try {
			// extract the user from the request
			User user = this.getUser(request);

			if (user != null) {
				super.put(ILiferayCommandKeys.LIFERAY_USER, user);
			}

			// extract the company id from the request
			Long companyId = this.getCompanyId(request);
			if (companyId != null) {
				super.put(ILiferayCommandKeys.LIFERAY_COMPANY_ID, companyId);
			}

		} catch (Exception e) {
			_logger.error("Error extracting user: " + e.getMessage(), e);
		}
	}

	/**
	 * find: Override method to support lazy (on-demand) retrieveal of permission checker.
	 * @param key The key for the value to find.
	 * @param <T> Class of the return object.
	 * @return T The found object or <code>null</code>.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T find(String key) {
		// let the super class find the object.
		T val = super.find(key);

		// If the key is for the permission checker but it was not found in the context
		if ((val == null) && (StringUtils.equals(key,ILiferayCommandKeys.LIFERAY_PERMISSION_CHECKER))) {

			// extract the request object from the context.
			HttpServletRequest request = super.find(ICommandKeys.HTTP_REQUEST);

			// if not found there isn't much we can do as a result.
			if (request == null) {
				return null;
			}

			// try to get the user from the incoming request
			User user = null;

			try {
				user = this.getUser(request);
			} catch (PortalException e) {
				_logger.error("Error getting user from request: " + e.getMessage(), e);
			} catch (SystemException e) {
				_logger.error("Error getting user from request: " + e.getMessage(), e);
			}

			// if the user was found
			if (user != null) {

				try {
					// create a new permission checker for the user.
					PermissionChecker permissionChecker = this.getPermissionChecker(user);

					// set it in the context so we don't create a new one later on
					super.put(ILiferayCommandKeys.LIFERAY_PERMISSION_CHECKER, permissionChecker);

					// return the permission checker
					return (T) permissionChecker;
				} catch (Exception e) {
					_logger.error("Error creating permission checker: " + e.getMessage(), e);
				}
			}

			// if we get here the we did not find a permission checker or could not create one from the user, so
			// little we can do but allow the null object to be returned.
		}

		return val;
	}
	
	// the following methods are defined as protected
	// methods that can be overridden in derived classes
	// also enables mocking these methods in a unit
	// test environment
	
	/**
	 * getUser: returns current Liferay user, typically via call to PortalUtil
	 * @param request
	 */
	protected User getUser(HttpServletRequest request) throws SystemException, PortalException {
		return PortalUtil.getUser(request);
	}
	
	/**
	 * getCompanyId: returns current Liferay companyId, typically via call to PortalUtil
	 * @param request
	 */
	protected long getCompanyId(HttpServletRequest request) {
		return PortalUtil.getCompanyId(request);
	}
	
	/**
	 * getPermissionChecker: returns a Liferay permissionChecker based on current user
	 * @param user for which permission checker is to be created
	 */
	protected PermissionChecker getPermissionChecker(User user) throws Exception {
		return PermissionCheckerFactoryUtil.create(user);
	}
}
