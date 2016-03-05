package com.xtivia.xsf.core.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.User;
import com.xtivia.xsf.liferay.LiferayCommandContext;

/**
 * 
 * Class that extends and is functionally equivalent to the base Liferay context
 * except that it allows us to short-circuit calls into PortalUtil in a testing environment).
 * At the moment really just a stub but could be expanded later to provide more 
 * configurability in a test environment........
 *
 */
public class MockLiferayCommandContext extends LiferayCommandContext {
	
	private static final long serialVersionUID = 1L;

	public MockLiferayCommandContext(HttpServletRequest  request,
			                         HttpServletResponse response, 
			                         Map<String, String> pathParameters) {
		super(request, response, pathParameters);
	}

	@Override
	protected User getUser(HttpServletRequest request) throws SystemException, PortalException {
		return null;
	}

	@Override
	protected long getCompanyId(HttpServletRequest request) {
        return 10157L;
	}
}
