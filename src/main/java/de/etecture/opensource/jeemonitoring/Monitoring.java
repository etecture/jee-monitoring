/*
 * This file is part of the ETECTURE Open Source Community Projects.
 *
 * Copyright (c) 2013 by:
 *
 * ETECTURE GmbH
 * Darmstädter Landstraße 112
 * 60598 Frankfurt
 * Germany
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the author nor the names of its contributors may be
 *    used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package de.etecture.opensource.jeemonitoring;

import de.etecture.opensource.jeelogging.api.Log;
import de.etecture.opensource.jeelogging.extension.AutoLogging;
import de.etecture.opensource.jeelogging.extension.AutoLoggingInterceptor;
import de.etecture.opensource.jeemonitoring.api.Alarm;
import de.etecture.opensource.jeemonitoring.api.KPI;
import de.etecture.opensource.jeemonitoring.utils.MBean;
import de.etecture.opensource.jeemonitoring.utils.MBeanRegistrationInterceptor;
import de.etecture.opensource.jeemonitoring.utils.ObjectNameProvider;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

/**
 * this is the Monitoring Singleton, that will be aggregating all KPI's and
 * registers them as MBean in the JMX registry
 *
 * @author rhk
 */
@Singleton
@Startup
@Interceptors({MBeanRegistrationInterceptor.class, AutoLoggingInterceptor.class})
@MBean
@AutoLogging
public class Monitoring implements ObjectNameProvider, DynamicMBean {

	@Resource(name = "Monitoring.ObjectName")
	String objectNameString = "de.etecture:type=Monitoring";

	@Resource(name = "Monitoring.Description")
	String description = "This MBean provides Monitoring functionality by presenting a list of KPI's.";

	@Inject
	Instance<KPI> kpis;

	@Inject
	Instance<Alarm> alarms;

	@Inject
	Log log;

	@Override
	public ObjectName getObjectName() {
		try {
			return new ObjectName(objectNameString);
		} catch (MalformedObjectNameException ex) {
			throw new IllegalStateException("cannot build objectname: ", ex);
		}
	}

	@Override
	public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
		for (KPI kpi : kpis) {
			if (kpi.getName().equals(attribute)) {
				return kpi.getValue();
			}
		}
		throw new AttributeNotFoundException(String.format("cannot found attribute %s in monitoring mbean.", attribute));
	}

	@Override
	public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
	}

	@Override
	public AttributeList getAttributes(String[] names) {
		AttributeList list = new AttributeList();
		for (String name : names) {
			try {
				Object value = getAttribute(name);
				if (value != null) {
					list.add(new Attribute(name, value));
				}
			} catch (AttributeNotFoundException | MBeanException | ReflectionException ex) {
				log.warn("cannot get attribute %s in Monitoring mbean: ", ex, name);
			}
		}
		return list;
	}

	@Override
	public AttributeList setAttributes(AttributeList list) {
        Attribute[] attrs = (Attribute[]) list.toArray(new Attribute[list.size()]);
		AttributeList retlist = new AttributeList();
		for (Attribute attr : attrs) {
			String name = attr.getName();
			Object value = attr.getValue();
			try {
				setAttribute(attr);
				retlist.add(new Attribute(name, value));
			} catch (AttributeNotFoundException | InvalidAttributeValueException | MBeanException | ReflectionException ex) {
				log.warn("cannot set attribute %s to %s in Monitoring mbean: ", ex, name, value);
			}
		}
		return retlist;
	}

	@Override
	public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException, ReflectionException {
		if ("clear".equals(actionName)) {
			for (KPI kpi : kpis) {
				kpi.clear();
			}
		} else {
			for (Alarm alarm : alarms) {
				if (("toggle" + alarm.getName()).equals(actionName)) {
					alarm.setEnabled((Boolean) params[0]);
				}
				if (("is" + alarm.getName() + "Enabled").equals(actionName)) {
					return alarm.isEnabled();
				}
			}
		}
		return null;
	}

	@Override
	public MBeanInfo getMBeanInfo() {
		List<MBeanAttributeInfo> attrs = new ArrayList<>();
		List<MBeanOperationInfo> opers = new ArrayList<>();

		for (KPI kpi : kpis) {
			attrs.add(new MBeanAttributeInfo(kpi.getName(), kpi.getType(), kpi.getDescription(), true, false, false));
		}
		MBeanParameterInfo[] enableParams = new MBeanParameterInfo[]{
			new MBeanParameterInfo("enable", Boolean.class.getName(), "wether or not the alarm should be enabled.")
		};
		for (Alarm alarm : alarms) {
			opers.add(new MBeanOperationInfo("toggle" + alarm.getName(), "enables or disables the " + alarm.getName(), enableParams, "void", MBeanOperationInfo.ACTION));
			opers.add(new MBeanOperationInfo("is" + alarm.getName() + "Enabled", "check wether or not the " + alarm.getName() + " is enabled.", null, Boolean.class.getName(), MBeanOperationInfo.ACTION));
		}

		opers.add(new MBeanOperationInfo("clear", "clears the statistics", null, "void", MBeanOperationInfo.ACTION));

		return new MBeanInfo(
				this.getClass().getName(),
				this.description,
				attrs.toArray(new MBeanAttributeInfo[attrs.size()]),
				null, // constructors (can not be constructed by JMX client)
				opers.toArray(new MBeanOperationInfo[opers.size()]),
				null); // notifications
	}
}
