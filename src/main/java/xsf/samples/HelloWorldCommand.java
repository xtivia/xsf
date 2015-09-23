package xsf.samples;

import java.util.HashMap;
import java.util.Map;

import com.xtivia.xsf.core.annotation.Route;
import com.xtivia.xsf.core.commands.CommandResult;
import com.xtivia.xsf.core.commands.ICommand;
import com.xtivia.xsf.core.commands.IContext;

@Route(uri="/hello/world/{last}/{first}", method="GET", authenticated=false)
public class HelloWorldCommand implements ICommand {

	@Override
	public CommandResult execute(IContext context) {
		
		Map<String,Object> data = new HashMap<String,Object>();
		
		//inputs from path paramters
		String firstName = context.find("first");
		String lastName = context.find("last");
		data.put("first_name", firstName);
		data.put("last_name", lastName);
		data.put("execution_time", new java.util.Date());
		
		return new CommandResult().setSucceeded(true).setData(data).setMessage("");
	}
}
