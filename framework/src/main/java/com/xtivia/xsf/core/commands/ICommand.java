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

/**
 * class ICommand: The interface defining a command entry point.
 */
public interface ICommand {
	
	/**
	 * execute: Entry point for the command to do it's thing.
	 * @param context Context for the command.
	 * @return CommandResult The result of the command execution.
	 */
	CommandResult execute(IContext context) throws Exception;

}
