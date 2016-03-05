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

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xtivia.xsf.core.commands.CommandResult;
import com.xtivia.xsf.core.commands.IContext;

/**
 * class DefaultMarshaller: Default implementation of the IMarshaller interface.
 */
public class DefaultMarshaller implements IMarshaller {
	
	private static final Logger _logger = LoggerFactory.getLogger(DefaultMarshaller.class);

	/**
	 * JSON_DATE_FORMAT: The date format string for parsing JSON dates.
	 */
	private static final String JSON_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
	/**
	 * CONTENT_TYPE_JSON: Static string for the json content mime type.
	 */
	private static final String CONTENT_TYPE_JSON = "application/json";

	/**
	 * logInOut: Flag indicating whether to log entry and exit points in the marshaller.
	 */
	private boolean logInOut = true;
	/**
	 * rawJson: Flag indicating whether raw JSON is the expected input object.
	 */
	private boolean rawJson  = false;

	/**
	 * isLogInOut: Returns the logInOut flag.
	 * @return boolean
	 */
	public boolean isLogInOut() {
		return logInOut;
	}

	/**
	 * setLogInOut: Sets the logInOut flag.
	 * @param logInOut
	 */
	public void setLogInOut(boolean logInOut) {
		this.logInOut = logInOut;
	}

	/**
	 * isRawJson: Returns the rawJson flag.
	 * @return boolean
	 */
	public boolean isRawJson() {
		return rawJson;
	}

	/**
	 * setRawJson: Sets the rawJson flag.
	 * @param rawJson
	 */
	public void setRawJson(boolean rawJson) {
		this.rawJson = rawJson;
	}

	/**
	 * fromRequest: Marshals into the ProcessedInput.
	 * @param context Context for the request.
	 * @param routeUri URI for the request.
	 * @param requestBody Body of the request.
	 * @param clazz The class of the value type that represents the input data.
	 * @return ProcessedInput The input object.
	 */
	@Override
	public ProcessedInput fromRequest(IContext  context,
			                          String    routeUri,
			                          byte[]    requestBody,
			                          Class<?>  clazz) {

		// create a new input object.
		ProcessedInput processedInput = new ProcessedInput(false,null);
		
		try {

			// if logInOut flag log the message.
			if (logInOut) {
				logInput(String.format("Received services request %s %s",getRequestMethod(context),routeUri));
			}

			// no input found in request body so return null and keep going
			if (requestBody == null) {
				return new ProcessedInput(true,null);
			}

			// convert the request body into a string.
			String json = new String(requestBody);

			// if logInOut log the json input
			if (logInOut) logInput(json);

			// create a new Jackson JSON object mapper
			ObjectMapper mapper = new ObjectMapper();

			//NOTE: SimpleDateFormat is not thread safe so it must be a new one each time
			mapper.setDateFormat(new SimpleDateFormat(JSON_DATE_FORMAT));

			// read the input data from the object mapper and set the continue flag.
		    processedInput.setInputData(mapper.readValue(json,clazz));
		    processedInput.setCanContinue(true);
		    
		} catch (Exception e) {

			// log the error
			_logger.error(String.format("Error parsing input for uri=%s",
					                    routeUri),e);
			setResponseStatus(context, HttpURLConnection.HTTP_OK);
			setResponseContentType(context, CONTENT_TYPE_JSON);
			try {
				// write the failure response.
				writeResponse(context, HttpURLConnection.HTTP_OK, CONTENT_TYPE_JSON, new CommandResult(false, null, "Error parsing input JSON"));

			} catch (IOException ex) {
				// This is a bad case - an error in extracting the input and an error in reporting the error back to the caller.
				_logger.error(String.format("IO Exception in parsing error response for uri=%s",
	                          routeUri),ex);
			}
		}

		// return the processed input result.
		return processedInput;
	}

