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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JType;

import de.topicmapslab.codegenerator.utils.DescriptorUtil;

/**
 * @author Hannes Niederhausen
 * 
 */
public class MethodDescriptor extends AbstractModifiedDescriptor implements IAnnoationDescriptor {

	private String name;

	private String returnType;

	private boolean collectionReturnType;

	private final ClassDescriptor parent;

	private List<AnnotationDescriptor> annotations;

	private List<ParameterDescriptor> parameters;

	/**
	 * Constructor
	 * 
	 * @param parent parent {@link ClassDescriptor}
	 */
	public MethodDescriptor(ClassDescriptor parent) {
		this.parent = parent;
		this.parent.addMethod(this);
		this.collectionReturnType=false;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the returnType
	 */
	public String getReturnType() {
		return returnType;
	}

	/**
	 * @param returnType
	 *            the returnType to set
	 */
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	/**
	 * @return the collectionReturnType
	 */
	public boolean isCollectionReturnType() {
		return collectionReturnType;
	}

	/**
	 * @param collectionReturnType
	 *            the collectionReturnType to set
	 */
	public void setCollectionReturnType(boolean collectionReturnType) {
		this.collectionReturnType = collectionReturnType;
	}

	/**
	 * @return the annotations
	 */
	public List<AnnotationDescriptor> getAnnotations() {
		if (annotations == null)
			return Collections.emptyList();
		return annotations;
	}

	/**
	 * @param annotation
	 *            the annotation to add
	 */
	public void addAnnotations(AnnotationDescriptor annotation) {
		if (this.annotations == null)
			this.annotations = new ArrayList<AnnotationDescriptor>();
		this.annotations.add(annotation);
	}

	/**
	 * @param annotation
	 *            the annotation to remove
	 */
	public void removeAnnotations(AnnotationDescriptor annotation) {
		if (this.annotations != null)
			this.annotations.remove(annotation);
	}

	/**
	 * @return the parameters
	 */
	public List<ParameterDescriptor> getParameters() {
		if (parameters == null)
			return Collections.emptyList();
		return parameters;
	}

	/**
	 * @param parameterDescriptor
	 */
	public void addParameter(ParameterDescriptor pd) {
		if (this.parameters == null)
			this.parameters = new ArrayList<ParameterDescriptor>();
		this.parameters.add(pd);
	}

	/**
	 * @param parameterDescriptor
	 */
	public void removeParameter(ParameterDescriptor pd) {
		if (this.parameters != null)
			this.parameters.remove(pd);
	}

	/**
	 * Generates the method using the codemodel factory
	 * @param type the codemodel representation of the parent
	 * @param cm the factory
	 * @throws Exception
	 */
	public final void generateMethod(JDefinedClass type, JCodeModel cm) throws Exception {
		JType retType = cm.VOID;
		if ((getReturnType()!=null) && (!"void".equals(getReturnType()))) {
			retType = DescriptorUtil.getCodeModeltype(cm, getReturnType(), isCollectionReturnType());
		}
		
		JMethod m = null;
		if (retType==null) {
			m = type.method(getMods(), DescriptorUtil.getPrimitiveType(getReturnType()), getName());
		} else {
			m = type.method(getMods(), retType, getName());
		}
		generateMethodBody(m, cm);
		
		for (AnnotationDescriptor ad : getAnnotations()) {
			ad.generateAnnotation(m, cm);
		}
		
		// adding params
		for (ParameterDescriptor pd : getParameters()) {
			pd.generateParameter(m, cm);
		}
	}

	protected void generateMethodBody(JMethod m, JCodeModel cm) {
		m.body().block().directStatement("// TODO implement the method");
	}

	/**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
	    StringBuilder builder = new StringBuilder();
	    builder.append("MethodDescriptor [name=");
	    builder.append(name);
	    builder.append(", returnType=");
	    builder.append(returnType);
	    builder.append(", parameters=");
	    builder.append(parameters);
	    builder.append("]");
	    return builder.toString();
    }
}
