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

package xsf.samples;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import xsf.samples.HelloWorldCommand2;

import com.liferay.portal.model.User;
import com.xtivia.xsf.core.commands.CommandContext;
import com.xtivia.xsf.core.commands.CommandResult;
import com.xtivia.xsf.liferay.ILiferayCommandKeys;


@RunWith(MockitoJUnitRunner.class)
public class HelloWorldCommand2Test {
	
	CommandContext context = new CommandContext();
	HelloWorldCommand2 command = new HelloWorldCommand2();
	
	@Mock
	User mockLiferayUser;
	
	/*
	 * Test error handling for cases where no parameters are
	 * available in the context
	 */
	@Test
	public void testEmptyContextPath() {
	   boolean testOK = false;
	   try {
		   command.execute(context);
	   } catch (IllegalArgumentException e){
		   testOK = true;
	   }
	   assertEquals(testOK,true);
	}
	
	/*
	 * Test case where all parameters are supplied except
	 * middle name (which is a query parameter). Validate
	 * that middle name is defaulted and that the default
	 * value for Liferay user email is returned as no
	 * logged-in user is made available.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testSuccessWithNoQueryParam() {
		context.put("first", "Joe");
		context.put("last", "Bloggs");
		CommandResult cr = command.execute(context);
		assertEquals(cr.isSucceeded(),true);
		Map results = (Map) cr.getData();
		assertNotNull(results);
		String firstName = (String) results.get("first_name");
		String lastName = (String) results.get("last_name");
		String middleName = (String) results.get("middle_name");
		String userEmail = (String) results.get("user_email");
		assertEquals(firstName,"Joe");
		assertEquals(lastName,"Bloggs");
		assertEquals(middleName,"Not Available");
		assertEquals(userEmail, "Not authenticated");
	}

	/*
	 * Test case where all parameters are supplied. Validate
	 * that middle name is echo to match supplied value
	 * and that the default value for Liferay user email 
	 * is returned as no logged-in user is made available.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testSuccessWithQueryParam() {
		context.put("first", "Joe");
		context.put("last", "Bloggs");
		context.put("mname", "Lee");
		CommandResult cr = command.execute(context);
		assertEquals(cr.isSucceeded(),true);
		Map results = (Map) cr.getData();
		assertNotNull(results);
		String firstName = (String) results.get("first_name");
		String lastName = (String) results.get("last_name");
		String middleName = (String) results.get("middle_name");
		String userEmail = (String) results.get("user_email");
		assertEquals(firstName,"Joe");
		assertEquals(lastName,"Bloggs");
		assertEquals(middleName,"Lee");
		assertEquals(userEmail, "Not authenticated");
	}
	
	/*
	 * Test case where all parameters are supplied except
	 * middle name (which is a query parameter). Validate
	 * that middle name is defaulted and that the correct
	 * value for Liferay user email is returned based on
	 * mocked User supplied in context.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testSuccessWithNoQueryParamAndUser() {
		context.put("first", "Joe");
		context.put("last", "Bloggs");
		Mockito.when(mockLiferayUser.getEmailAddress()).thenReturn("xsf@xtivia.com");
		context.put(ILiferayCommandKeys.LIFERAY_USER, mockLiferayUser);
		CommandResult cr = command.execute(context);
		assertEquals(cr.isSucceeded(),true);
		Map results = (Map) cr.getData();
		assertNotNull(results);
		String firstName = (String) results.get("first_name");
		String lastName = (String) results.get("last_name");
		String middleName = (String) results.get("middle_name");
		String userEmail = (String) results.get("user_email");
		assertEquals(firstName,"Joe");
		assertEquals(lastName,"Bloggs");
		assertEquals(middleName,"Not Available");
		assertEquals(userEmail, "xsf@xtivia.com");
	}
}
