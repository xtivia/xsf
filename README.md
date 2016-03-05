##Using the Xtivia Services Framework (XSF) to Create REST Services in Liferay
Version 1.1.0 Released 27 Oct 2015

Version 1.1.0 adds the following features:
(1) Support for @Route on methods in addition to classes
(2) Moves Jackson version to 2.6.3


XSF is a framework that Xtivia has created (and used for multiple client engagements) that enables the rapid development of custom REST services in Liferay. REST services developed using XSF are coded in a fashion similar to JAX-RS or Jersey but can take advantages of Liferay features such as roles/permissions.

###Source Code

This distribution now includes the source code for the XSF framework itself in the *framework* directory of this repository.

###Documentation

The full documentation for XSF can be downloaded [here](https://raw.githubusercontent.com/xtivia/xsf/master/xsf_userguide.pdf). 

###Sample XSF Application

As the documentation describes the XSF library is also currently available as Maven artifacts from Maven Central, including both the main JAR as well as an archetype that can be used for building XSF-based applications (these are deployed as Liferay portlet plugins). This distribution provides an as-generated example of an application/plugin that was previously generated using the XSF archetype.

The download here can then serve as a quick starting point for XSF experimentation. Please refer to the XSF documentation (see link above for more details).

##Building

Given Liferay's clear direction (starting with Liferay 7) of migrating its build tooling to Gradle, XSF provides support at present for both Gradle- and Maven-based builds with the expectation of an eventual conversion to the sole use of Gradle.

##Questions

If you have questions or interest regarding the use of any of these advanced features in XSF please email us at *xsf@xtivia.com*.
