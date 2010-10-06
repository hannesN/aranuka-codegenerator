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
/**
 * 
 */
package de.topicmapslab.codegenerator.descriptors;

import static de.topicmapslab.codegenerator.utils.DescriptorUtil.getCodeModeltype;
import static de.topicmapslab.codegenerator.utils.DescriptorUtil.getPrimitiveType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMod;

import de.topicmapslab.codegenerator.CodeGeneratorException;

/**
 * @author Hannes Niederhausen
 * 
 */
public class FieldDescriptor implements IAnnoationDescriptor {

	private String name;

	private String type;

	private boolean many;

	private List<AnnotationDescriptor> annotations;

	private final ClassDescriptor parent;

	private boolean generateAccessor;

	private boolean generateMutator;

	private boolean generateAddAndRemove;

	/**
     * 
     */
	public FieldDescriptor(ClassDescriptor parent) {
		this(parent, true);
	}
	
	public FieldDescriptor(ClassDescriptor parent, boolean generateMethods) {
		this(parent, generateMethods, null, null, false);
	}
	
	

	/**
	 * @param parent the parent {@link ClassDescriptor}
	 * @param generateMethods flag if the accessors and mutators should be generated 
	 * @param name the name of the field
	 * @param type the type of the field
	 * @param many if there's one or more instances (using type of List<Type>)
	 */
	public FieldDescriptor(ClassDescriptor parent, boolean generateMethods, String name, String type, boolean many) {
	    super();
	    this.parent = parent;
		this.parent.addField(this);
		this.generateAccessor = generateMethods;
		this.generateMutator = generateMethods;
		this.generateAddAndRemove = generateMethods;
	    this.name = name;
	    this.type = type;
	    this.many = many;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the real field name, which if isMany is true is <code>getName()+"Set"</code> else
	 * <code>getName()</code> .
	 * @return
	 */
	public String getRealName() {
		if (isMany()) {
			return getName()+"Set";
		}
		return getName();
	}
	
	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the many
	 */
	public boolean isMany() {
		return many;
	}

	/**
	 * @param many
	 *            the many to set
	 */
	public void setMany(boolean many) {
		this.many = many;
	}

	/**
     * @return the parent
     */
    public ClassDescriptor getParent() {
	    return parent;
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
	 * @param generateAccessor
	 *            the generateAccessor to set
	 */
	public void setGenerateAccessor(boolean generateAccessor) {
		this.generateAccessor = generateAccessor;
	}

	/**
	 * @param generateMutator
	 *            the generateMutator to set
	 */
	public void setGenerateMutator(boolean generateMutator) {
		this.generateMutator = generateMutator;
	}
	
	/**
     * @param generateAddAndRemove the generateAddAndRemove to set
     */
    public void setGenerateAddAndRemove(boolean generateAddAndRemove) {
	    this.generateAddAndRemove = generateAddAndRemove;
    }
    
    /**
     * @return the generateAddAndRemove
     */
    public boolean isGenerateAddAndRemove() {
	    return generateAddAndRemove;
    }

	/**
	 * @param clazz
	 * @throws CodeGeneratorException
	 * @throws Exception
	 */
	public void generateField(JDefinedClass clazz, JCodeModel cm) throws Exception {
		JClass typeClass = getCodeModeltype(cm, getType(), isMany());

		String name = getRealName();
		System.out.println(getParent().getQualifiedName()+": "+getName());

		JFieldVar field = null;
		if (typeClass != null) {
			field = clazz.field(JMod.PRIVATE, typeClass, name);
		} else
			field = clazz.field(JMod.PRIVATE, getPrimitiveType(getType()), name);

		for (AnnotationDescriptor ad : getAnnotations()) {
			ad.generateAnnotation(field, cm);
		}

		// generate new methods (getter setter)
		if (generateAccessor) {
			AccessorDescriptor ad = new AccessorDescriptor(parent);
			ad.setFieldName(name);
			ad.setFieldType(getType());
			ad.setCollectionReturnType(isMany());
		}

		if (generateMutator) {
			MutatorDescriptor md = new MutatorDescriptor(parent);
			md.setFieldName(name);
			md.setCollectionReturnType(isMany());
			ParameterDescriptor pd = new ParameterDescriptor(md);
			pd.setType(getType());
			pd.setName(name);
			pd.setMany(isMany());
		}

		// creating add/remove methods
		
		if (isMany() && generateAddAndRemove) {
			AddMethodDescriptor amd = new AddMethodDescriptor(parent);
			amd.setFieldName(getName());
			amd.setFieldType(getType());
			ParameterDescriptor pd = new ParameterDescriptor(amd);
			pd.setType(getType());
			pd.setName(getName());

			RemoveMethodDescriptor rmd = new RemoveMethodDescriptor(parent);
			rmd.setFieldName(getName());
			rmd.setFieldType(getType());
			pd = new ParameterDescriptor(rmd);
			pd.setType(getType());
			pd.setName(getName());
		}
	}
}
