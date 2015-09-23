package xsf.samples.people;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;

import com.xtivia.xsf.core.annotation.Route;
import com.xtivia.xsf.core.commands.CommandResult;
import com.xtivia.xsf.core.commands.ICommand;
import com.xtivia.xsf.core.commands.IContext;

/**
 * class PeopleService: Demonstrates how to create multiple REST endpoints in a single Java source file
 via the use of public static inner classes. Provides a sample in-memory CRUD
 operations for a list of people.

 Everything needed to implement the entire set of CRUD actions is included in this
 single source file including the value object used for marshalling data to/from the
 client, the endpoint classes, as well as utility functions used by the classes.

 Note that these are fully independent classes (unlike non-static inner classes).
 */
public class PeopleService {
	/**
	 * PEOPLE_URI: The default partial path that we'll be handling requests on.
	 */
	private static final String PEOPLE_URI = "/people";

	/**
	 * class Person: This is the POJO that will be used as input and output for
	 * the crud operations.  This will be auto-marshalled to/from JSON using Jackson.
	 */
	public static class Person {
		
		public long   id = -1;
		public String lastName;
		public String firstName;
		public String location;

		/**
		 * Person: Default constructor.
		 */
		public Person() {}

		/**
		 * Person: Constructor with field args.
		 * @param id
		 * @param first
		 * @param last
		 * @param location
		 */
		public Person(long id, String first, String last, String location) {
			this.id = id;
			this.firstName = first;
			this.lastName = last;
			this.location = location;
		}	
	}

	/**
	 * class GetPeople: Returns all people.
	 *
	 * Per our configuration, any incoming GET request to http://localhost:8080/delegate/xsf/people
	 * will be handled by this command object.
	 */
	@Route(uri=PEOPLE_URI, method="GET", authenticated=false)
	public static class GetPeople implements ICommand {
		/**
		 * execute: Handles the command execution.
		 * @param context
		 * @return CommandResult The result of the command.
		 */
		@Override
		public CommandResult execute(IContext context) {
			// return the current list of people.
			// NOTE: This is where you would execute a query against your data store to retrieve all records.
			return new CommandResult().setData(__people);
		}
	}

	/**
	 * class GetPerson: Returns the person with the given id.
	 *
	 * Per our configuration, any incoming GET request to http://localhost:8080/delegate/xsf/people/228
	 * will be handled by this command object.
	 */
	@Route(uri=PEOPLE_URI+"/{id}", method="GET", authenticated=false)	
	public static class GetPerson implements ICommand {
		/**
		 * execute: Handles the command execution.
		 * @param context
		 * @return CommandResult The result of the command.
		 */
		@Override
		public CommandResult execute(IContext context) {
			// initialize a command result that represents a failure.
			CommandResult cr = new CommandResult().setSucceeded(false).setMessage("General error");

			try {
				// find the person using the given id.
			  Person person = findById(getInputId(context));

				// if the person is found
			  if (person != null) {
				  // clear the result error indicator and set the person
				  cr.setSucceeded(true).setData(person).setMessage("");
			  } else {
				  // update the message to indicate the person is not found.
				  cr.setMessage("Requested person not found");
			  }
			} catch (Exception e) {
				// set the message to be the exception message.
				cr.setMessage(e.getMessage());
			}

			// return the result.
			return cr;
		}
	}

	/**
	 * class AddPerson: Adds a new person.
	 *
	 * Per our configuration, any incoming POST request to http://localhost:8080/delegate/xsf/people
	 * will be handled by this command object.
	 */
	@Route(uri=PEOPLE_URI, method="POST", authenticated=false,inputClass="xsf.samples.people.PeopleService$Person",inputKey="person")	
	public static class AddPerson implements ICommand {
		/**
		 * execute: Handles the command execution.
		 * @param context
		 * @return CommandResult The result of the command.
		 */
		@Override
		public CommandResult execute(IContext context) {
			// create a new command result indicating general failure
			CommandResult cr = new CommandResult().setSucceeded(false).setMessage("General error");

			try {
				// extract the person from the context
			  Person newPerson = getInputPerson(context);

				// validate the person record
			  validatePerson(newPerson);

			  // generate a trivial ID based on epoch time
			  newPerson.id = System.currentTimeMillis();

				// add the person to the list
				// NOTE: This is where you'd actually be persisting the person into your data store.
			  __people.add(newPerson);

				// mark the result as successful and return the new person object.
			  cr.setSucceeded(true).setData(newPerson).setMessage("");

			} catch (Exception e) {
				// use the exception message as the failure message.
				cr.setMessage(e.getMessage());
			}

			// return the result
			return cr;
		}
	}

