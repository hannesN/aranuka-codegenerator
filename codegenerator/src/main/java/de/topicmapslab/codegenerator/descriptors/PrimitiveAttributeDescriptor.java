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

import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JCodeModel;

import de.topicmapslab.codegenerator.CodeGeneratorException;

/**
 * @author Hannes Niederhausen
 * 
 */
public class PrimitiveAttributeDescriptor extends AbstractAttributeDescriptor {

	/**
	 * @param parent
	 */
	public PrimitiveAttributeDescriptor(AnnotationDescriptor parent) {
		super(parent);
	}

	
	/**
	 * @param annotation
	 * @param cm
	 * @throws CodeGeneratorException
	 *             if the value is neither a boxes primitive nor {@link String}
	 */
	public void generateAttribute(JAnnotationUse annotation, JCodeModel cm) throws CodeGeneratorException {
		if (Boolean.class.equals(getValue().getClass())) {
			annotation.param(getName(), (Boolean) getValue());
			return;
		}

		if (Byte.class.equals(getValue().getClass())) {
			annotation.param(getName(), (Byte) getValue());
			return;
		}

		if (Short.class.equals(getValue().getClass())) {
			annotation.param(getName(), (Short) getValue());
			return;
		}

		if (Integer.class.equals(getValue().getClass())) {
			annotation.param(getName(), (Integer) getValue());
			return;
		}

		if (Long.class.equals(getValue().getClass())) {
			annotation.param(getName(), (Long) getValue());
			return;
		}

		if (Float.class.equals(getValue().getClass())) {
			annotation.param(getName(), (Float) getValue());
			return;
		}

		if (Double.class.equals(getValue().getClass())) {
			annotation.param(getName(), (Double) getValue());
			return;
		}

		if (Character.class.equals(getValue().getClass())) {
			annotation.param(getName(), (Character) getValue());
			return;
		}

		if (String.class.equals(getValue().getClass())) {
			annotation.param(getName(), (String) getValue());
			return;
		}

		throw new CodeGeneratorException("Illeagel type for Primitive AttributeDescriptor! Type is: "
		        + getValue().getClass().getName());

	}
}
