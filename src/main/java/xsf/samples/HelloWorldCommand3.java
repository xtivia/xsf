package xsf.samples;

import java.util.Calendar;

import xsf.samples.model.SampleInput;
import xsf.samples.model.SampleOutput;

import com.xtivia.xsf.core.annotation.Route;
import com.xtivia.xsf.core.commands.CommandResult;
import com.xtivia.xsf.core.commands.ICommand;
import com.xtivia.xsf.core.commands.IContext;

/*
 * Sample URL for this command = http://localhost:8080/delegate/xsf/hello/world3/2742
 * Sample JSON input for this command below .... 
 *
{
  "inputText" : "foobar",
  "inputNumber" : 22,
  "inputDate" : "2015-01-06T20:23:38"
}
 */

@Route(uri="/hello/world3/{id}", method="POST", authenticated=false,
       inputKey="inputData", inputClass="xsf.samples.model.SampleInput")

public class HelloWorldCommand3 implements ICommand {

	@Override
	public CommandResult execute(IContext context) {
		
		SampleOutput output = new SampleOutput();
		
		//inputs from path paramters
		String id = context.find("id");
		
		//inputs from posted JSON (marshalled to Java object)
		SampleInput input = context.find("inputData");
		if (input == null) {
			return new CommandResult().setSucceeded(false).setMessage("No inputs were detected");
		}
		
		output.setId(id);
		output.setCount(input.getInputNumber()+1);
		output.setText(input.getInputText().toUpperCase());
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(input.getInputDate());
		
		output.setDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK));
		output.setMonth(calendar.get(Calendar.MONTH));
		
		return new CommandResult().setSucceeded(true).setData(output).setMessage("");
	}
}
