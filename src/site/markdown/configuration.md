## Configuring the name of the Monitoring MBean

The Monitoring MBean can be configured to change the ObjectName with which the MBean will be configured as well as the description. This is done by adding an Environment Entry either in the ejb-jar.xml or the application.xml.

It is recommend to add this `<env-entry>` in the application.xml, because reassembling of the EJB jar with another ejb-jar.xml is dangerous.

Here's an example for configuring the ObjectName to `de.acme.whatever&type=MyFancyMonitoring`:

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
		<env-entry>
			<description>configures the objectname for the monitoring mbean</description>
			<env-entry-name>Monitoring.ObjectName</env-entry-name>
			<env-entry-type>java.lang.String</env-entry-type>
			<env-entry-value>de.acme.whatever&type=MyFancyMonitoring</env-entry-value>
		</env-entry>
		<env-entry>
			<description>configures the description for the monitoring mbean</description>
			<env-entry-name>Monitoring.Description</env-entry-name>
			<env-entry-type>java.lang.String</env-entry-type>
			<env-entry-value>This is my fancy monitoring. Look! It can change it's description...</env-entry-value>
		</env-entry>
		...
	</application>

