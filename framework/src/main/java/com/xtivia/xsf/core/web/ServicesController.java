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

import java.io.InputStream;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ServletConfigAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.xtivia.xsf.core.auth.IAuthorizer;
import com.xtivia.xsf.core.auth.NullAuthorizer;
import com.xtivia.xsf.core.commands.CommandResult;
import com.xtivia.xsf.core.commands.ICommand;
import com.xtivia.xsf.core.commands.IContext;
import com.xtivia.xsf.core.commands.IFilter;

/**
 * class ServicesController: The main entry point into the XSF framework, it 
 * implements the controller Spring will dispatch to for every incoming request.
 */
public abstract class ServicesController implements Controller, ServletConfigAware { 
	
	private static final Logger _logger = LoggerFactory.getLogger(ServicesController.class);

	/**
	 * sc: The stored servlet config.
	 */
	private ServletConfig sc;

	/**
	 * uriPrefix: The main URI prefix.
	 */
	private String uriPrefix = null;

	/**
	 * DEFAULT_INPUT_NAME: Default input name for all route input handling.
	 */
	private static final String DEFAULT_INPUT_NAME = "INPUT";
	/**
	 * SUB_CONTEXT: The sub context servlet config param that Liferay's 
	 * DelegateServlet will be routing messages to us on.
	 */
	private static final String SUB_CONTEXT = "sub-context";

	/**
	 * applicationContext: The spring application context
	 */
	@Autowired private ApplicationContext applicationContext;
	/**
	 * router: The request router.
	 */
	@Autowired protected IRouter router;

	/**
	 * marshaller: The request/response marshaller.
	 */
	@Autowired protected IMarshaller marshaller;

	/**
	 * contextDecoratorMgr: list/manager for context decorators.
	 */	
	@Autowired protected ContextDecoratorManager contextDecoratorMgr;

	/**
	 * authorizer: The framework authorizer. Responsible for framework authorization
	 *  of individual commands/routes.
	 */
    protected IAuthorizer authorizer = new NullAuthorizer();
	
	/**
	 * setServletConfig: Sets the servlet config injected by Spring's ServletConfigAware interface.
	 * @param sc
	 */
	@Override
	public void setServletConfig(ServletConfig sc) {
		this.sc = sc;
	}
	
	/**
	 * handleRequest: Entry point for the spring controller.
	 * @param request
	 * @param response
	 * @return ModelAndView Always returns <code>null</code>.
	 * @throws Exception
	 */
	@Override
	public ModelAndView handleRequest(HttpServletRequest  request, 
	  		                          HttpServletResponse response) 
	  		                          throws              Exception {

		findAndExecuteCommand(request,response);
		return null;
	}

	/**
	 * findAndExecuteCommand: The main processor for the request.
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	protected void findAndExecuteCommand(HttpServletRequest  request,
			                             HttpServletResponse response) {
		// get the request URI
		String routeUri = stripUriPrefix(request.getRequestURI());	

		// find the RoutingInfo that should handle the request.
		RoutingInfo routingInfo = router.getRoutingInfo(routeUri,request.getMethod());

		// if the routing info lookup failed...
		if (routingInfo == null || 
			routingInfo.getRoute() == null || 
			routingInfo.getRoute().getCommandName() == null) {

			// the routing info was not found, report back to the caller.
			    onRouteNotFound(getFullWebContext(request, response, null),routeUri);
			    return;
		}

		// extract the route and the command name from the route.
		IRoute route = routingInfo.getRoute();
		String commandName = route.getCommandName();

        try {

	        // create a new command context with what we have.
            IContext ctx = getFullWebContext(request, response, routingInfo.getPathParameters());

	        // retrieve the command object based on the bean name.
            Object commandObject = applicationContext.getBean(commandName);

	        // if the spring bean is not found
            if (commandObject == null) {

                if (_logger.isDebugEnabled()) {
                  _logger.debug(String.format("Could not locate command bean named %s in context for uri=%s",
                                              commandName,routeUri));
                }

	            // treat as a route not found even though we do have a route config.
                onRouteNotFound(ctx, routeUri);
                return;
            }

	        // If the found bean is not an instance of ICommand
            if (!(commandObject instanceof ICommand)) {
                if (_logger.isDebugEnabled()) {
                      _logger.debug(String.format("Named command %s (%s) for uri=%s is not a ICommand object",
                                                  commandName,commandObject.getClass().getName(), routeUri));
                }

	            // treat as a route not found even though we have a spring bean
                onRouteNotFound(ctx, routeUri);
                return;
            }

	        // cast as a command object
            ICommand command = (ICommand) commandObject;
            
		    // save the routing info inside the context. This can be useful for commands
		    // that act as a sub-dispatcher or proxy and need to have information about what
		    // route was matched (and/or path parameters)
		    ctx.put(ICommandKeys.ROUTING_INFO,routingInfo);

	        // see if the authenticator allows access to the route
            boolean passesAuth = authorizer.authorize(route,command,ctx);

	        // if it does not
            if (!passesAuth) {
	            // return the authentication failure.
                marshaller.onAuthorizationFailure(ctx,routeUri,route);
                return;
            }

	        // extract the request body
		    byte[] requestBody = readRequestBody(request);

	        // Parse the request into a concrete object.
	        ProcessedInput processedInput = 
	        		marshaller.fromRequest(ctx, routeUri, requestBody, route.getInputClass());

	        // if the input can be processed
		    if (processedInput.isCanContinue()) {

			    // extract the input data object
		    	Object input = processedInput.getInputData();

			    // if the input data is not null
			    if (input != null ) {
				    // get the input name from either the route or use the defaut
		    	   String inputName = route.getInputName();
		    	   inputName = (inputName != null ? inputName : DEFAULT_INPUT_NAME);

				    // add the input name to the context
		    	   ctx.put(inputName, input);
			    }
			    
			    // let the command execute the result
		        CommandResult cr = command.execute(ctx);

			    // if the command implements the IFilter interface.
			    if (command instanceof IFilter) {
				    try {
					    // let the filter post-process the result.
					    ((IFilter) command).postProcess(cr, null);
				    } catch (Exception e) {
					    _logger.error("Error invoking filter on class [" + 
				                       command.getClass().getName() + "]: " + e.getMessage(), e);
				    }
			    }

			    // have the marshaller return the response
		        getMarshaller().toResponse(ctx, routeUri, route, cr);
		    }
			
		} catch (Exception e) {
	        _logger.error("Error processing route: " + e.getMessage(), e);

			marshaller.onException(getFullWebContext(request, response, null), routeUri, route, e);
		}
	}

	/**
	 * stripUriPrefix: Strip the non-route specific parts of the URL away, (e.g. /delegate/xsf)
	 * so that only the
	 * URIs defined in routes remain.
	 * @param uri
	 * @return String The URI with a stripped prefix.
	 */
	private String stripUriPrefix(String uri) {

		// if uriPrefix is null
		if (uriPrefix == null) {
			// use the default sub context
			String subContext = "xsf";

			// if we have a servlet config
			if (sc != null) {
				// try to get a value from the servlet config
			  String subContextFromSC = sc.getInitParameter(SUB_CONTEXT);

				// if we have a value then use it.
			  if (subContextFromSC != null) {
				subContext = subContextFromSC;  
			  }
			}

			// manufacture a uriPrefix
		    uriPrefix = String.format("/%s",subContext);
		}

		// look for the prefix in the URI
		int ndx = uri.indexOf(uriPrefix);

		// if not found, just return the uri.
		if (ndx == -1) {
			return uri;
		}

		// remove everything after the prefix.
		return uri.substring(ndx+uriPrefix.length());
	}
			