	/**
	 * writeResponse: Utility method to write the response.
	 * @param context
	 * @param status
	 * @param type
	 * @param data
	 * @throws IOException
	 */
	protected void writeResponse(final IContext context, final int status, final String type, final Object data) throws IOException {
		writeResponse(context, status, type, false, false, data);
	}

	/**
	 * writeResponse: Utility method to write the response.
	 * @param context
	 * @param status
	 * @param type
	 * @param addCacheHeaders
	 * @param data
	 * @throws IOException
	 */
	protected void writeResponse(final IContext context, final int status, final String type, final boolean addCacheHeaders, final boolean logOut, final Object data) throws IOException {

		setResponseStatus(context, status);
		setResponseContentType(context, type);

		if (addCacheHeaders) writeCacheSuppressionHeaders(context);

		// get the output stream to write into
		OutputStream os = getResponseOutputStream(context);

		// create a new Jackson JSON object mapper
		ObjectMapper mapper = new ObjectMapper();

		//NOTE: SimpleDateFormat is not thread safe so it must be a new one each time
		mapper.setDateFormat(new SimpleDateFormat(JSON_DATE_FORMAT));

		if (logOut) {
			// create a string writer for the data
			StringWriter sw = new StringWriter();

			// write the value into the string writer
			mapper.writeValue(sw, data);

			// extract the value from the string writer
			String s = sw.toString();

			// log it
			logOutput(s);

			// and then write to the output stream.
			os.write(s.getBytes());
		} else {
			// write JSON to the output stream by converting the command result.
			mapper.writeValue(os, data);
		}

		// flush the response
		os.flush();
	}

	/**
	 * toResponse: Generates the response into the output stream.
	 * @param context Context for the command.
	 * @param routeUri URI for the command.
	 * @param route The route for the command.
	 * @param commandResult The command result object.
	 */
	@Override
	public void toResponse(IContext      context,
					       String        routeUri,
					       IRoute        route,
					       CommandResult commandResult) {

		// get the return object, either the raw unparsed json or the command result object.
		Object toReturn = rawJson ? commandResult.getData() : commandResult;

		try {
			// write the object to the response
			writeResponse(context,HttpURLConnection.HTTP_OK,CONTENT_TYPE_JSON,!route.isCached(),logInOut, toReturn);
		} catch (IOException ex) {
			_logger.error(String.format("IO Exception in writing response for uri=%s",routeUri),ex);
		}
	}

	/**
	 * onRouteNotFound: Called when the route was not found.
	 * @param context Context for the command.
	 * @param routeUri URI that was requested.
	 */
	@Override
	public void onRouteNotFound(IContext context, String   routeUri) {
			         
		/*
		 *  A not-found error always results in a 404 regardless of what the rawJson setting is
		 *  set to. This has to be implemented this way because a not-found might occur due
		 *  to a bad method, for example, and in the case of a HEAD request we need to send
		 *  back a 404 because HEAD returns no body content. The safest path was decided to
		 *  simply always return a 404 for all not-found cases.
		 */
		
		setResponseStatus(context, HttpURLConnection.HTTP_NOT_FOUND);
		_logger.error(String.format("Requested command not found for uri=%s", routeUri));
	}

	/**
	 * onException: Called when an exception is thrown by the command processor.
	 * @param context Context for the route.
	 * @param routeUri URI for the command.
	 * @param route Route that was being processed.
	 * @param exception Exception that was encountered.
	 */
	@Override
	public void onException(IContext  context,
			                String    routeUri,
			                IRoute    route,
			                Exception exception) {

		// if raw json to be returned, just set the response status and return.
		if (rawJson) {
		    setResponseStatus(context, HttpURLConnection.HTTP_INTERNAL_ERROR);
		    return;
		}
			        
		CommandResult cr;

		_logger.error(String.format("Exception on route=%s",routeUri),exception);

		// assumption is that most of the IllegalArgumentExceptions are being thrown by
		// validation logic and that the error messages would be useful to JS code 
		// running on the client so send that message on through ....
		if (exception instanceof IllegalArgumentException) {
			cr= new CommandResult().setSucceeded(false)
	                               .setMessage(exception.getMessage());
		} else {
		// .... but for all other exceptions send a generic error message
			cr = new CommandResult().setSucceeded(false)
	                                .setMessage(STANDARD_ERROR_MESSAGE);
		}

		// write the response to the output stream.
		toResponse(context, routeUri, route, cr);
	}

