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

import org.tmapi.core.Locator;
import org.tmapi.core.Topic;

import de.topicmapslab.codegenerator.factories.POJOGenerationException;

/**
 * 
 * @author Sven Krosse
 * 
 */
public class TypeUtility {

	public static String getJavaName(final Topic topic) throws POJOGenerationException {
		return getJavaName(getLocator(topic));
	}

	private static final String getJavaName(Locator locator) {
		String reference = locator.getReference();
		int index = reference.lastIndexOf("/");
		if (index != -1) {
			reference = reference.substring(index + 1);
		}

		return getJavaName(reference);
	}

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

	public static String getFieldName(String name) {
		String tmp = getJavaName(name);
		return Character.toLowerCase(tmp.charAt(0)) + tmp.substring(1);
	}

	public static final String getTypeAttribute(Locator locator) {
		String reference = locator.getReference();
		int index = reference.lastIndexOf("/");
		if (index != -1) {
			reference = reference.substring(index + 1);
		}
		if (reference.equalsIgnoreCase("topic-name")) {
			reference = "tm:name";
		}

		String tmp = getJavaName(reference);
		tmp = Character.toLowerCase(tmp.charAt(0)) + tmp.substring(1);
		return tmp;
	}

	public static final Locator getLocator(Topic topic) throws POJOGenerationException {

		if (!topic.getSubjectIdentifiers().isEmpty()) {
			for (Locator locator : topic.getSubjectIdentifiers()) {
				if (locator.getReference().contains("tinytim")) {
					continue;
				}
				return locator;
			}
		}

		if (!topic.getSubjectLocators().isEmpty()) {
			for (Locator locator : topic.getSubjectLocators()) {
				if (locator.getReference().contains("tinytim")) {
					continue;
				}
				return locator;
			}
		}

		if (!topic.getItemIdentifiers().isEmpty()) {
			for (Locator locator : topic.getItemIdentifiers()) {
				if (locator.getReference().contains("tinytim")) {
					continue;
				}
				return locator;
			}

		}
		throw new POJOGenerationException("Topic with id " + topic.getId() + "has no identifier");
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

	public static Class<?> toJavaType(final Locator datatype) {
		return toJavaType(datatype.getReference());
	}

	public static Class<?> toJavaType(final String datatype) {
		if (xsdToJavaMappings.containsKey(datatype)) {
			return xsdToJavaMappings.get(datatype);
		}
		return String.class;
	}

	public static String field2Method(String fieldName) {
		StringBuilder b = new StringBuilder();
		b.append(Character.toUpperCase(fieldName.charAt(0)));
		b.append(fieldName.substring(1));

		return b.toString();
	}

}
