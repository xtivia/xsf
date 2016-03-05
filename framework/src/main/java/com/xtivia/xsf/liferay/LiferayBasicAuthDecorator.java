/**
 * Copyright (c) 2015 Xtivia, Inc. All rights reserved.
 * <p/>
 * This file is part of the Xtivia Services Framework (XSF) library.
 * <p/>
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.xtivia.xsf.liferay;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.model.Company;
import com.liferay.portal.security.auth.Authenticator;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.xtivia.xsf.core.commands.IContext;
import com.xtivia.xsf.core.web.ICommandKeys;
import com.xtivia.xsf.core.web.IContextDecorator;

/**
 * XsfBasicAuth: An implementation of the IContextDecorator that will process BASIC authorization
 * header and populate the context with an instance of the corresponding Liferay user if the 
 * supplied credentials match an existing user. Leverages the Lifeay auth pipeline for authentication.
 */
public class LiferayBasicAuthDecorator implements IContextDecorator {
	
	private static Logger _logger = LoggerFactory.getLogger(LiferayBasicAuthDecorator.class);
	
	private boolean useScreenName = false;
	
	/**
	 * @return the useScreenName boolean (instead of email address)
	 */
	public boolean isUseScreenName() {
		return useScreenName;
	}

	/**
	 * @param useScreenName set the boolean for useScreenName (instead of email address)
	 */
	public void setUseScreenName(boolean useScreenName) {
		this.useScreenName = useScreenName;
	}

	/**
	 * decorateContext: Top level function invoked by the XSF framework for context decoration.
	 * @param context the XSF context.
	 */
	@Override
	public IContext decorateContext(IContext context) {
		// if user is already logged in normally then do nothing
		Object object = context.get(ILiferayCommandKeys.LIFERAY_USER);
		if (object == null) {
			processBasicAuth(context);
		}
		return context;
	}

	/**
	 * processBasicAuth: Performs discovery and initial splitting of the BASIC auth header.
	 * @param context the XSF context.
	 */	
	protected void processBasicAuth(IContext context) {
		
		HttpServletRequest request = context.find(ICommandKeys.HTTP_REQUEST);
		
		// Get the Authorization header, if one was supplied

		String authorization = request.getHeader("Authorization");
		
		if (authorization != null) {
			
			StringTokenizer st = new StringTokenizer(authorization);

			if (st.hasMoreTokens()) {
				
				String basic = st.nextToken();
				
				// We only handle HTTP Basic authentication

				if (basic.equalsIgnoreCase(HttpServletRequest.BASIC_AUTH)) {
					
					String encodedCredentials = st.nextToken();
					
					if (_logger.isDebugEnabled()) {
						_logger.debug("Encoded credentials for XSF BASIC AUTH are " + encodedCredentials);
					}
					
                    loadCredentialsInContext(request,encodedCredentials,context);
				}
			}
		}
	}

	/**
	 * loadCredentialsInContext: Decodes BASIC header and attempts to authenticate user in Liferay.
	 *                           
	 * @param request the HTTP servlet request previously extracted from the XSF context.
	 * @param encodedCredentials the BASIC Auth credentials (encoded) previously extracted from the request.
	 * @param context the XSF context.
	 */		
	@SuppressWarnings("unchecked")
	protected void loadCredentialsInContext(HttpServletRequest request,
			                                String             encodedCredentials, 
			                                IContext           context) {
		
		String decodedCredentials = new String(Base64.decode(encodedCredentials));

		if (_logger.isDebugEnabled()) {
			_logger.debug("Decoded credentials are " + decodedCredentials);
		}

		int pos = decodedCredentials.indexOf(':');
		
		if (pos != -1) {
			
			String login = GetterUtil.getString(decodedCredentials.substring(0, pos));
			String password = decodedCredentials.substring(pos + 1);
			
			try{
				Company company = PortalUtil.getCompany(request);
				if (company == null) return;
				
				Map<String, String[]> headerMap = new HashMap<String, String[]>();

				Enumeration<String> enu1 = request.getHeaderNames();

				while (enu1.hasMoreElements()) {
					String name = enu1.nextElement();

					Enumeration<String> enu2 = request.getHeaders(name);

					List<String> headers = new ArrayList<String>();

					while (enu2.hasMoreElements()) {
						String value = enu2.nextElement();
						headers.add(value);
					}

					headerMap.put(name, headers.toArray(new String[headers.size()]));
				}

				Map<String, String[]> parameterMap = request.getParameterMap();
				Map<String, Object> resultsMap = new HashMap<String, Object>();
				
				if (!useScreenName) {
					int authResult = UserLocalServiceUtil.
						authenticateByEmailAddress(company.getCompanyId(), login, password, 
							                       headerMap, parameterMap, resultsMap);
					
					if (authResult == Authenticator.SUCCESS) { 
						
						context.put(ILiferayCommandKeys.LIFERAY_USER,
								    UserLocalServiceUtil.getUserByEmailAddress(company.getCompanyId(), login));
					}
				} else {
					int authResult = UserLocalServiceUtil.
							authenticateByScreenName(company.getCompanyId(), login, password, 
								                     headerMap, parameterMap, resultsMap);
						
					if (authResult == Authenticator.SUCCESS) { 
						
						context.put(ILiferayCommandKeys.LIFERAY_USER,
								    UserLocalServiceUtil.getUserByScreenName(company.getCompanyId(), login));
					}
				}
				
			} catch (Exception e) {_logger.error("Exception in BASIC auth handler",e);}				
		}
	}
	
}
