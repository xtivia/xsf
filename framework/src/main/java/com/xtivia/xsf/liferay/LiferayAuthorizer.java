package com.xtivia.xsf.liferay;

import com.liferay.portal.model.User;
import com.xtivia.xsf.core.auth.IAuthorized;
import com.xtivia.xsf.core.auth.IAuthorizer;
import com.xtivia.xsf.core.commands.ICommand;
import com.xtivia.xsf.core.commands.IContext;
import com.xtivia.xsf.core.web.IRoute;

/**
 * class LiferayAuthorizer: The default authorizer used when running in Liferay. Instead of checking for remote user
 * in request it will look for an instance of a Liferay user object in the context.
 */
public class LiferayAuthorizer implements IAuthorizer {

	/**
	 * authorize: Default authorizer that allows access to public routes or proxies check to the command.
	 * @param route Route to be authorized.
	 * @param command Command to be authorized.
	 * @param context Context for the authorization.
	 * @return boolean <code>true</code> if authorized otherwise not allowed.
	 */	
	@Override
	public boolean authorize(IRoute route, ICommand command, IContext context) {
		
        if (!route.isAuthenticated()) {
            return true;  // public routes always pass authentication check
        } else {
        	User user = context.find(ILiferayCommandKeys.LIFERAY_USER);
        	if (user == null) {
         		return false;
        	}
        }

	    // if the command implements IAuthorized, proxy a call to it to let it evaluate the authorization.
        if (command instanceof IAuthorized) {
            return ((IAuthorized) command).authorize(context);
        }
        
	    // if we get here then we are allowing access.
        return true;
	}

}
