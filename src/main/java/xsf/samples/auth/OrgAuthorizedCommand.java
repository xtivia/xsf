package xsf.samples.auth;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Organization;
import com.liferay.portal.model.User;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.service.OrganizationLocalServiceUtil;
import com.xtivia.xsf.core.annotation.Route;
import com.xtivia.xsf.core.auth.IAuthorized;
import com.xtivia.xsf.core.commands.CommandResult;
import com.xtivia.xsf.core.commands.ICommand;
import com.xtivia.xsf.core.commands.IContext;
import com.xtivia.xsf.liferay.ILiferayCommandKeys;

/*
 This command demonstrates using a customized, app-specific authenticators with the XSF framework. 
 */
@Route(uri="/needsorgrole/echo/{last}/{first}", method="GET", authenticated=true)
public class OrgAuthorizedCommand implements ICommand, IAuthorized {
	
	private Logger _logger = LoggerFactory.getLogger(OrgAuthorizedCommand.class);

    @Override
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

    /*
    An example authenticator for Liferay. Loops through all of the organizations
    that the logged in user belongs to and checks to see if the user has the
    VIEW permission for Blogs assigned to him/her in any of those orgs
   */
	@Override
	public boolean authorize(IContext context) {
       try {
            User user = context.find(ILiferayCommandKeys.LIFERAY_USER);
            if (user == null) return false;
            
            PermissionChecker permissionChecker = context.find(ILiferayCommandKeys.LIFERAY_PERMISSION_CHECKER);
            if (permissionChecker == null) {
                _logger.error("Could not find permission checker for user="+user.getEmailAddress());
                return false;
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
