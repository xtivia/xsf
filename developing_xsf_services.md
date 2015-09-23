###Hello World in XSF

We will review developing some simple "Hello World" style REST services using XSF. The examples we will discuss are provided as samples in the XSF distribution (located in the *com.xtivia.xsf.samples* package). For our first service we will build a simple echo-style service that accepts path parameters from URI segments and returns a simple object to 'echo' those inputs.

Once XSF has been installed in your development environment (see above) developing a new REST service is as simple as creating a Java class, implementing a single method within that class, and then adding an annotation to the class to configure the routing information for the service. XSF is built using the Spring framework so each command should also be created as a named Spring bean, which we accomplish via the use of the @Component annotation.

XSF uses the well-known "command" design pattern. Each endpoint/service should be implemented in a class that implements the *ICommand* interface, an interface that contains a single method named *execute()*. 

The *execute*() method accepts a single "context" parameter that provides access to inputs needed by the command (path parameters, session parameters, query parameters, etc.) and must return an instance of a *CommandResult* object. We will have more to say on the context object later in this article, but for 
now think of it as a "door" to the execution environment of the command; at runtime this environment is provided by XSF itself, but it can easily be populated by unit tests to fully exercise the functions and error-handing of the command itself.

A *CommandResult* object contains three fields:

-   A *succeeded* (boolean) field indicating whether or not the execution
    of the command succeeded

-   A *message* (String) field that can be used to supply more information
    when the command fails

-   A *data* (Object) field that contains the returned Java object (payload).     XSF will marshal the entire *CommandResult* into JSON, including the contents of the *data* field. XSF uses the Jackson library for JSON marshalling;     the recommended approach is to use simple value objects for marshalling information to/from the service.

So with the information above as a backdrop we can now begin the construction of our echo service. Listing 1 below provides the implementation of this service, which is provided in the XSF distribution in *com.xtivia.xsf.samples.HelloWorldCommand*. This service is invoked with a URL that looks something like 
*http://localhost:8080/delegate/xsf/hello/world/Bloggs/Joe*, where the last two path segments represent a last name and first name respectively.

```java
    package com.xtivia.xsf.samples;
    [imports omitted for brevity]

    @Component("helloCommand")
    @Route(uri="/hello/world/{last}/{first}", method="GET")

    public class HelloWorldCommand implements ICommand {

      @Override
      public CommandResult execute(IContext context) {
        Map<String,String> data = new HashMap<String,String>();

        //inputs from path parameters
        String firstName = context.find("first");
        String lastName = context.find("last");
        data.put("first_name", firstName);
        data.put("last_name", lastName);
        return new CommandResult().setSucceeded(true).setData(data).setMessage("");
      }
    }
```
**Listing 1, HelloWorldCommand.java**

Let's first examine the @Route annotation at the top of the class. You will see that the *uri* value in this annotation describes the 'route' that is used to invoke this command. It does not include the host information, nor does it include the */delegate/xsf* portion of the full URI. For now consider those portions of the overall URI as fixed; we will provide more information about the */delegate/xsf* portion of the URI in a subsequent article.

Also note that the route definition indicates that the last two segments of the URI are the path parameters, namely *last* and *first*. XSF will route any inbound request that matches this URI to this command, and further, XSF will parse the URI for these parameters and make them available to the 
command's *execute()* method via the supplied context parameter.

We can see our command/service accessing these parameters in the first two statements of the *execute()* method. Invocations are made to the *find()* method of the supplied context; *find()* is a typesafe generic method that will return a value of matching type, or null if a value cannot be found 
(or is not null but does not match the requested type). You can think of the context as a specialized implementation of the *Map* interface that proxies information from the request, session, parameters and overall Liferay execution environment.

Finally our command needs to construct an object to return as output from the service. In this case we will use a simple *HashMap* object; in subsequent examples we will demonstrate how to return custom Java objects from our services. In all cases our commands only need to set the desired return object into the *data* field of a *CommandResult* object and XSF will handle the marshalling to JSON!

We simply insert the values we originally received as inputs (with error handling intentionally not present for brevity) into our output object, set this object into the *data* field of the *CommandResult* object and then set the *succeeded* flag to true before exiting the method. And we're done!

Now if we invoke our service using something like *curl* or a REST testing tool (or even the browser in this case since this service is invoked via GET) with the URL *http://localhost:8080/delegate/xsf/hello/world/Bloggs/Joe* we will receive the JSON shown in Listing 2 as our output.

```json
    {
          "succeeded":true,
          "data" : {"first_name":"Joe", "last_name":"Bloggs"},
          "message": ""
    }
```
**Listing 2, JSON returned from invocation of HelloWorldCommand**

**Hello World, Part 2**

A slightly more enhanced version of our service is provided in *com.xtivia.xsf.samples.HelloWorldCommand2* shown in Listing 3 below (and included in the XSF distribution). This command example is very similar 
to our first one both in terms of route definition and implementation, but demonstrates a couple of additional features of XSF in code we add near the end of its *execute()* method.

