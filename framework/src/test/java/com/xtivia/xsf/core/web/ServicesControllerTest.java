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

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Date;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

/*
 * ServicesControllerTest : JUnit test case that spins up a embedded Jetty server and then runs
 * full HTTP-based tests against the entire JEE/Spring/XSF stack. Leverages rest-assured
 * testing tool to invoke services and check returned results.
 * 
 * This file may seem to be a bit lengthy, but the intent is to pull all tests that require
 * the spinup/shutdown of the Jetty server here in this single file so as to avoid multiple
 * start/stops of the server during unit testing.
 */
public class ServicesControllerTest {
	
	@BeforeClass
	public static void startServer() throws Exception {
		JettyServer.startIfRequired();
	}
	
	@AfterClass
	public static void stopServer() throws Exception {
		JettyServer.stop();
	}

	/*
	 *  Basic testing of the Services Controller logic
	 */
	@Test
	public void testGetOk() throws Exception {
	   given().
	     contentType("application/json").
	   when().  
         get("/delegate/xsf/testing/bloggs/joe").
       then().
           assertThat().statusCode(200).
             assertThat().contentType("application/json").
           
           body("message",   equalTo("GET"),
                "succeeded", equalTo(true),
                "data.text", equalTo("com.xtivia.xsf.core.web.GetCommand"));		   
	}
	
	@Test
	public void testDeleteOk() throws Exception {	
	   given().
	     contentType("application/json").
	   when().  
         delete("/delegate/xsf/testing/bloggs/joe").
       then().
         assertThat().statusCode(200).	
         assertThat().contentType("application/json").
         body("message",   equalTo("DELETEjoebloggs"),
              "succeeded", equalTo(true),
              "data.text", equalTo("com.xtivia.xsf.core.web.DeleteCommand"));	
    }
	
	@Test
	public void testPostOk() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		StringWriter sw = new StringWriter();
		String textToEcho = "Hello Posted World";
		BigDecimal bdValue = new BigDecimal("3.33");
		mapper.writeValue(sw, new TestResource(textToEcho,bdValue,new Date()));
		
		  given().
		     contentType("application/json").
		     body(sw.toString()).
		   when().  
             post("/delegate/xsf/testing_post/bloggs/joe").
           then().
             assertThat().statusCode(200).
             assertThat().contentType("application/json").	
             body("message",    equalTo("POST"+textToEcho),
                  "data.text",  equalTo("com.xtivia.xsf.core.web.PostCommand"),
                  "data.rate",  equalTo(4.33f),
                  "succeeded",  equalTo(true));
	}
	
	@Test
	public void testPutOk() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		StringWriter sw = new StringWriter();
		String textToEcho = "Hello Posted World";
		BigDecimal bdValue = new BigDecimal("3.33");
		mapper.writeValue(sw, new TestResource(textToEcho,bdValue,new Date()));
		
		given().
		     contentType("application/json").
		     body(sw.toString()).
		when().  
          put("/delegate/xsf/testing_put/static/route").
        then().
          assertThat().statusCode(200).
          assertThat().contentType("application/json").	
          body("message",    equalTo("PUT"+textToEcho),
               "data.text",  equalTo("com.xtivia.xsf.core.web.PutCommand"),
               "data.rate",  equalTo(-0.67f),
               "succeeded",  equalTo(true));
	}

	@Test
	public void testGetBadRoute() throws Exception {
		 given().
		     contentType("application/json").
		 when().  
		     head("/delegate/xsf/badroute/bloggs/joe").
         then().
             assertThat().statusCode(404);
	}
	
	@Test
	public void testGetBadMethod() throws Exception {		
		   given().
		     contentType("application/json").
		   when().  
		     head("/delegate/xsf/badmethod/bloggs/joe").
           then().
             assertThat().statusCode(404);
	}	
	
	/*
	 *  Testing of method-based routing
	 */
	@Test
	public void testDispatchedGetOk() throws Exception {
	   given().
	     contentType("application/json").
	   when().  
         get("/delegate/xsf/methods/get").
       then().
           assertThat().statusCode(200).
           assertThat().contentType("application/json").
           body("message",   equalTo("GET"),
                "succeeded", equalTo(true),
                "data.Hello", equalTo("World"));		   
	}
	
	@Test
	public void testDispatchedGetWithAuthOk() throws Exception {
	   given().
	     contentType("application/json").
	   when().  
         get("/delegate/xsf/methods/getWithAuth").
       then().
           assertThat().statusCode(200).
           assertThat().contentType("application/json").
           body("message",   equalTo("Authorization fails for route=/methods/getWithAuth"),
                "succeeded", equalTo(false));		   
	}
	
	@Test
	public void testDispatchedPostOk() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		StringWriter sw = new StringWriter();
		String textToEcho = "Hello Posted World";
		BigDecimal bdValue = new BigDecimal("3.33");
		mapper.writeValue(sw, new TestResource(textToEcho,bdValue,new Date()));
		
	    given().
	      contentType("application/json").
	      body(sw.toString()).
	    when().  
          post("/delegate/xsf/methods/post/bloggs/joe").
        then().
          assertThat().statusCode(200).
          assertThat().contentType("application/json").	
          body("message",    equalTo("POST_METHOD"+textToEcho),
              "data.text",  equalTo("com.xtivia.xsf.core.web.MethodRoutesCommand"),
              "data.rate",  equalTo(4.33f),
              "succeeded",  equalTo(true));	   
	}
	
}