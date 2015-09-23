package xsf.samples.auth;

import java.util.List;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Organization;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.RoleConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.xtivia.xsf.core.commands.IContext;
import com.xtivia.xsf.liferay.ILiferayCommandKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * class XsfAuthUtil: A utility class to support authentication checks.
 */
public class XsfAuthUtil {
	private static final Logger _logger = LoggerFactory.getLogger(XsfAuthUtil.class);

	/**
	 * isOmniAdmin: Determines if the user is an administrator.
	 *
	 * @param ctx Context for the check.
	 * @return boolean <code>true</code> if the user is an administrator, otherwise they are not.
	 */
	public static boolean isOmniAdmin(IContext ctx) {
		// get the user from the context
		User user = ctx.find(ILiferayCommandKeys.LIFERAY_USER);

		// if user is not found then they are not logged into liferay,
		// and a guest is never an administrator.
		if (user == null) {
			return false;
		}

		// See if the user is an administrator for the company.
		try {
			return RoleLocalServiceUtil.hasUserRole(user.getUserId(),
					user.getCompanyId(),
					RoleConstants.ADMINISTRATOR,
					true);

		} catch (SystemException e) {
			_logger.error("Error checking user for administrator role: " + e.getMessage(), e);
		} catch (PortalException e) {
			_logger.error("Error checking user for administrator role: " + e.getMessage(), e);
		}

		// if we get here user is not an admin
		return false;
	}

	/**
	 * hasPortalRole: Determines if the user has the given portal role.
	 *
	 * @param ctx      Context for the check.
	 * @param rolename Role to check for.
	 * @return boolean <code>true</code> if the user has the given role, otherwise they do not.
	 */
	public static boolean hasPortalRole(IContext ctx, String rolename) {

		// get the user from the context
		User user = ctx.find(ILiferayCommandKeys.LIFERAY_USER);

		// if user is not found then they are not logged into liferay,
		// and a guest will never have the role.
		if (user == null) {
			return false;
		}

		// See if the user has the role in the company.
		try {
			return RoleLocalServiceUtil.hasUserRole(user.getUserId(),
					user.getCompanyId(),
					rolename, true);

		} catch (SystemException e) {
			_logger.error("Error checking user for " + rolename + " role: " + e.getMessage(), e);
		} catch (PortalException e) {
			_logger.error("Error checking user for " + rolename + " role: " + e.getMessage(), e);
		}

		// if we get here user does not have the role
		return false;
	}

	/**
	 * userInOrganization: Determines if the user is a member of the named organization.
	 *
	 * @param ctx     Context for the check.
	 * @param orgname Name of the organization to check for membership.
	 * @return boolean <code>true</code> if the user is a member of the organization, otherwise they are not.
	 */
	public static boolean userInOrganization(IContext ctx, String orgname) {

		// get the user from the context
		User user = ctx.find(ILiferayCommandKeys.LIFERAY_USER);

		// if user is not found then they are not logged into liferay,
		// and a guest will never be a member of the organization.
		if (user == null) {
			return false;
		}

		try {
			// get the users organizations
			List<Organization> organizations = user.getOrganizations();

			// check each org to see if the name matches
			for (Organization organization : organizations) {
				if (organization.getName().equals(orgname)) {
					// user is a member
					return true;
				}
			}
		} catch (SystemException e) {
			_logger.error("Error checking user for " + orgname + " membership: " + e.getMessage(), e);
		} catch (PortalException e) {
			_logger.error("Error checking user for " + orgname + " membership: " + e.getMessage(), e);
		}

		// if we get here user is not a member of the org.
		return false;
	}

	/**
	 * userInOrganizationRole: Determines if the user has the given organizational role.
	 *
	 * @param ctx      Context for the check.
	 * @param orgname  Name of the organization to check for the role.
	 * @param rolename Organization role to check for.
	 * @return boolean <code>true</code> if the user has the org role, otherwise they do not.
	 */
	public static boolean userInOrganizationRole(IContext ctx,
	                                             String orgname,
	                                             String rolename) {

		// get the user from the context
		User user = ctx.find(ILiferayCommandKeys.LIFERAY_USER);

		// if user is not found then they are not logged into liferay,
		// and a guest will never be a member of the organization and
		// therefore will not have the org role.
		if (user == null) {
			return false;
		}

		Organization foundOrg = null;

		try {
			// get the users organizations
			List<Organization> organizations = user.getOrganizations();

			// check each org to see if the name matches
			for (Organization organization : organizations) {
				if (organization.getName().equals(orgname)) {
					foundOrg = organization;
					break;
				}
			}

			// if the org is not in the user list, then they won't have the org.
			if (foundOrg == null) {
				return false;
			}

			// get the list of roles defined for the org
			List<Role> roles = RoleLocalServiceUtil.getUserGroupRoles(user.getUserId(),
					foundOrg.getGroupId());

			// check each one for a match.
			for (Role role : roles) {
				if (role.getName().equals(rolename)) {
					return true;
				}
			}
		} catch (SystemException e) {
			_logger.error("Error checking user for " + orgname + " role " + rolename + ": " + e.getMessage(), e);
		} catch (PortalException e) {
			_logger.error("Error checking user for " + orgname + " role " + rolename + ": " + e.getMessage(), e);
		}

		// if we get here user does not have the org role
		return false;
	}

}
