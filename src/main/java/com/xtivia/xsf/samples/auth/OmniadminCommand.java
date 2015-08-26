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

package com.xtivia.xsf.samples.auth;

import com.xtivia.xsf.core.annotation.Route;
import com.xtivia.xsf.core.commands.CommandResult;
import com.xtivia.xsf.core.commands.ICommand;
import com.xtivia.xsf.core.commands.IContext;
import com.xtivia.xsf.liferay.auth.Omniadmin;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/*
 This commands requires that the user being logged in and
 also is a member of the Administrators group
 */
@Component
@Route(uri="/needsomniadmin/echo/{last}/{first}", method="GET")
@Omniadmin
public class OmniadminCommand implements ICommand {

    public CommandResult execute(IContext context) {

        Map<String, String> data = new HashMap<String, String>();

        //inputs from path paramters
        String firstName = context.find("first");
        String lastName = context.find("last");
        data.put("first_name", firstName);
        data.put("last_name", lastName);
        data.put("command_name",this.getClass().getSimpleName());

        return new CommandResult().setSucceeded(true).setData(data).setMessage("");
    }
}
