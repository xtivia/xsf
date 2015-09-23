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

public class XsfAuthUtil {
	
	//------ isOmniAdmin--------------
	public static boolean isOmniAdmin(IContext ctx) {
		
		User user = ctx.find(ILiferayCommandKeys.LIFERAY_USER);
        if (user == null) return false;
        
        try {
	        return RoleLocalServiceUtil.hasUserRole(user.getUserId(),
	                                                user.getCompanyId(),
	                                                RoleConstants.ADMINISTRATOR,
	                                                true);
	        
        } catch (SystemException e) {e.printStackTrace(); return false;
	    } catch (PortalException e) {e.printStackTrace(); return false;}
	}
	
	//------- hasPortalRole --------------
	public static boolean hasPortalRole(IContext ctx, String rolename) {
		
        User user = ctx.find(ILiferayCommandKeys.LIFERAY_USER);
        if (user == null) return false;
		
        try {    
          return RoleLocalServiceUtil.hasUserRole(user.getUserId(),
        		                                  user.getCompanyId(),
        		                                  rolename, true);
            
        } catch (SystemException e) {e.printStackTrace(); return false;
        } catch (PortalException e) {e.printStackTrace(); return false; }
	}
	
	//-------- userInOrganization --------------
	public static boolean userInOrganization(IContext ctx, String orgname) {
			
        User user = ctx.find(ILiferayCommandKeys.LIFERAY_USER);
        if (user == null) return false;
        
        try {
	        List<Organization> organizations = user.getOrganizations();
		    for (Organization organization : organizations){
				if (organization.getName().equals(orgname)){
				    return true;
				}
		    }
		    
			return false;           
        } catch (SystemException e) {e.printStackTrace(); return false;
        } catch (PortalException e) {e.printStackTrace(); return false; }
	}
		
	//-------- userInOrganizationRole --------------	
	public static boolean userInOrganizationRole(IContext ctx, 
			                                     String   orgname, 
			                                     String   rolename) {

        User user = ctx.find(ILiferayCommandKeys.LIFERAY_USER);
        if (user == null) return false;
        
        Organization foundOrg = null;
        
        try {
	        List<Organization> organizations = user.getOrganizations();
		    for (Organization organization : organizations){
				if (organization.getName().equals(orgname)){
				    foundOrg = organization;
				    break;
				}
		    }
		    
			if (foundOrg == null) return false; 
			
			List<Role> roles = RoleLocalServiceUtil.getUserGroupRoles(user.getUserId(), 
					                                                  foundOrg.getGroupId());
			for (Role role: roles) {
				if (role.getName().equals(rolename)) {
					return true;
				}
			}
			
			return false;
			
			
        } catch (SystemException e) {e.printStackTrace(); return false;
        } catch (PortalException e) {e.printStackTrace(); return false; }		
	}
	
}
