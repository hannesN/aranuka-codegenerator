/*******************************************************************************
 * Copyright 2010 Hannes Niederhausen, Topic Maps Lab
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.topicmapslab.codegenerator.utils;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Sven Krosse
 * 
 */
public class TypeUtility {

	/**
	 * Returns a Java compliant name for the topic
	 * 
	 * @param name a name which will be made Java compliant
	 * @return 
	 */
	public static String getJavaName(String name) {
		StringBuilder builder = new StringBuilder();
		char lastChar = name.charAt(0);
		builder.append(new String(lastChar + "").toUpperCase());
		for (int i = 1; i < name.length(); i++) {
			char c = name.charAt(i);
			if (c != '-' && c != '_' && c != ':' && c != ' ' && '.' != c) {
				if ('-' == lastChar || '_' == lastChar || ':' == lastChar || '.' == lastChar || ' ' == lastChar) {
					builder.append(new String(c + "").toUpperCase());
				} else {
					builder.append(c);
				}
			}
			lastChar = c;
		}

		return builder.toString();
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public static String getFieldName(String name) {
		String tmp = getJavaName(name);
		return Character.toLowerCase(tmp.charAt(0)) + tmp.substring(1);
	}
	
	private static final Map<String, Class<?>> xsdToJavaMappings = new HashMap<String, Class<?>>();
	static {
		xsdToJavaMappings.put("http://www.w3.org/2001/XMLSchema#string", String.class);
		xsdToJavaMappings.put("http://www.w3.org/2001/XMLSchema#integer", int.class);
		xsdToJavaMappings.put("http://www.w3.org/2001/XMLSchema#int", int.class);
		xsdToJavaMappings.put("http://www.w3.org/2001/XMLSchema#float", float.class);
		xsdToJavaMappings.put("http://www.w3.org/2001/XMLSchema#date", Date.class);
		xsdToJavaMappings.put("http://www.w3.org/2001/XMLSchema#dateTime", Date.class);
		xsdToJavaMappings.put("http://www.w3.org/2001/XMLSchema#double", double.class);
		xsdToJavaMappings.put("http://www.w3.org/2001/XMLSchema#iri", URI.class);
		xsdToJavaMappings.put("http://www.w3.org/2001/XMLSchema#boolean", boolean.class);
		xsdToJavaMappings.put("http://www.w3.org/2001/XMLSchema#decimal", float.class);
	}

	/**
	 * Returns the class object for the given datatype
	 * @param datatype the IRI for the xsd datatype
	 * @return
	 */
	public static Class<?> toJavaType(final String datatype) {
		if (xsdToJavaMappings.containsKey(datatype)) {
			return xsdToJavaMappings.get(datatype);
		}
		return String.class;
	}
}