	/**
	 * onAuthorizationFailure: Called when there is an authorization failure accessing the route.
	 * @param context Context for the route.
	 * @param routeUri URI for the requested route.
	 * @param route Route that was requested.
	 */
    @Override
    public void onAuthorizationFailure(IContext  context,
                                        String    routeUri,
                                        IRoute    route) {

    	// if raw json, just set the status and return.
		if (rawJson) {
		    setResponseStatus(context, HttpURLConnection.HTTP_FORBIDDEN);
		    return;
		}
		
	    // create the message
        String msg = String.format("Authorization fails for route=%s",routeUri);

	    // write the response to the output stream.
        toResponse(context, routeUri, route, new CommandResult().setSucceeded(false).setMessage(msg));
    }

    /*
    Optional logging of input and output
     */
    protected void logInput(String toLog) {
		    if (_logger.isDebugEnabled())
		    _logger.debug(toLog);
	}
	
	protected void logOutput(String toLog) {
			if (_logger.isDebugEnabled())
				_logger.debug(toLog);
	}

    /*
    The following functions are isolated here so that they can potentially be
    overriden when we need to deal with Portal Request/Responses in the context
    */

	/**
	 * writeCacheSuppressionHeaders: Sets the cache suspension headers in the response.
	 * @param context The context.
	 */
	protected void writeCacheSuppressionHeaders(IContext context) {
		// get the response object from the context
        HttpServletResponse response = context.find(ICommandKeys.HTTP_RESPONSE);

		if (response == null) return;

		// set the cache-suspension headers.
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
		response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
		response.setDateHeader("Expires", 0); // Proxies.
	}

	/**
	 * getRequestMethod: Returns the request method for the request.
	 * @param context
	 * @return String The request method.
	 */
    protected String getRequestMethod(IContext context) {
        HttpServletRequest request = context.find(ICommandKeys.HTTP_REQUEST);
        return request != null ? request.getMethod() : null;
    }

	/**
	 * getResponseOutputStream: Returns the response output stream.
	 * @param context
	 * @return OutputStream The output stream for the response.
	 * @throws IOException
	 */
    protected OutputStream getResponseOutputStream(IContext context) throws IOException {
        HttpServletResponse response = context.find(ICommandKeys.HTTP_RESPONSE);
        return (response != null ? response.getOutputStream() : null);
    }

	/**
	 * setResponseStatus: Sets the response status code.
	 * @param context
	 * @param status
	 */
    protected void setResponseStatus(IContext context, int status) {
        HttpServletResponse response = context.find(ICommandKeys.HTTP_RESPONSE);
        if (response != null) response.setStatus(status);
    }

	/**
	 * setResponseContentType: Sets the content type in the response.
	 * @param context
	 * @param contentType
	 */
    protected void setResponseContentType(IContext context, String contentType) {
        HttpServletResponse response = context.find(ICommandKeys.HTTP_RESPONSE);
        if (response != null) response.setContentType(contentType);
    }

	/**
	 * STANDARD_ERROR_MESSAGE: The standard error message string.
	 */
	public static final String STANDARD_ERROR_MESSAGE  = "An error occurred while processing the request";
	/**
	 * NOTFOUND_MESSAGE: The not found message string.
	 */
	public static final String NOTFOUND_MESSAGE        = "The requested command could not be located";
	/**
	 * AUTHERROR_MESSAGE: The authorization error message string.
	 */
	public static final String AUTHERROR_MESSAGE       = "An authorization error occurred while processing the request";
}
