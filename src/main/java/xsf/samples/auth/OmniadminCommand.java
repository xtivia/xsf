package xsf.samples.auth;

import java.util.HashMap;
import java.util.Map;

import com.xtivia.xsf.core.annotation.Route;
import com.xtivia.xsf.core.auth.IAuthorized;
import com.xtivia.xsf.core.commands.CommandResult;
import com.xtivia.xsf.core.commands.ICommand;
import com.xtivia.xsf.core.commands.IContext;

/**
 * class OmniadminCommand: A command that requires that a user is logged in and is also an administrator.
 *
 * Per our configuration, any incoming GET request to http://localhost:8080/delegate/xsf/needsomniadmin/echo/Bloggs/Joe
 * will be handled by this command object.
 */
@Route(uri="/needsomniadmin/echo/{last}/{first}", method="GET", authenticated=true)
public class OmniadminCommand implements ICommand, IAuthorized {

	/**
	 * execute: Handles the processing of the request.  The code here matches the HelloWorldCommand's execute() method,
	 * it doesn't do anything extra.
	 * @param context Context for the request.
	 * @return CommandResult The result of the command execution.
	 */
	@Override
    public CommandResult execute(IContext context) {

		// create a
        Map<String, String> data = new HashMap<String, String>();

        //inputs from path paramters
        String firstName = context.find("first");
        String lastName = context.find("last");
        data.put("first_name", firstName);
        data.put("last_name", lastName);
        data.put("command_name",getClass().getSimpleName());

        return new CommandResult().setData(data);
    }

	/**
	 * authorize: This is the method defined in the IAuthorized interface and allows the command a chance to determine if
	 * the user is authorized.
	 * @param context Context for the request.
	 * @return boolean <code>true</code> if the user is authorized, otherwise they are not.
	 */
    @Override
    public boolean authorize(IContext context) {

	    // hand off to a utility class for evaluation.
	    return XsfAuthUtil.isOmniAdmin(context);
    }
}
