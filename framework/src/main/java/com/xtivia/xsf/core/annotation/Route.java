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
package com.xtivia.xsf.core.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.stereotype.Component;

/**
 * class Route: Annotation defining the route.
 */
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface Route  {
	/**
	 * uri: The URI for the route.
	 */
	String   uri();

	/**
	 * method: The HTTP method for the route.
	 */
	String   method()        default "GET";

	/**
	 * inputClass:
	 */
	String   inputClass()    default "";

	/**
	 * inputKey:
	 */
    String   inputKey()      default "";

	/**
	 * cached: Flag indicating whether content is cached or not.
	 */
    boolean  cached()        default false;

	/**
	 * authenticated: Flag indicating whether authenticated access is required or not (public guest accessable).
	 */
    boolean  authenticated() default true;
}