package ${pkg};

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;

import com.xtivia.xsf.core.annotation.Route;
import com.xtivia.xsf.core.commands.CommandResult;
import com.xtivia.xsf.core.commands.ICommand;
import com.xtivia.xsf.core.commands.IContext;

public class ${name}Service {
	
	private static final String ${name_upper}_URI = "/${name_lower}";
	
	public static class ${name} {
		
		 public long id = -1;
		<% fields.each { %> public String $it;
		<% } %>
	}
	
	// return all ${name_lower}s
	@Route(uri=${name_upper}_URI, method="GET", authenticated=false)
	public static class Get${name}s implements ICommand {
		@Override
		public CommandResult execute(IContext context) {
			return new CommandResult().setSucceeded(true)
					                  .setData(__data)
					                  .setMessage("");
			
		}
	}

	// return a single ${name_lower} based on supplied ID
	@Route(uri=${name_upper}_URI+"/{id}", method="GET", authenticated=false)	
	public static class Get${name} implements ICommand {
		@Override
		public CommandResult execute(IContext context) {
			CommandResult cr = new CommandResult().setSucceeded(false).setMessage("General error");
			try {
			  ${name} ${name_lower} = findById(getInputId(context));
			  if (${name_lower} != null) {
				  cr.setSucceeded(true).setData(${name_lower}).setMessage("");
			  } else {
				  cr.setMessage("Requested ${name_lower} not found");
			  }
			} catch (Exception e) {
				cr.setMessage(e.getMessage());
			}
			return cr;
		}
	}
		
    // add a new ${name_lower}
	@Route(uri=${name_upper}_URI, method="POST", authenticated=false,inputClass="${pkg}.${name}Service\$${name}",inputKey="${name_lower}")	
	public static class Add${name} implements ICommand {
		@Override
		public CommandResult execute(IContext context) {	
			CommandResult cr = new CommandResult().setSucceeded(false).setMessage("General error");
			try {
			  ${name} new${name} = getInput${name}(context);
			  validate${name}(new${name});
			  // generate a trivial ID based on epoch time
			  new${name}.id = new java.util.Date().getTime();
			  __data.add(new${name});
			  cr.setSucceeded(true).setData(new${name}).setMessage("");

			} catch (Exception e) {
				cr.setMessage(e.getMessage());
			}
			return cr;
		}
	}
	
	// update a single ${name_lower} based on supplied ID
	@Route(uri=${name_upper}_URI+"/{id}", method="PUT", authenticated=false,inputClass="${pkg}.${name}Service\$${name}",inputKey="${name_lower}")	
	public static class Update${name} implements ICommand {
		@Override
		public CommandResult execute(IContext context) {
			CommandResult cr = new CommandResult().setSucceeded(false).setMessage("General error");
			try {
			  ${name} old${name} = findById(getInputId(context));
			  if (old${name} != null) {
				  
				  ${name} new${name} = getInput${name}(context);
				  
				  validate${name}(new${name});
				  <% fields.each { %> 
       	          old${name}.$it=new${name}.$it;
		          <% } %>
				  cr.setSucceeded(true).setMessage("");
			  } else {
				  cr.setMessage("Request to update non-existing ${name_lower}");
			  }
			} catch (Exception e) {
				cr.setMessage(e.getMessage());
			}
			return cr;
		}
	}
	
	// delete a single ${name_lower} based on supplied ID
	@Route(uri=${name_upper}_URI+"/{id}", method="DELETE", authenticated=false)	
	public static class Delete${name} implements ICommand {
		@Override
		public CommandResult execute(IContext context) {	
			CommandResult cr = new CommandResult().setSucceeded(false).setMessage("General error");
			try {
			  long id = getInputId(context);
			  int ndx = 0;
			  for (${name} ${name_lower} : __data) {
				  if (${name_lower}.id == id) {
					  __data.remove(ndx);
					  cr.setSucceeded(true).setMessage("");
				  } else {
					  ndx++;
				  }
			  }
			  cr.setMessage("Requested to add ${name_lower} with duplicate ID");
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
	
	private static ${name} getInput${name}(IContext context) throws Exception {
		${name} ${name_lower} = context.find("${name_lower}");
		Validate.notNull(${name_lower},"Required ${name_lower} object not found in input");
		return ${name_lower};
	}
	
	private static ${name} findById(long id) {
	    for (${name} ${name_lower} : __data) {
		  if (${name_lower}.id == id) {
			return ${name_lower};
		  }
		}
		return null;
	}
	
	private static void validate${name}(${name} ${name_lower}) throws Exception {
       	<% fields.each { %> 
       	  Validate.notNull(${name_lower}.$it,"${it} cannot be null");
		<% } %>
	}

    private static List<${name}> __data = new ArrayList<${name}> ();
 
}
