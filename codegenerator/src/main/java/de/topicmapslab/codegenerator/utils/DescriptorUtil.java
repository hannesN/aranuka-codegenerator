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

import java.util.Set;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;

/**
 * @author Hannes Niederhausen
 *
 */
public class DescriptorUtil {
	
	
	/**
     * @param cm
     * @param type
     * @param isMany
     * @return
     */
    public static JClass getCodeModeltype(JCodeModel cm, String type, boolean isMany) {
	    JClass typeClass = null;
		if (isMany) {
			if (hasPrimitiveType(type)) {
				typeClass = cm.ref(Set.class).narrow(getBoxed(type));
			} else {
				typeClass = cm.ref(type);
				typeClass = cm.ref(Set.class).narrow(typeClass);
			}
		} else {
			typeClass = hasPrimitiveType(type) ? null : cm.ref(type);
		}
	    return typeClass;
    }
	
	/**
	 * @param type
	 * @return
	 */
	public static Class<?> getBoxed(String type) {
		if ("boolean".equals(type))
			return Boolean.class;
		if ("byte".equals(type))
			return Byte.class;
		if ("short".equals(type))
			return Short.class;
		if ("int".equals(type))
			return Integer.class;
		if ("long".equals(type))
			return Long.class;
		if ("float".equals(type))
			return Float.class;
		if ("double".equals(type))
			return Double.class;
		if ("char".equals(type))
			return Character.class;

		throw new RuntimeException("Invalid type! " + type + " is no primitive datatype");
	}

	/**
	 * @param type
	 * @return
	 */
	public static Class<?> getPrimitiveType(String type) {
		if ("boolean".equals(type))
			return Boolean.TYPE;
		if ("byte".equals(type))
			return Byte.TYPE;
		if ("short".equals(type))
			return Short.TYPE;
		if ("int".equals(type))
			return Integer.TYPE;
		if ("long".equals(type))
			return Long.TYPE;
		if ("float".equals(type))
			return Float.TYPE;
		if ("double".equals(type))
			return Double.TYPE;
		if ("char".equals(type))
			return Character.TYPE;

		throw new RuntimeException("Invalid type! " + type + " is no primitive datatype");
	}

	public static boolean hasPrimitiveType(String type) {
		return ("boolean".equals(type)) || ("byte".equals(type)) || ("short".equals(type))
		        || ("int".equals(type)) || ("long".equals(type)) || ("float".equals(type))
		        || ("double".equals(type)) || ("char".equals(type));
	}
	
	public static boolean isBoolean(String type) {
		return ("boolean".equals(type)) || (Boolean.class.getName().equals(type));
	}
	
	public static String field2Method(String fieldName) {
		StringBuilder b = new StringBuilder();
		b.append(Character.toUpperCase(fieldName.charAt(0)));
		b.append(fieldName.substring(1));
		
		return b.toString();
	}
}
