##Using the Xtivia Services Framework (XSF) to Create REST Services in Liferay

**NOTE: This 1.0.0-SNAPSHOT version of XSF was updated on 09-21-15 to address some simplifications in the POM
and in the authorizaton model. If needed, the prior version is available here under *Releases* as *July 2015 Early Release
**

XSF is a framework that Xtivia has created (and used for multiple client engagements) that enables the rapid development of custom REST services in Liferay.

As we know the current trend in web application development is toward Single Page Applications (SPAs),where the majority of application functionality is implemented in JavaScript that executes in the browser. SPAs then communicate with REST APIs that act as proxies for a wide range of enterprise services and data stores. XSF provides a means by which we can rapidly develop these kinds of REST services in a Liferay environment.

While it's true that one can create "resource-based" services via the *serveResource()* method and "resource IDs" in a portlet, this approach is very different from the traditional annotation-based approaches used in JAX-RS/Jersey/Spring based REST development. Further, the *"serveResource()"* approach becomes cumbersome when all of the resulting REST services for an application are spread across multiple portlet implementations and resulting WARs. What is preferable is to find a way to build a singular, annotation-based set of REST services that can be leveraged by a number of different portlet applications but deployed and managed as a single artifact.

Finally, its important that any REST services developed for Liferay are able to leverage the Liferay roles and permissions model in order to control access to the services. The access control model in Liferay is very rich and provides a high-level of both control and management over access to elements of the
User Interface (UI). With XSF we can leverage this same model to control whether or not those same permissions and access control mechanisms are als applied to access to REST services. This becomes a critical element
for SPA-based solutions in Liferay.

To summarize then these were the key goals for XSF (and any resulting REST services that are developed using it):

- Support the development of individual REST endpoints as simple annotated Java objects (POJOs). 

- Enable the testing of these POJO-based services in a basic JUnit environment that does not require a web container environment for unit testing.

- Provide a declarative mechanism for defining "routes" to each of the REST endpoints that maps the URI and HTTP method to a particular endpoint implementation (POJO). For readers who have used existing web frameworks such as Rails, Sinatra, Grails, Ratpack, Django, etc. this should be a familiar concept.

- Be Liferay-aware. REST services have particular value in a Liferay-server environment when they can access the logged-in Liferay user and leverage Liferay APIs such as roles and permission-checking, etc. Then the same permissions used to control access to use interface elements can then be used to control access to services used by the UI.

- Leverage Liferay SDKs and hot-deployment support. In effect this means that the services are contained in a Liferay portlet WAR. The benefit is that this provides the maximum amount of support in terms of Liferay features and also enables services to be hot-deployed during development.

**Installation and Setup**

The XSF distribution is a Maven project that builds a Liferay portlet application (WAR) with sample REST services that are built using XSF. It requires that you have Maven installed on your development system, as well as having downloaded and installed the Liferay Maven artifacts for your particular target version of Liferay (see https://www.liferay.com/documentation/liferay-portal/6.1/development/-/ai/installing-required-liferay-artifacts).

(NOTE: as shipped the distribution refers to version 6.2.3 of the Maven artifacts for Liferay CE, so if you are using the most recent version of the Liferay CE server you will probably not need to perform this step as the required Maven artifacts will be automatically downloaded from Maven Central.)

Prior to performing a build you will also need to adjust the *liferay.version* property in the POM as appropriate for the version of your installed Liferay Maven artifacts and server. Once you have configured this property in the POM, then building new REST services is as simple as creating Java classes, annotating them with XSF-based annotations, and executing *mvn package* to create a WAR file. 

Optionally, you can also set the *liferay.home* property in your POM to point to your targeted Liferay server/bundle and then running *mvn install* will also move the created WAR into into your Liferay server's *deploy* directory (in addition to placing the file in your local Maven repository).

To validate that your distribution of XSF is property installed and functional, set the two properties described above in the pom.xml file, then open a terminal session in the XSF distribution and type '**mvn deploy**'. You will see a number of messages in your terminal session as the sample application compiles and then deploys the XSF services portlet into your targeted Liferay installation.

If the build is successful you can then validate that the XSF samples are functional by opening up a browser and entering *http://localhost:8080/delegate/xsf/hello/world/bloggs/joe*. This will invoke one of the sample services provided with XSF and should result in the following JSON being returned to your browser:

```
    {
          "succeeded":true,
          "data" : {"first_name":"joe", "last_name":"bloggs"},
          "message": ""
    }
```

This example is fully described in the documentation on the Samples but receiving the JSON above is an indicator that your XSF installation is functional and ready for use in developing your own custom REST services. Now you can start developing your own custom services in this project and remove the Xtivia "Hello World" samples.

**Further Reading and Documentation**

[Developing Custom Services with XSF](developing_xsf_services.md)

[Authenticating/Authorizing Services with XSF](authorizing_xsf_services.md) 

**Unit Testing**

As an example of how straightforward XSF makes it to write unit tests for the commands that provide your service endpoints the framework samples include a unit test case for the *HelloWorldCommand2* command (*HelloWorldCommand2Test.java* under */src/test*).

A quick glance at the source code and comments for this test file demonstrates that the use of the *IContext's* map-based abstraction for reading (and writing) environment parameters makes it simple and easy to create mocked environments that emulate both success and failure conditions with a minimum of effort on the part of the test writer.

**Licensing and Source Code**

XSF is licensed via LGPL so that you can use it freely in developing your own Liferay application services. 

The distribution includes the XSF framework (as a JAR file) as well as some sample implementations of REST services based on the framework. For those interested in obtaining the XSF source code please email us at *xsf@xtivia.com*.

**Customization**

There are very few required changes needed to develop your own custom services instead of using the supplied Xtivia samples.

By default all service invocations use *[host:port]/delegate/xsf/....* style URLs. While the 'delegate' portion of the URL can only be changed by changing your Liferay configuration, you can easily map the *'xsf'* portion of this URL to something of your own choice by modifying line 19 in *src/main/webapp/WEB-INF/web.xml*.


**Summary and Additional Features**

Admittedly our provided examples are simple, but hopefully we have given you insight into how easy it can be to set up a suite of application REST services using XSF.

XSF services provide a rich mechanism for the development of REST services in Liferay including, 
among other things, use of the Liferay permissions model for services. XSF also supports a number of other valuable features (not documented here) including:

- integration with Liferay logging
- the use of  command "chains"
- plugging in your own custom marshaller (e.g. to use XML instead of JSON) 
- dynamic routing for cases where the mapping of URIs to commands is not known until runtime (i.e., dynamic dispatching). 

Future updates may also add a portlet to the distribution for use in management and visualization
of XSF service throughput and error handling. If you have questions or interest regarding the use of any of these advanced features in XSF please email us at *xsf@xtivia.com*.
