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

package de.topicmapslab.codegenerator.descriptors;

import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JCodeModel;

import de.topicmapslab.codegenerator.CodeGeneratorException;

/**
 * @author Hannes Niederhausen
 * 
 */
public class PrimitiveArrayAttributeDescriptor extends AbstractAttributeDescriptor {

	/**
	 * @param parent
	 */
	public PrimitiveArrayAttributeDescriptor(AnnotationDescriptor parent) {
		super(parent);
	}

	/**
	 * @param annotation
	 * @param cm
	 * @throws CodeGeneratorException
	 *             if the value is neither a boxes primitive nor {@link String}
	 */
	public void generateAttribute(JAnnotationUse annotation, JCodeModel cm) throws CodeGeneratorException {

		JAnnotationArrayMember pa = annotation.paramArray(getName());

		Class<? extends Object> valClass = getValue().getClass();
		if (!valClass.isArray())
			throw new CodeGeneratorException("Value is not an array!");

		Object[] values = (Object[]) getValue();

		for (Object o : values) {
			if (Boolean.class.getName().equals(o.getClass())) {
				pa.param((Boolean) o);
			} else if (Byte.class.equals(o.getClass())) {
				pa.param((Byte) o);
			} else if (Short.class.equals(o.getClass())) {
				pa.param((Short) o);
			} else if (Integer.class.equals(o.getClass())) {
				pa.param((Integer) o);
			} else if (Long.class.equals(o.getClass())) {
				pa.param((Long) o);
			} else if (Float.class.equals(o.getClass())) {
				pa.param((Float) o);
			} else if (Double.class.equals(o.getClass())) {
				pa.param((Double) o);
			} else if (Character.class.equals(o.getClass())) {
				pa.param((Character) o);
			} else if (String.class.equals(o.getClass())) {
				pa.param((String) o);
			} else {
				throw new CodeGeneratorException("Illeagel type for Primitive AttributeDescriptor! Type is: "
				        + o.getClass().getName());
			}
		}
	}
}
