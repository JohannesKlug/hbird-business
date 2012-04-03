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
 */
package org.hbird.business.simpleparametersimulator;

import java.util.Date;

import org.apache.camel.Exchange;

public class TimeParameter extends BaseParameter {

	/***/
	private static final long serialVersionUID = 3583468275604010575L;

	public TimeParameter(String name, String description, String unit) {
		super(name, description, 0d, unit);
	}

	@Override
	protected void process(Exchange exchange) {
		newInstance();
		exchange.getIn().setBody((new Date()).getTime());		
	}
}