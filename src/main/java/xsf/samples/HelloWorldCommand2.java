package xsf.samples;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.Validate;

import com.liferay.portal.model.User;
import com.xtivia.xsf.core.annotation.Route;
import com.xtivia.xsf.core.commands.CommandResult;
import com.xtivia.xsf.core.commands.ICommand;
import com.xtivia.xsf.core.commands.IContext;
import com.xtivia.xsf.liferay.ILiferayCommandKeys;

/**
 * class HelloWorldCommand2: Next revision of the HelloWorld controller.  This one adds info from the Liferay
 * context into the result and validates the input parameters.
 *
 * Per our configuration, any incoming GET request to http://localhost:8080/delegate/xsf/hello/world2/Bloggs/Joe
 * will be handled by this command object.
 */
@Route(uri="/hello/world2/{last}/{first}", method="GET",authenticated=false)
public class HelloWorldCommand2 implements ICommand {

	/**
	 * execute: Handles the processing of the request.
	 * @param context Context for the request.
	 * @return CommandResult The result of the command execution.
	 */
	@Override
	public CommandResult execute(IContext context) {

		// create a new map to hold our dynamic data
		Map<String,String> data = new HashMap<String,String>();
		
		// inputs from path paramters
		String firstName = context.find("first");
		Validate.notNull(firstName,"Required path param=firstName not found");

		String lastName = context.find("last");
		Validate.notNull(lastName,"Required path param=lastName not found");

		// set the parameter values back into the data object.
		data.put("first_name", firstName);
		data.put("last_name", lastName);
		
		// optional inputs from query string
		data.put("middle_name", "Not Available");
		String middleName = context.find("mname");
		if (middleName != null) {
			data.put("middle_name", middleName);
		}
		
		// if the user is currently logged into Liferay, inject the current user's
		// email address into the response data.
		User user = context.find(ILiferayCommandKeys.LIFERAY_USER);
		if (user != null) {
			data.put("user_email", user.getEmailAddress());
		} else {
			data.put("user_email", "Not authenticated");
		}

		// return the new command result with the data.
		return new CommandResult().setData(data);
	}
}