	/**
	 * class UpdatePerson: Updates the person using the given id.
	 *
	 * Per our configuration, any incoming PUT request to http://localhost:8080/delegate/xsf/people/228
	 * will be handled by this command object.
	 */
	@Route(uri=PEOPLE_URI+"/{id}", method="PUT", authenticated=false,inputClass="xsf.samples.people.PeopleService$Person",inputKey="person")	
	public static class UpdatePerson implements ICommand {
		/**
		 * execute: Handles the command execution.
		 * @param context
		 * @return CommandResult The result of the command.
		 */
		@Override
		public CommandResult execute(IContext context) {
			// create a new command result initialized to error status.
			CommandResult cr = new CommandResult().setSucceeded(false).setMessage("General error");

			try {
				// try to get the existing person object
			  Person oldPerson = findById(getInputId(context));

				// we can only update a found person.
			  if (oldPerson != null) {
				  // extract the person from the input
				  Person newPerson = getInputPerson(context);

				  // validate the person object
				  validatePerson(newPerson);

				  // update the old person with the values from the new person
				  oldPerson.firstName = newPerson.firstName;
				  oldPerson.lastName = newPerson.lastName;
				  oldPerson.location = newPerson.location;

				  // NOTE: This is where you'd do some sort of DB update if you're using
				  // some sort of persistence mechanism.

				  // clear the error message, no data to return.
				  cr.setSucceeded(true).setMessage("");
			  } else {
				  // update the message to indicate the person was not found.
				  cr.setMessage("Requested to update non-existing person");
			  }
			} catch (Exception e) {
				// set the failure message to the exception message.
				cr.setMessage(e.getMessage());
			}

			// return the result object
			return cr;
		}
	}

	/**
	 * class DeletePerson: Deletes the person using the given id.
	 *
	 * Per our configuration, any incoming DELETE request to http://localhost:8080/delegate/xsf/people/228
	 * will be handled by this command object.
	 */
	@Route(uri=PEOPLE_URI+"/{id}", method="DELETE", authenticated=false)	
	public static class DeletePerson implements ICommand {
		/**
		 * execute: Handles the command execution.
		 * @param context
		 * @return CommandResult The result of the command.
		 */
		@Override
		public CommandResult execute(IContext context) {
			// initialize the command result to a general failure
			CommandResult cr = new CommandResult().setSucceeded(false).setMessage("General error");

			try {
				// extract the id of the person to delete from the context
			  long id = getInputId(context);

				// iterate over each record in the list
				// NOTE: For a DB crud implementation you'd be doing some sort of delete against
				// your database.
			  int ndx = 0;
				boolean removed = false;

			  for (Person person : __people) {
				  if (person.id == id) {
					  // remove the found user from the list and return the success result.
					  __people.remove(ndx);
					  cr.setSucceeded(true).setMessage("");
					  removed = true;
					  break;
				  } else {
					  ndx++;
				  }
			  }

				if (! removed) {
					// did not find the specified person in the list
					cr.setMessage("Requested to delete, person not found.");
				}
			} catch (Exception e) {
				// set the message to the exception message.
				cr.setMessage(e.getMessage());
			}

			// return the result.
			return cr;
		}
	}

	/**
	 * getInputId: Returns the person id provided in the context.
	 * @param context
	 * @return long The found person id.
	 * @throws Exception if the param is not found or not a long.
	 */
	private static long getInputId(IContext context) throws Exception {
		// find the id in the context
		  String idstr = context.find("id");
		// validate it
		  Validate.notNull(idstr,"ID is null on get request");
		// and return it
		  return new Long(idstr);
	}

	/**
	 * getInputPerson: Returns the person object provided as input in the context
	 * @param context
	 * @return Person The found person object.
	 * @throws Exception if no person found in the context.
	 */
	private static Person getInputPerson(IContext context) throws Exception {
		// find the person in the context
		Person person = context.find("person");
		// validate one was found
		Validate.notNull(person,"Required person object not found in input");
		// return them
		return person;
	}

	/**
	 * findById: Finds a person in the list by their id.
	 * @param id
	 * @return Person The found person or <code>null</code> if not found.
	 */
	private static Person findById(long id) {
		// for each person in the list
		for (Person person : __people) {
			// if the ids match
			if (person.id == id) {
				// return the person
				return person;
			}
		}

		// if we get here the person is not in the list.
		return null;
	}

	/**
	 * validatePerson: Simple validator to ensure the person object is valid.
	 * @param person
	 * @throws Exception if the person is not valid.
	 */
	private static void validatePerson(Person person) throws Exception {
		Validate.notNull(person.firstName,"First name is required.");
		Validate.notNull(person.lastName,"Last name is required.");
		Validate.notNull(person.location,"Location is required.");
	}

	/**
	 * __people: Internal list of people.
	 */
    private static List<Person> __people = new ArrayList<Person> ();

	/**
	 * Static initializer to pre-populate our list of people
	 */
    static {
    	__people.add(new Person(1, "Bugs","Bunny","Missouri"));
       	__people.add(new Person(2, "Daffy","Duck","Ohio"));
       	__people.add(new Person(3, "Wiley","Coyote","Texas"));
       	__people.add(new Person(4, "Foghorn","Leghorn","South Carolina"));
       	__people.add(new Person(5, "Tweety","Bird","New York"));
       	__people.add(new Person(6, "Porky","Pig","Colorado"));
    }
}
