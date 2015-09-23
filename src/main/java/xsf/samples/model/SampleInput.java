package xsf.samples.model;

import java.util.Date;

/**
 * class SampleInput: Simple POJO to contain the values marshalled from the incoming JSON request for the HelloWorldCommand3 class.
 *
 * No javadoc inside the pojo, suffice it to say that it will match the incoming JSON:
 *
 {
 "inputText" : "foobar",
 "inputNumber" : 22,
 "inputDate" : "2015-01-06T20:23:38"
 }
 */
public class SampleInput {
	
	private String inputText;
	private int    inputNumber;
	private Date   inputDate;
	
	public String getInputText() {
		return inputText;
	}
	
	public void setInputText(String inputText) {
		this.inputText = inputText;
	}
	
	public int getInputNumber() {
		return inputNumber;
	}
	
	public void setInputNumber(int inputNumber) {
		this.inputNumber = inputNumber;
	}
	
	public Date getInputDate() {
		return inputDate;
	}
	
	public void setInputDate(Date inputDate) {
		this.inputDate = inputDate;
	}

}
