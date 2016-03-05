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

/**
 * interface ILiferayCommandKeys: Key values for accessing Liferay values from the context.
 */
public interface ILiferayCommandKeys {

	/**
	 * LIFERAY_USER: Key for the Liferay User object.
	 */
	String LIFERAY_USER       = "_LIFERAY_USER";
	
	/**
	 * LIFERAY_COMPANY_ID: Key for the Liferay company id.
	 */
	String LIFERAY_COMPANY_ID = "_LIFERAY_COMPANY_ID";
	
	/**
	 * LIFERAY_PERMISSION_CHECKER: Key for the Liferay PermissionChecker instance.
	 */
    String LIFERAY_PERMISSION_CHECKER = "_LIFERAY_PERMISSION_CHECKER";
}
