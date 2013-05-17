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
package de.etecture.opensource.jeemonitoring.utils;

import de.etecture.opensource.jeelogging.api.Log;
import java.lang.management.ManagementFactory;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

/**
 * an interceptor to register a {@link Singleton} as an MBean
 *
 * @author rhk
 */
@Interceptor
@MBean
public class MBeanRegistrationInterceptor {

	@Inject
	Log log;

	@PostConstruct
	public void registerBean(InvocationContext ctx) throws Exception {
		ctx.proceed();
		if (ctx.getTarget() != null) {
			ObjectName objectName = getObjectNameForTarget(ctx.getTarget());
			log.debug("Register %s with objectname %s to the MBeanServer", ctx.getTarget().getClass().getSimpleName(), objectName);
			ManagementFactory.getPlatformMBeanServer().registerMBean(ctx.getTarget(), objectName);
		}
	}

	@PreDestroy
	public void unregister(InvocationContext ctx) throws Exception {
		final ObjectName objectName = getObjectNameForTarget(ctx.getTarget());
		final MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
		if (platformMBeanServer.isRegistered(objectName)) {
			log.debug("Unregister %s with objectname %s from the MBeanServer", ctx.getTarget().getClass().getSimpleName(), objectName);
			ManagementFactory.getPlatformMBeanServer().unregisterMBean(objectName);
		}
		ctx.proceed();
	}

	private ObjectName getObjectNameForTarget(Object target) throws MalformedObjectNameException {
		if (target == null) {
			throw new IllegalArgumentException("target must not be null");
		} else if (ObjectNameProvider.class.isAssignableFrom(target.getClass())) {
			return ((ObjectNameProvider)target).getObjectName();
		} else if (target.getClass().isAnnotationPresent(MBean.class)) {
			return buildObjectName(target.getClass().getAnnotation(MBean.class).value(), target);
		} else {
			throw new IllegalArgumentException("target must be tagged with the @MBean or @DynamicMBean annotation.");
		}
	}

	private ObjectName buildObjectName(String nameDecl, Object target) throws MalformedObjectNameException {
		if (nameDecl == null || nameDecl.length() == 0) {
			// compute the ObjectName from target itself
			nameDecl = String.format("%s:type=%s", target.getClass().getPackage().getName(), target.getClass().getSimpleName());
		} else if (!nameDecl.contains("type=")) {
			nameDecl += ":type=" + target.getClass().getSimpleName();
		}
		return new ObjectName(nameDecl);
	}
}
