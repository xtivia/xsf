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
package com.xtivia.xsf.core.web;

import org.springframework.stereotype.Component;

import com.xtivia.xsf.core.annotation.Route;
import com.xtivia.xsf.core.commands.CommandResult;
import com.xtivia.xsf.core.commands.ICommand;
import com.xtivia.xsf.core.commands.IContext;

@Component
@Route(uri="/testing/{last}/{first}", method="GET", authenticated=false)
public class GetCommand implements ICommand {

	@Override
	public CommandResult execute(IContext context) {
		CommandResult cr = new CommandResult();
		cr.setSucceeded(true);
		TestResource tr = new TestResource();
		tr.setText(this.getClass().getName());
		cr.setData(tr);
		cr.setMessage("GET");
		return cr;
	}

}
