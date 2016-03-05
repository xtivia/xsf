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
 * class IFilter: Defines a post processor invoked after command completion to allow for transformation or other post processing of the result.
 */
public interface IFilter {

	/**
	 * postProcess: Entry point for the filter.
	 * @param commandResult The command result to be processed.
	 * @param exception A possible exception encountered during command execution.
	 */
	void postProcess(CommandResult commandResult, Exception exception);
}

