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

/**
 * class OrgAuthorizedCommand: A command that requires that a user is logged in and is a member of an organization.
 * <p/>
 * Per our configuration, any incoming GET request to http://localhost:8080/delegate/xsf/needsorgrole/echo/Bloggs/Joe
 * will be handled by this command object.
 */
@Route(uri = "/needsorgrole/echo/{last}/{first}", method = "GET", authenticated = true)
public class OrgAuthorizedCommand implements ICommand, IAuthorized {
	private static final Logger _logger = LoggerFactory.getLogger(OrgAuthorizedCommand.class);

	/**
	 * execute: Handles the processing of the request.  The code here matches the HelloWorldCommand's execute() method,
	 * it doesn't do anything extra.
	 *
	 * @param context Context for the request.
	 * @return CommandResult The result of the command execution.
	 */
	@Override
	public CommandResult execute(IContext context) {
		Map<String, String> data = new HashMap<String, String>();

		//inputs from path paramters
		String firstName = context.find("first");
		String lastName = context.find("last");
		data.put("first_name", firstName);
		data.put("last_name", lastName);
		data.put("command_name", getClass().getSimpleName());

		return new CommandResult().setData(data);
	}

	/**
	 * authorize: This is the method defined in the IAuthorized interface and allows the command a chance to determine if
	 * the user is authorized.  Loops through all of the organizations that the logged in user belongs to and checks to
	 * see if the user has the VIEW permission for Blogs assigned to him/her in any of those orgs.
	 *
	 * @param context Context for the request.
	 * @return boolean <code>true</code> if the user is authorized, otherwise they are not.
	 */
	@Override
	public boolean authorize(IContext context) {
		try {
			// Extract the user from the current context
			User user = context.find(ILiferayCommandKeys.LIFERAY_USER);

			// if we have no user, then user is not logged in and they are not authorized.
			// should not happen since we've specified that the user must be logged in via
			// the @Route annotation above, but the additional check can't hurt.
			if (user == null) {
				return false;
			}

			// Get the permission checker for the user.
			PermissionChecker permissionChecker = context.find(ILiferayCommandKeys.LIFERAY_PERMISSION_CHECKER);
			if (permissionChecker == null) {
				_logger.error("Could not find permission checker for user=" + user.getEmailAddress());
				return false;
			}

			// Get the list of organizations the user is a member of.
			List<Organization> userOrgs =
					OrganizationLocalServiceUtil.getUserOrganizations(user.getUserId());

			// for each organization
			for (Organization userOrg : userOrgs) {
				// check if they have blog view permission
				boolean hasPermission =
						permissionChecker.hasPermission(userOrg.getGroupId(), "33", 0, "VIEW");

				// if they have the permission, then the user is authorized to access the command.
				if (hasPermission) {
					return true;
				}
			}
		} catch (SystemException e) {
			_logger.warn("Error evaluating user authorization: " + e.getMessage(), e);
		}

		return false;
	}
}
