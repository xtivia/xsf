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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.xtivia.xsf.core.annotation.Route;
import com.xtivia.xsf.core.commands.CommandResult;
import com.xtivia.xsf.core.commands.ICommand;
import com.xtivia.xsf.core.commands.IContext;

//@Route(uri="/methods", authenticated=false)
@Route(uri="/methods")
public class MethodRoutesCommand implements ICommand {
	
	@Route(uri="/get",authenticated=false)
	public CommandResult doGet(IContext ctx) {
		Map<String,String> data = new HashMap<String,String>();
		data.put("Hello", "World");
		CommandResult cr = new CommandResult();
		cr.setSucceeded(true);
		cr.setData(data);
		cr.setMessage("GET");
		return cr;
	}
	
	@Route(uri="/getWithAuth",authenticated=true)
	public CommandResult doAuthenticatedGet(IContext ctx) {
		Map<String,String> data = new HashMap<String,String>();
		data.put("Hello", "World");
		CommandResult cr = new CommandResult();
		cr.setSucceeded(true);
		cr.setData(data);
		cr.setMessage("GET");
		return cr;
	}
	
	@Route(uri="/post/{last}/{first}", method="POST",
		   authenticated=false,
		   inputKey="inputData", inputClass="com.xtivia.xsf.core.web.TestResource")
	public CommandResult doPost(IContext ctx) {
		CommandResult cr = new CommandResult();
		cr.setSucceeded(true);
		TestResource tr_in = ctx.find("inputData");
		TestResource tr_out = new TestResource();
		tr_out.setText(this.getClass().getName());
		tr_out.setRate(tr_in.getRate().add(new BigDecimal(1)));
		cr.setData(tr_out);
		cr.setMessage("POST_METHOD"+tr_in.getText());
		return cr;		
	}

	@Override
	public CommandResult execute(IContext context) {
		return Xsf.dispatch(this, context);
	}

}
