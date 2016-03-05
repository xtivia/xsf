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
package com.xtivia.xsf.core.commands;

import java.util.Map;

/**
 * class IContext: Interface extension to define a project-specific Map type.
 */
@SuppressWarnings("rawtypes")
public interface IContext extends Map {

	/**
	 * find: Finds the map value for the given key.
	 * @param key The key for the value to find.
	 * @param <T> The class the value object should be.
	 * @return T The found object from the context or <code>null</code> if not defined.
	 */
	<T> T find(String key);
}

