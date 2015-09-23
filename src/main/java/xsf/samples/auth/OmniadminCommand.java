package xsf.samples.auth;

import java.util.HashMap;
import java.util.Map;

import com.xtivia.xsf.core.annotation.Route;
import com.xtivia.xsf.core.auth.IAuthorized;
import com.xtivia.xsf.core.commands.CommandResult;
import com.xtivia.xsf.core.commands.ICommand;
import com.xtivia.xsf.core.commands.IContext;

/*
 This commands requires that the user being logged in and
 also is a member of the Administrators group
 */
@Route(uri="/needsomniadmin/echo/{last}/{first}", method="GET", authenticated=true)
public class OmniadminCommand implements ICommand, IAuthorized {

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
    	return XsfAuthUtil.isOmniAdmin(context);
    }
}
