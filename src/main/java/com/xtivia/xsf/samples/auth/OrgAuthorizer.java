package com.xtivia.xsf.samples.auth;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Organization;
import com.liferay.portal.model.User;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.service.OrganizationLocalServiceUtil;
import com.xtivia.xsf.core.auth.AuthContext;
import com.xtivia.xsf.core.commands.IContext;
import com.xtivia.xsf.liferay.ILiferayCommandKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import com.xtivia.xsf.core.auth.IAuthorizer;

/*
  An example authenticator for Liferay. Loops through all of the organizations
  that the logged in user belongs to and checks to see if the user has the
  VIEW permission for Blogs assigned to him/her in any of those orgs
 */
public class OrgAuthorizer implements IAuthorizer {

    private static Logger _logger = LoggerFactory.getLogger(OrgAuthorizer.class);

    @Override
    public boolean authorize(AuthContext authContext, IContext context) {
        try {
            User user = context.find(ILiferayCommandKeys.LIFERAY_USER);
            if (user == null) {
                return false;
            }
            PermissionChecker permissionChecker = authContext.find(ILiferayCommandKeys.LIFERAY_PERMISSION_CHECKER);
            if (permissionChecker == null) {
                _logger.error("Could not find permission checker for user="+user.getEmailAddress());
            }
            List<Organization> userOrgs =
                    OrganizationLocalServiceUtil.getUserOrganizations(user.getUserId());
            for (Organization userOrg : userOrgs) {
                boolean hasPermission =
                        permissionChecker.hasPermission(userOrg.getGroupId(), "33", 0, "VIEW");
                if (hasPermission) {
                    return true;
                }
            }
        }  catch (SystemException e) {e.printStackTrace(); }

        return false;
    }
}
