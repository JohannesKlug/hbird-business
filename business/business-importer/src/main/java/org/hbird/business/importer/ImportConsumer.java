/**
 * villemos solutions [space^] (http://www.villemos.com) 
 * Probe. Send. Act. Emergent solution. 
 * Copyright 2011 Gert Villemos
 * All Rights Reserved.
 * 
 * Released under the Apache license, version 2.0 (do what ever
 * you want, just dont claim ownership).
 * 
 * NOTICE:  All information contained herein is, and remains
 * the property of villemos solutions, and its suppliers
 * if any. The intellectual and technical concepts contained
 * herein are proprietary to villemos solutions
 * and its suppliers and may be covered by European and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * 
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from villemos solutions.
 * 
 * And it wouldn't be nice either.
 * 
 */
package org.hbird.business.importer;

import org.apache.camel.Processor;
import org.apache.camel.impl.ScheduledPollConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The HelloWorld consumer.
 */
public class ImportConsumer extends ScheduledPollConsumer {

	private static final transient Logger LOG = LoggerFactory.getLogger(ImportConsumer.class);

	private final ImportEndpoint endpoint;

	public ImportConsumer(ImportEndpoint endpoint, Processor processor) {
		super(endpoint, processor);
		this.endpoint = endpoint;
	}

	@Override
	protected int poll() throws Exception {


		return 1;
	}


}
