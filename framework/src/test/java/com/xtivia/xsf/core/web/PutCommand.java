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

import org.springframework.stereotype.Component;

import com.xtivia.xsf.core.annotation.Route;
import com.xtivia.xsf.core.commands.CommandResult;
import com.xtivia.xsf.core.commands.ICommand;
import com.xtivia.xsf.core.commands.IContext;

@Component
@Route(uri="/testing_put/static/route", method="PUT", authenticated=false,
       inputKey="inputData", inputClass="com.xtivia.xsf.core.web.TestResource")
public class PutCommand implements ICommand {

	@Override
	public CommandResult execute(IContext context) {
		CommandResult cr = new CommandResult();
		cr.setSucceeded(true);
		TestResource tr_in = context.find("inputData");
		TestResource tr_out = new TestResource();
		tr_out.setText(this.getClass().getName());
		tr_out.setRate(tr_in.getRate().subtract(new BigDecimal(4.0)));
		cr.setData(tr_out);
		cr.setMessage("PUT"+tr_in.getText());
		return cr;
	}
}
