jee-monitoring
==============

## Introduction

This module implements a generic monitoring facility for any Enterprise Java Application.

## What is Monitoring?

Monitoring means, that an application provides informations about it's current state to an operator.
These informations basically has statistic characteristics and are called KPI (key performance indicator). Several KPI's are provided out-of-the-box with this module, while an application can also provide it's own KPI's that are collected by this module.

## Default KPI's

The KPI implementations provided by this module are:

**Standard KPI's**

| KPI-Name		| Description 										          |
| :------------ | :---------------------------------------------------------- |
| Uptime        | duration since last Server-Start or Deployment              |
| Version       | Die installierte Version des Moduls                         |

**Request-based KPI's**

| KPI-Name		| Description 										          |
| :------------ | :---------------------------------------------------------- |
| requestCount	| count of requests made to the service						  |
| failureCount  | count of requests made to the service that raises a failure |
| MTBF			| Mean-Time-Between-Failure							          |
| FPM           | Failure per Minute                                          |
| FPR           | Failure per Request = (failureCount / requestCount) * 100%  |
| RPM           | count of requsts per Minute                                 |
| ART			| Average Request Time										  |
| SRT			| Shortest Request Time										  |
| LRT			| Longest Request Time										  | 
| CILH          | count of requests made in the last hour                     |

**Aggregating KPI's**

| KPI-Name		| Description 										          |
| :------------ | :---------------------------------------------------------- |
| Health-Status | applications current status of health						  |

## Integrating Monitoring in your App

This module based on an EJB jar, so it could be assembled in an EAR that forms the application.

In maven, an EAR module can be add an EJB type dependency:

	<dependency>
		<groupId>de.etecture.opensource</groupId>
		<artifactId>jee-monitoring</artifactId>
		<version>1.0.0</version>
		<type>ejb</type>
	</dependency>

This will add an entry in the application.xml of an EAR automatically. If not, an application assembler could add it manually:

	<?xml version="1.0" encoding="UTF-8"?>
	<application 
		xmlns="http://java.sun.com/xml/ns/javaee" 
		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
		xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/application_6.xsd"
		version="6">
		...
		<module>
			<ejb>jee-monitoring</ejb>
		</module>
		...
	</application>

The Monitoring Singleton is then automatically registered as an MBean to the JMX registry at the startup of the application.

The default ObjectName of the Monitoring MBean is: `de.etecture&type=Monitoring`. Such it could be found in a JMX Application under "de.etecture" category and "Monitoring" bean name.

## Adopting Monitoring in your Application

### Default KPI's

Most of the default KPI's are working out-of-the-box. So there is no neccesarity for a developer here.

An exception to this is the "version" KPI, which should be configured inside the application.xml via an env-entry "Monitoring.Version".

### Request based KPI's

The Request-KPI's are based on the CDI-Event `RequestEvent`. So when an application wants to tell the monitoring facility, that a request was made to this service, it has to fire an instance of this event:

~~~~~
public class AnyService {
    @Inject
    Events<RequestEvent> events;
	
    public void methodThatWillBeTracked() {
        // do whatever you should do in this method
        events.fire(new RequestEvent(...).finished());
    }
}
~~~~~

Another way to fire RequestEvents is the way by using the `RequestTrackingInterceptor` and its corresponding Interceptor-Binding with `@RequestTracking` Annotation:

~~~~~
@RequestTracking
public class AnyService {

    public void methodThatWillBeTracked() {
        // do whatever you should do in this method
    }
}
~~~~~

This interceptor ensures, that all methods inside the service will be tracked as a request. Beside this, the duration of the request will be tracked too.

If there is a failure (a method throws an exception, that is not an `@ApplicationException`) inside the request call, the interceptor recognizes this failure and tracks it inside the RequestEvent (so that MTBF or failureCount can do it's statistics).
