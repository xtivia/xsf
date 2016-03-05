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

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * class CommandContext: Concrete implementation of IContext for managing command context.
 */
@SuppressWarnings({"serial", "rawtypes"})
public class CommandContext extends HashMap implements IContext {

	private static final Logger _logger = LoggerFactory.getLogger(CommandContext.class);

	/**
	 * find: Implementation of the find() method from the IContext interface.
	 * @param key The key for the value to find.
	 * @param <T> The type of object for auto-casting.
	 * @return T The found object or <code>null</code>.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T find(String key) {
		// pull the object out of the map.
		Object entity = get(key);

		if (entity != null) {
			// try to cast and return as desired type.
			try {
				return (T) entity;
			} catch (ClassCastException e) {
				_logger.error(String.format("Cannot cast %s to desired type in context", entity.getClass().getName()));
			}
		}

		return null;
	}
}
