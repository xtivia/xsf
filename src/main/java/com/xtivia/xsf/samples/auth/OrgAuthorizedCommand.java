package com.xtivia.xsf.samples.auth;

import com.xtivia.xsf.core.annotation.Route;
import com.xtivia.xsf.core.commands.CommandResult;
import com.xtivia.xsf.core.commands.ICommand;
import com.xtivia.xsf.core.commands.IContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.xtivia.xsf.core.auth.IAuthorizer;
import com.xtivia.xsf.core.auth.IAuthorized;

/*
 This command demonstrates providing a list of custom
 authenticators to the XSF framework. See the OrgAuthenticator
 class for the implementation of the actual authenticator.
 */
@Component
@Route(uri="/needsorgrole/echo/{last}/{first}", method="GET")
public class OrgAuthorizedCommand implements ICommand, IAuthorized {

    @Override
    public List<IAuthorizer> getAuthorizers() {
        List<IAuthorizer> authenticators = new ArrayList<IAuthorizer>();
        authenticators.add(new  OrgAuthorizer());
        return authenticators;
    }

    @Override
    public CommandResult execute(IContext context) {
        Map<String, String> data = new HashMap<String, String>();

        //inputs from path paramters
        String firstName = context.find("first");
        String lastName = context.find("last");
        data.put("first_name", firstName);
        data.put("last_name", lastName);
        data.put("command_name",this.getClass().getSimpleName());

        return new CommandResult().setSucceeded(true).setData(data).setMessage("");
    }
}