Code has been added to demonstrate retrieving query parameters from the request; note that the technique is the same as before in terms of interrogating the supplied context for the desired parameter. So our code does not need to worry with boilerplate logic to retrieve path parameters vs. request query parameters; instead this is all handled by XSF and made available to services/commands via the supplied context. This also has the additional benefit of making commands easy to test; the unit tests can easily mock values for testing and place them into an input context – the context object supports all of the 'write' methods for a Map as well.

In our case the code tests for the presence of a query parameter named *mname* and if it found echoes the value back in the return object. If the query parameter was not supplied on the request a default value is returned instead.

```java
    @Component("helloCommand2")
    @Route(uri="/hello/world2/{last}/{first}", method="GET")

    public class HelloWorldCommand2 implements ICommand {
        @Override
        public CommandResult execute(IContext context) {
            Map<String,String> data = new HashMap<String,String>();

            //inputs from path parameters
            String firstName = context.find("first");
            Validate.notNull(firstName,"Required path param=firstName not found");
            String lastName = context.find("last");
            Validate.notNull(lastName,"Required path param=lastName not found");
            data.put("first_name", firstName);
            data.put("last_name", lastName);
            
            // optional input from query string
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
```
**Listing 3, HelloWorldCommand2.java**

The final portion of our *execute()* method provides an early taste of integration with Liferay elements in XSF-based services. In this case the service leverages the fact that XSF will determine if the current user is 
logged into Liferay or not and if so will place a copy of the Liferay *User* object that represents the logged-in user into the context for subsequent access by services/commands. We will talk more about integration with the Liferay API in subsequent articles.

Listing 4 below provides two examples of URLs to invoke our service and the JSON that results from these respective invocations.

```
    URL: http://localhost:8080/delegate/xsf/hello/world2/Bloggs/Joe?mname=Lee" (not logged in)

    JSON returned:
        {
          "succeeded":true,
          "data": {"first_name":"Joe",
                             "middle_name":"Lee",
                             "last_name":"Bloggs",
                             "user_email":"Not authenticated"
                            },
          "message":""
        }


    URL: http://localhost:8080/delegate/xsf/hello/world2/Bloggs/Joe (after log in)

    JSON returned:
        {
          "succeeded":true,
          "data":{
                             "first_name":"Joe",
                             "middle_name":"NMN",
                             "last_name":"Bloggs",
                             "user_email":"xsf@xtivia.com"
                           },
          "message":""
        }
```
**Listing 4, URLs and resulting JSON for HelloWorldCommand2**

**Hello World, Part 3**

Until now our examples have been based on simple GET requests where all inputs are supplied in the URI, and where the returned object is a standard Java Map class. In our final example we will demonstrate a POST based service and will use custom application objects to define the input as well as the 
output for the service. Our third example is provided in *com.xtivia.xsf.samples.HelloWorldCommand3,* shown below as Listing 5 and included in the XSF distribution. Note that in the interest of space we have
not included the source listings for the *SampleInput* and *SampleOutput* classes but these are also available in the XSF distribution.

```java
    package com.xtivia.xsf.samples;
    [imports omitted for brevity]

    @Component("helloCommand3")
    @Route(uri="/hello/world3/{id}", method="POST",
           inputKey="inputData", inputClass="com.xtivia.xsf.samples.model.SampleInput")
           
    public class HelloWorldCommand3 implements ICommand {

        @Override
        public CommandResult execute(IContext context) {

            SampleOutput output = new SampleOutput();

            //inputs from path parameters
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
```
**Listing 5, HelloWorldCommand3.java**

Note that in this example we add two additional attributes related to the marshalling of an inbound object in the @Route annotation, *inputKey* and *inputClass*. The former is used to instruct XSF what key to use when storing the inbound object in the context, and the latter defines what Java class should be used to marshal the inbound JSON string. XSF currently uses the Jackson library for all of its JSON marshalling (XSF in fact supports a pluggable architecture for marshalling; out-of-the-box it supports JSON but 
provides an extension mechanism for a implementing custom marshaller if, for example, you wanted to use XML instead of JSON.)

The Jackson library is quite flexible in terms of its marshalling support as we have seen in the previous examples where we returned a Map object and it was converted to JSON with no additional effort on our part. Embedded child objects and embedded collections are supported quite nicely by Jackson, however, we would recommend as a best practice that you attempt to use the "value object" pattern for objects used to marshal JSON to/from the client.

In our sample service/command we obtain the input object from the client, do some minor manipulations on fields from that object, and then set the modified values back into an object to be returned to the client. Note than in the case of the returned object we do not need to specify any additional metadata about the returned class. All reasonably straightforward value objects can be marshalled into JSON by interrogation of the class definition for the returned object.

Listing 6 below provides an example of a URL invocation that might be used to trigger the execution of this service, as well as a sample JSON input and the corresponding JSON output:

```
    URL: http://localhost:8080/delegate/xsf/hello/world3/2742

    Input JSON:
        {
          "inputText" : "foobar",
          "inputNumber" : 22,
          "inputDate" : "2015-01-06T20:23:38"
    }

    Output JSON:
    {
          "succeeded":true,
          "data": {"id":"2742",
                             "text":"FOOBAR",
                             "month":0,
                             "dayOfWeek":3,
                             "count":23},
          "message":""
    }
```
**Listing 6, URL and input/output JSON for HelloWorldCommand2**