	/**
	 * readRequestBody: read the contents of a POST/PUT or return null if no body contents exist
	 * @param request
	 * @return byte[] The byte array of request data or <code>null</code> if none available.
	 * @throws Exception
	 */
	private byte[] readRequestBody(HttpServletRequest request) throws Exception {

        InputStream inputStream = null;
		byte[] data = null;

		try {
			inputStream = request.getInputStream();
			if (inputStream != null) {
			   data = IOUtils.toByteArray(inputStream);
			   // coerce an empty body into null for
			   // subsequent marshalling logic
			   if (data != null && data.length == 0) {
				   data = null;
			   }
			}
		} finally {
			IOUtils.closeQuietly(inputStream);
		}

		return data;
	}

	/**
	 * getMarshaller: Returns the marshaller instance.
	 * @return IMarshaller The marshaller instance.
	 */
	protected IMarshaller getMarshaller() {
		return this.marshaller;
	}

	/**
	 * getAuthorizer: Returns the authorizer instance.
	 * @return IAuthorizer The authorizer instance.
	 */
    public IAuthorizer getAuthorizer() {
        return authorizer;
    }

	/**
	 * setAuthorizer: Sets the authorizer to use.
	 * @param authorizer
	 */
    public void setAuthorizer(IAuthorizer authorizer) {
        this.authorizer = authorizer;
    }

	/**
	 * getFullWebContext: Returns the full web context including any modification by registered decorators.
	 * @param request
	 * @param response
	 * @param pathParameters
	 * @return IContext The full context instance.
	 */
	protected IContext getFullWebContext(HttpServletRequest  request,
	                                              HttpServletResponse   response,
	                                              Map<String,String>    pathParameters) {

		// start by getting the initial context.
		IContext context = getWebContext(request, response, pathParameters);

		// allow for context decoration
		if (contextDecoratorMgr != null) {
			context = contextDecoratorMgr.decorateContext(context);
		}

		return context;
	}

	/**
	 * getWebContext: Gets a web context from the given data.
	 * @param request
	 * @param response
	 * @param pathParameters
	 * @return IContext The context instance to use for the command.
	 */
    protected IContext getWebContext(HttpServletRequest  request,
                                               HttpServletResponse   response,
                                               Map<String,String>    pathParameters) {

         return new WebCommandContext(request,response,pathParameters);
    }

	/**
	 * onRouteNotFound: Returns the route not found message to the caller.
	 * @param ctx
	 * @param routeUri
	 */
	protected void onRouteNotFound(IContext ctx, String routeUri) {

		marshaller.onRouteNotFound(ctx, routeUri);
	}

	/**
	 * getRouter: Returns the router for the service controller.
	 * @return IRouter The router instance.
	 */
	public IRouter getRouter() {
		return router;
	}
}
