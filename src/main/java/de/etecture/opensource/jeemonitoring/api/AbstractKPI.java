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
package de.etecture.opensource.jeemonitoring.api;

import de.etecture.opensource.jeemonitoring.events.KPIChangedEvent;
import de.etecture.opensource.jeemonitoring.utils.ForKPIBinding;
import javax.enterprise.event.Event;
import javax.inject.Inject;

/**
 * this is an abstract implementation of the most common methods of the {@link KPI} interface
 *
 * @author rhk
 */
public abstract class AbstractKPI<T> implements KPI<T> {

	private final String name;
	private final String type;
	private final String description;
	private T value;

	@Inject
	Event<KPIChangedEvent> changedEvents;

	protected AbstractKPI(String name, Class<T> type, T initialValue) {
		this(name, type, "", initialValue);
	}

	protected AbstractKPI(String name, Class<T> type, String description, T initialValue) {
		this.name = name;
		this.value = initialValue;
		this.description = description;
		this.type = type.getName();
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public T getValue() {
		return this.value;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getType() {
		return type;
	}

	protected void setValue(T newValue) {
		T oldValue = this.value;
		this.value = newValue;
		fireChangeEvent(oldValue, newValue);
	}

	protected void fireChangeEvent(Object oldValue, Object newValue) {
		changedEvents.select(new ForKPIBinding(this.name)).fire(new KPIChangedEvent(this.name, oldValue, newValue));
	}
}
