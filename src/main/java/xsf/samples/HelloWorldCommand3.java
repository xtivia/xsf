package xsf.samples;

import java.util.Calendar;

import xsf.samples.model.SampleInput;
import xsf.samples.model.SampleOutput;

import com.xtivia.xsf.core.annotation.Route;
import com.xtivia.xsf.core.commands.CommandResult;
import com.xtivia.xsf.core.commands.ICommand;
import com.xtivia.xsf.core.commands.IContext;

/**
 * class HelloWorldCommand3: Next revision to the command, this one requires a specifically defined input object to
 * be provided for the command.  This command demonstrates the automatic JSON marshalling for input and output objects
 * used by XSF.  The marshalling is handled by the Jackson JSON mapper.
 *
 * Per our configuration, any incoming POST request to http://localhost:8080/delegate/xsf/hello/world3/12
 * will be handled by this command object.
 *
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

	/**
	 * execute: Handles the processing of the request.
	 * @param context Context for the request.
	 * @return CommandResult The result of the command execution.
	 */
	@Override
	public CommandResult execute(IContext context) {
		// define a new sample output object.
		SampleOutput output = new SampleOutput();
		
		// inputs from path paramters
		String id = context.find("id");
		
		// inputs from posted JSON (marshalled to Java object)
		SampleInput input = context.find("inputData");
		if (input == null) {
			return new CommandResult().setSucceeded(false).setMessage("No inputs were detected");
		}

		// set the values in the output object.
		output.setId(id);
		output.setCount(input.getInputNumber()+1);
		output.setText(input.getInputText().toUpperCase());
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(input.getInputDate());
		
		output.setDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK));
		output.setMonth(calendar.get(Calendar.MONTH));

		// return the command result with our new output object.
		return new CommandResult().setData(output);
	}
}
