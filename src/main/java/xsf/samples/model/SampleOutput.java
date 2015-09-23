package xsf.samples.model;

public class SampleOutput {
	
	String id;
	String text;
	int    month;
	int    dayOfWeek;
    int    count;
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
    public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public int getMonth() {
		return month;
	}
	
	public void setMonth(int month) {
		this.month = month;
	}
	
	public int getDayOfWeek() {
		return dayOfWeek;
	}
	
	public void setDayOfWeek(int dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}
	
	public int getCount() {
		return count;
	}
	
	public void setCount(int count) {
		this.count = count;
	}
    
}
