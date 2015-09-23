package xsf.samples;

import java.util.HashMap;
import java.util.Map;

import com.xtivia.xsf.core.annotation.Route;
import com.xtivia.xsf.core.commands.CommandResult;
import com.xtivia.xsf.core.commands.ICommand;
import com.xtivia.xsf.core.commands.IContext;

/**
 * class HelloWorldCommand: Basic implementation of a public command.
 *
 * Per our configuration, any incoming GET request to http://localhost:8080/delegate/xsf/hello/world/Bloggs/Joe
 * will be handled by this command object.
 */
@Route(uri="/hello/world/{last}/{first}", method="GET", authenticated=false)
public class HelloWorldCommand implements ICommand {

	/**
	 * execute: Handles the processing of the request.
	 * @param context Context for the request.
	 * @return CommandResult The result of the command execution.
	 */
	@Override
	public CommandResult execute(IContext context) {

		// define a map to hold the result data from the command
		Map<String,Object> data = new HashMap<String,Object>();
		
		// inputs from path paramters
		String firstName = context.find("first");
		String lastName = context.find("last");

		// populate values in the response data
		data.put("first_name", firstName);
		data.put("last_name", lastName);
		data.put("execution_time", new java.util.Date());

		// return a new command result object with our return data.
		return new CommandResult().setData(data);
	}
}
