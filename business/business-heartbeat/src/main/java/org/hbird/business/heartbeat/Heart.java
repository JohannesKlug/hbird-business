/**
 * Licensed to the Hummingbird Foundation (HF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The HF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */package org.hbird.business.heartbeat;

import java.util.Date;

import org.hbird.exchange.heartbeat.Heartbeat;

public class Heart {

	/** The name / ID of the component. */
	protected String componentId = "unknown";
	
	/** The period between heartbeats. The time to-wait for the next expected beat,*/
	protected long period = 10000;
	
	public Heart(String componentId, long period) {
		super();
		this.componentId = componentId;
		this.period = period;
	}

	public Heartbeat process() {
		Date now = new Date();
		return new Heartbeat(componentId, now.getTime(), now.getTime() + period);
	}
}
