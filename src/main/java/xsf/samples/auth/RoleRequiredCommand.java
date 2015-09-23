package xsf.samples.auth;

import java.util.HashMap;
import java.util.Map;

import com.xtivia.xsf.core.annotation.Route;
import com.xtivia.xsf.core.auth.IAuthorized;
import com.xtivia.xsf.core.commands.CommandResult;
import com.xtivia.xsf.core.commands.ICommand;
import com.xtivia.xsf.core.commands.IContext;

/*
 This command requires that the user being logged into
 Liferay and is a member of at least one of the specified
 groups in the @PortalRole annotation. Additionally we also
 allow omniadmins to access this command
 */
@Route(uri="/needsportalrole/echo/{last}/{first}", method="GET", authenticated=true)
public class RoleRequiredCommand implements ICommand, IAuthorized {

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

	@Override
	public boolean authorize(IContext context) {
		
		// allow portal admins to use this command
		if (XsfAuthUtil.isOmniAdmin(context)) return true;
		
		// check to use if the user has any one of the required portal/regular roles
		String[] rolesToCheck = {"SomeRole", "PortalTestRole"};
		for (String role : rolesToCheck) {
			if (XsfAuthUtil.hasPortalRole(context,role)) {
				return true;
			}
		}
		
		return false;
	}
    
}
