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
package de.etecture.opensource.jeemonitoring.kpis;

import de.etecture.opensource.jeemonitoring.api.AbstractKPI;
import de.etecture.opensource.jeemonitoring.api.KPI;
import de.etecture.opensource.jeemonitoring.api.requests.Failed;
import de.etecture.opensource.jeemonitoring.api.requests.RequestEvent;
import java.util.concurrent.atomic.AtomicLong;
import javax.ejb.Singleton;
import javax.enterprise.event.Observes;

/**
 * this is a {@link KPI} that tracks requests
 *
 * @author rhk
 */
@Singleton
public class FailedRequestCountKPI extends AbstractKPI<AtomicLong> {

	public FailedRequestCountKPI() {
		super("failureCount", AtomicLong.class, "count of requests made to the service that raises a failure", new AtomicLong(0));
	}

	public void onRequestEvent(@Observes @Failed RequestEvent event) {
		long oldValue = super.getValue().getAndIncrement();
		super.fireChangeEvent(oldValue, super.getValue().longValue());
	}

	@Override
	public void clear() {
		super.getValue().set(0l);
	}
}
