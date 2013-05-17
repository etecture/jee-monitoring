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
package de.etecture.opensource.jeemonitoring.api.requests;

/**
 * this is a RequestEvent.
 *
 * <p>A request event should be fired by any service to increment the {@link RequestCountKPI}.</p>
 *
 * <p>Here's an example on how to fire this Event:
 *
 * <pre>
public class AnyService {
    &#64;Inject
  	Events&lt;RequestEvent&gt; events;

  	public void methodThatWillBeTracked() {
  	    events.fire(new RequestEvent());
    }
}
 * </pre>
 * </p>
 *
 * <p>
 * Another way to fire RequestEvents is the way by using the {@link RequestTrackingInterceptor} and its corresponding Interceptor-Binding with &#64;{@link RequestTracking} Annotation
 * </p>
 *
 * @author rhk
 */
public class RequestEvent {

	private final String target;
	private Throwable throwable = null;
	private long started = System.currentTimeMillis();
	private long finished;

	/**
	 * constructs a new RequestEvent
	 *
	 * @param target
	 */
	public RequestEvent(String target) {
		this.target = target;
	}

	/**
	 * returns the target for this event
	 *
	 * @return
	 */
	public String getTarget() {
		return target;
	}

	/**
	 * returns true, when the target, that raises this event has failed
	 *
	 * @return
	 */
	public boolean isFailed() {
		return throwable != null;
	}

	/**
	 * returns the throwable that is the cause for failing
	 *
	 * @return
	 */
	public Throwable getThrowable() {
		return throwable;
	}

	/**
	 * returns the time, when the request was started
	 *
	 * @return
	 */
	public long getStarted() {
		return started;
	}

	/**
	 * returns the time, when the request has finished
	 *
	 * @return
	 */
	public long getFinished() {
		return finished;
	}

	/**
	 * returns the duration of this request
	 *
	 * @return
	 */
	public long getDuration() {
		return finished - started;
	}

	/**
	 * mark this request object finished
	 *
	 * @return
	 */
	public RequestEvent finished() {
		finished = System.currentTimeMillis();
		return this;
	}

	/**
	 * adds a throwable to this event
	 *
	 * @param throwable
	 */
	public RequestEvent finished (Throwable throwable) {
		this.throwable = throwable;
		finished = System.currentTimeMillis();
		return this;
	}

}
