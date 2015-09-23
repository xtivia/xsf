package xsf.samples;

import java.util.HashMap;
import java.util.Map;

import com.xtivia.xsf.core.annotation.Route;
import com.xtivia.xsf.core.commands.CommandResult;
import com.xtivia.xsf.core.commands.ICommand;
import com.xtivia.xsf.core.commands.IContext;

@Route(uri="/hello/world/{name}", method="GET", authenticated=false)
public class HelloWorldCommand implements ICommand {

	@Override
	public CommandResult execute(IContext context) {
		
		Map<String,Object> data = new HashMap<String,Object>();
		String name = ctx.find('name');
		data.put("name", name);
		data.put("execution_time", new java.util.Date());
		return new CommandResult().setSucceeded(true).setData(data).setMessage("");
	}
}
