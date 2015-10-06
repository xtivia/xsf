package xsf.samples.people;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;

import com.xtivia.xsf.core.annotation.Route;
import com.xtivia.xsf.core.commands.CommandResult;
import com.xtivia.xsf.core.commands.ICommand;
import com.xtivia.xsf.core.commands.IContext;

/*
  Demonstrates how to create multiple REST endpoints in a single Java source file
  via the use of public static inner classes. Provides a sample in-memory CRUD
  operations for a list of people.
  
  Everything needed to implement the entire set of CRUD actions is included in this
  single source file including the value object used for marshalling data to/from the
  client, the endpoint classes, as well as utility functions used by the classes.
  
  Note that these are fully independent classes (unlike non-static inner classes). 
 */

public class PeopleService {
	
	private static final String PEOPLE_URI = "/people";
	
	public static class Person {
		
		public long   id = -1;
		public String lastName;
		public String firstName;
		public String location;
		
		public Person() {}
		
		public Person(long id, String first, String last, String location) {
			this.id = id;
			this.firstName = first;
			this.lastName = last;
			this.location = location;
		}	
	}
	
	// return all people
	@Route(uri=PEOPLE_URI, method="GET", authenticated=false)
	public static class GetPeople implements ICommand {
		@Override
		public CommandResult execute(IContext context) {
			return new CommandResult().setSucceeded(true)
					                  .setData(__people)
					                  .setMessage("");
			
		}
	}

	// return a single person based on supplied ID
	@Route(uri=PEOPLE_URI+"/{id}", method="GET", authenticated=false)	
	public static class GetPerson implements ICommand {
		@Override
		public CommandResult execute(IContext context) {
			CommandResult cr = new CommandResult().setSucceeded(false).setMessage("General error");
			try {
			  Person person = findById(getInputId(context));
			  if (person != null) {
				  cr.setSucceeded(true).setData(person).setMessage("");
			  } else {
				  cr.setMessage("Requested person not found");
			  }
			} catch (Exception e) {
				cr.setMessage(e.getMessage());
			}
			return cr;
		}
	}
		
    // add a new person
	@Route(uri=PEOPLE_URI, method="POST", authenticated=false,inputClass="xsf.samples.people.PeopleService$Person",inputKey="person")	
	public static class AddPerson implements ICommand {
		@Override
		public CommandResult execute(IContext context) {	
			CommandResult cr = new CommandResult().setSucceeded(false).setMessage("General error");
			try {
			  Person newPerson = getInputPerson(context);
			  validatePerson(newPerson);
			  // generate a trivial ID based on epoch time
			  newPerson.id = new java.util.Date().getTime();
			  __people.add(newPerson);
			  cr.setSucceeded(true).setData(newPerson).setMessage("");

			} catch (Exception e) {
				cr.setMessage(e.getMessage());
			}
			return cr;
		}
	}
	
	// update a single person based on supplied ID
	@Route(uri=PEOPLE_URI+"/{id}", method="PUT", authenticated=false,inputClass="xsf.samples.people.PeopleService$Person",inputKey="person")	
	public static class UpdatePerson implements ICommand {
		@Override
		public CommandResult execute(IContext context) {
			CommandResult cr = new CommandResult().setSucceeded(false).setMessage("General error");
			try {
			  Person oldPerson = findById(getInputId(context));
			  if (oldPerson != null) {
				  Person newPerson = getInputPerson(context);
				  validatePerson(newPerson);
				  oldPerson.firstName = newPerson.firstName;
				  oldPerson.lastName = newPerson.lastName;
				  oldPerson.location = newPerson.location;
				  cr.setSucceeded(true).setMessage("");
			  } else {
				  cr.setMessage("Requested to update non-existing person");
			  }
			} catch (Exception e) {
				cr.setMessage(e.getMessage());
			}
			return cr;
		}
	}
	
	// delete a single person based on supplied ID
	@Route(uri=PEOPLE_URI+"/{id}", method="DELETE", authenticated=false)	
	public static class DeletePerson implements ICommand {
		@Override
		public CommandResult execute(IContext context) {	
			CommandResult cr = new CommandResult().setSucceeded(false).setMessage("General error");
			try {
			  long id = getInputId(context);
			  int ndx = 0;
			  for (Person person : __people) {
				  if (person.id == id) {
					  __people.remove(ndx);
					  cr.setSucceeded(true).setMessage("");
				  } else {
					  ndx++;
				  }
			  }
			  cr.setMessage("Requested to add person with duplicate ID");
			} catch (Exception e) {
				cr.setMessage(e.getMessage());
			}
			return cr;
		}
	}
	
	// 
	// Utility functions used by all endpoints
	//
	
	private static long getInputId(IContext context) throws Exception {
		  String idstr = context.find("id");
		  Validate.notNull(idstr,"ID is null on get request");
		  return new Long(idstr);
	}
	
	private static Person getInputPerson(IContext context) throws Exception {
		Person person = context.find("person");
		Validate.notNull(person,"Required person object not found in input");
		return person;
	}
	
	private static Person findById(long id) {
		for (Person person : __people) {
			if (person.id == id) {
				return person;
			}
		}
		return null;
	}
	
	private static void validatePerson(Person person) throws Exception {
		Validate.notNull(person.firstName,"First name is required.");
		Validate.notNull(person.lastName,"Last name is required.");
		Validate.notNull(person.location,"Location is required.");
	}

	// some sample data for our demo
    private static List<Person> __people = new ArrayList<Person> ();
    static {
    	__people.add(new Person(1, "Daffy","Duck","Missouri"));
       	__people.add(new Person(2, "Minnie","Mouse","Ohio"));
       	__people.add(new Person(3, "Elmer","Fudd","Texas"));
       	__people.add(new Person(4, "Foghorn","Leghorn","South Carolina"));
       	__people.add(new Person(5, "Mother","Goose","New York"));
       	__people.add(new Person(6, "Bugs","Bunny","Colorado"));
    }
}
