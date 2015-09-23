XSF is fully integrated with the Liferay authentication/authorization system. What this means to you as a services developer is that you can access information about the currently logged-in Liferay user from within your REST services, and you can also leverage mechanisms that allow you to create access control rules for services.

###XSF Service Authentication and Authorization Mechanisms

In its simplest form an XSF command is marked as requiring authentication via the *authenticated* attribute on the @Route annotation attached to it. The default value for this attribute is TRUE which means that by default each command that you create will require that the user be logged in to Liferay in order to access it. Commands that are intended to be used as public/guest routes should have this value set to FALSE (as can be seen in the "HelloWorld" commands provided in *com.xtivia.xsf.samples*.

Commands can indicate to XSF that they require some type of customized authorization by implementing the *IAuthorized* interface. This interface requires the implementation of a single method, *authorize()*, that must return a true/false value to indicate whether or not authorization is successful.

So essentially if a command implements the *IAuthorized* interface it is indicating to the XSF framework "yes, I require some customized authorization before I can be accessed", and the command is responsible for implementing the required authorization.

###Use of the Context in Authorization Logic

When executing the *authorize()* method the supplied context object provides two important elements that custom authorizers can use to implement whatever unique access control requirements that they need. One of these has been discussed before and that is the User object representing the user that has logged in to Liferay. The other key object that can be accessed using the key value *ILiferayCommandKeys.LIFERAY_PERMISSION_CHECKER* is a Liferay permission checker object that can be used to determine whether or not the logged in user meets custom criteria in terms of assigned Liferay roles and permissions. For further details refer to the sample *OrgAuthorizedCommand* class in the package *xsf.samples.auth*.

###Authorizer Examples

We have provided a static class named *XsfAuthUtil.java* as an example helper class that provides a number of utility methods that could be used by custom authorizers to execute Liferay-specific permission checks. This file is located in the package *xsf.samples.auth.* In this same package you will find examples that implement authorization logic based on whether or not the user is in a portal admin role, is in a specific portal (regular) role, and even custom authorization based on (as an example) whether or not any of the user's assigned organization roles would grant them access to view blogs data. Please refer to these examples for assistance in developing your own custom authorization logic.