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

package com.xtivia.xsf.samples;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.xtivia.xsf.core.annotation.Route;
import com.xtivia.xsf.core.commands.CommandResult;
import com.xtivia.xsf.core.commands.ICommand;
import com.xtivia.xsf.core.commands.IContext;

@Component("helloCommand")
@Route(uri="/hello/world/{last}/{first}", method="GET")
public class HelloWorldCommand implements ICommand {

	@Override
	public CommandResult execute(IContext context) {
		
		Map<String,String> data = new HashMap<String,String>();
		
		//inputs from path paramters
		String firstName = context.find("first");
		String lastName = context.find("last");
		data.put("first_name", firstName);
		data.put("last_name", lastName);
		
		return new CommandResult().setSucceeded(true).setData(data).setMessage("");
	}
}
