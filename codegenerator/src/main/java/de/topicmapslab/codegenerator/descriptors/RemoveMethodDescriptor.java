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

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JMethod;

/**
 * @author Hannes Niederhausen
 * 
 */
public class RemoveMethodDescriptor extends MethodDescriptor {

	private String fieldName;
	private String fieldType;

	/**
	 * @param parent
	 */
	public RemoveMethodDescriptor(ClassDescriptor parent) {
		super(parent);
		setReturnType("void");
	}

	/**
	 * @return the fieldName
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * @param fieldName
	 *            the fieldName to set
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
		updateName();
	}

	/**
	 * @return the fieldType
	 */
	public String getFieldType() {
		return fieldType;
	}

	/**
	 * @param fieldType
	 *            the fieldType to set
	 */
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
		updateName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void generateMethodBody(JMethod m, JCodeModel cm) {
		String fieldName = getFieldName() + "Set";
		JFieldRef ref = JExpr._this().ref(fieldName);
		JConditional _if = m.body()._if(ref.ne(JExpr._null()));
		_if._then().block().invoke(ref, "remove").arg(JExpr.ref(getFieldName()));
	}

	/**
     * 
     */
	private void updateName() {
		if (getFieldName() == null)
			return;
		String name = "remove";
		name += Character.toUpperCase(getFieldName().charAt(0)) + getFieldName().substring(1);
		setName(name);
	}
}
