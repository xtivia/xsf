package com.xtivia.xsf.samples;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.springframework.stereotype.Component;

import com.liferay.portal.model.User;
import com.xtivia.xsf.core.annotation.Route;
import com.xtivia.xsf.core.commands.CommandResult;
import com.xtivia.xsf.core.commands.ICommand;
import com.xtivia.xsf.core.commands.ICommandKeys;
import com.xtivia.xsf.core.commands.IContext;

@Component("helloCommand2")
@Route(uri="/hello/world2/{last}/{first}", method="GET")
public class HelloWorldCommand2 implements ICommand {

	@Override
	public CommandResult execute(IContext context) {
		
		Map<String,String> data = new HashMap<String,String>();
		
		//inputs from path paramters
		String firstName = context.find("first");
		Validate.notNull(firstName,"Required path param=firstName not found");
		String lastName = context.find("last");
		Validate.notNull(lastName,"Required path param=lastName not found");

		data.put("first_name", firstName);
		data.put("last_name", lastName);
		
		//inputs from query string
		data.put("middle_name", "NMN");
		String middleName = context.find("mname");
		if (middleName != null) {
			data.put("middle_name", middleName);
		}
		
		//input based on logged-in Liferay user
		User user = context.find(ICommandKeys.LIFERAY_USER);
		data.put("user_email", "Not authenticated");
		if (user != null) {
			data.put("user_email", user.getEmailAddress());
		}
		
		return new CommandResult().setSucceeded(true).setData(data).setMessage("");
	}
}
