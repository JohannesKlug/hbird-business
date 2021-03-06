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
package org.hbird.business.core;

import java.lang.reflect.Field;
import java.util.Map;

/** Maps all POJO fields to JMS header fields, leaving the POJO unchanged. If
 * you only want specific fields, consider using the 'SelectedFields' mapper.
 *
 * This class can be added to a camel route, to map the fields of a POJO to
 * JMS message header fields. The JMS message, containing the POJO in its 
 * body, will thereafter also list all POJO fields in its header. The mapper
 * will take public, protected as well as private fields, from this class as
 * well as all super classes.
 * 
 * This is useful for filtering in for example routes using ActiveMQ, where only
 * the header fields can be used for filtering / routing.
 *  
 * NOTE: This class is strictly not needed. The same effect can be gained by
 * using Camel scripting the the route, for example 
 * 
 * <route>
 *   <...>
 *   <setHeader name="Name">
 *     <simple>${body.getName}</simple>
 *   </setHeader>
 *   <...>
 * </route>
 *  
 * Which will create a header field 'Name' set to the value of the body objects
 * return of 'getName'. Mapping many fields in many different routes can however 
 * seriously over complicate the route definitions and lead to a bloat of XML.
 * In addition the header fields of any new types of data transfered in a route
 * will have to be reflected in all script based mappings as well. 
 *   
 * Using a single instance of this class, reused in different routes, may thus
 * be a better alternative
 *
 * <bean id="mapper" class="org.hbird.exchange.utils.AllFields"/>
 * 
 * <route id="Route1">
 *   <...>
 *   <to uri="bean:mapper"/>
 *   <...>
 * </route>
 *
 * <route id="Route2">
 *   <...>
 *   <to uri="bean:mapper"/>
 *   <...>
 * </route>
 */ 
public abstract class FieldBasedAccessor {

	/**
	 * Method to list all fields of the 'clazz', including all inherited fields of all
	 * super classes, private as well as protected.
	 * 
	 * @param clazz The name of the class of which all fields should be listed.
	 * @param fields The map of all private, protected and public fields of the clazz and all its super classes. 
	 * The map is keyed on the field name, and the value is the field.
	 */
	protected void recursiveGet(Class<?> clazz, Map<String, Field> fields) {

		for (Field field : clazz.getDeclaredFields()) {
			fields.put(field.getName(), field);
		}
		if (clazz.getSuperclass() != null) {
			recursiveGet(clazz.getSuperclass(), fields);
		}
	}
}
