## Integrating Monitoring in your App

This module based on an EJB jar, so it could be assembled in an EAR that forms the application.

In maven, an EAR module can be add an EJB type dependency:

	<dependency>
		<groupId>de.etecture.commons</groupId>
		<artifactId>monitoring</artifactId>
		<version>2.0.1</version>
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
			<ejb>monitoring</ejb>
		</module>
		...
	</application>

The Monitoring Singleton is then automatically registered as an MBean to the JMX registry at the startup of the application.

The default ObjectName of the Monitoring MBean is: `de.etecture.commons&type=Monitoring`. Such it could be found in a JMX Application under "de.etecture.commons" category and "Monitoring" bean name.
