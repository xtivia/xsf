package com.xtivia.xsf.core.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xtivia.xsf.liferay.LiferayServicesController;

/**
 * This is a mock controller that extends the Liferay services controller and
 * is functionally equivalent except that it returns a mock context (that
 * allows us to short-circuit calls into PortalUtil in a testing environment)
 * At the moment really just a stub but could be expanded later to provide more 
 * configurability in a test environment........
 */
public class MockLiferayServicesController extends LiferayServicesController {

	@Override
	protected WebCommandContext getWebContext(HttpServletRequest  request,
			                                  HttpServletResponse response, 
			                                  Map<String, String> pathParameters) {
		return new MockLiferayCommandContext(request, response, pathParameters);
	}
	
	

}
