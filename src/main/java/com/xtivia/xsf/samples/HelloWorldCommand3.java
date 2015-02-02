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

import java.util.Calendar;

import org.springframework.stereotype.Component;

import com.xtivia.xsf.core.annotation.Route;
import com.xtivia.xsf.core.commands.CommandResult;
import com.xtivia.xsf.core.commands.ICommand;
import com.xtivia.xsf.core.commands.IContext;
import com.xtivia.xsf.samples.model.SampleInput;
import com.xtivia.xsf.samples.model.SampleOutput;

/*
 * Sample URL for this command = http://localhost:8080/delegate/xsf/hello/world3/2742
 * Sample JSON input for this command below .... 
 *
{
  "inputText" : "foobar",
  "inputNumber" : 22,
  "inputDate" : "2015-01-06T20:23:38"
}
 */

@Component("helloCommand3")
@Route(uri="/hello/world3/{id}", method="POST", 
       inputKey="inputData", inputClass="com.xtivia.xsf.samples.model.SampleInput")

public class HelloWorldCommand3 implements ICommand {

	@Override
	public CommandResult execute(IContext context) {
		
		SampleOutput output = new SampleOutput();
		
		//inputs from path paramters
		String id = context.find("id");
		
		//inputs from posted JSON (marshalled to Java object)
		SampleInput input = context.find("inputData");
		if (input == null) {
			return new CommandResult().setSucceeded(false).setMessage("No inputs were detected");
		}
		
		output.setId(id);
		output.setCount(input.getInputNumber()+1);
		output.setText(input.getInputText().toUpperCase());
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(input.getInputDate());
		
		output.setDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK));
		output.setMonth(calendar.get(Calendar.MONTH));
		
		return new CommandResult().setSucceeded(true).setData(output).setMessage("");
	}
}
