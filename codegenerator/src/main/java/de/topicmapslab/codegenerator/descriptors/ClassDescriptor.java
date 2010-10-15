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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JClassContainer;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

import de.topicmapslab.codegenerator.CodeGeneratorException;

/**
 * The class describing a class and provides the code generation with CodeModel.
 * 
 * <p>
 * class is public with no inheritance
 * </p>
 * 
 * @author Hannes Niederhausen
 * 
 */
public class ClassDescriptor extends AbstractModifiedDescriptor implements IAnnoationDescriptor, IClassContainer {

	private final IClassContainer parent;

	/**
	 * name of the class
	 */
	private String name;

	private String type;

	private List<String> implementsList;

	private String extendsName;

	private List<MethodDescriptor> methods;

	private List<ClassDescriptor> innerClasses;

	private List<AnnotationDescriptor> annotations;

	private List<FieldDescriptor> fields;
	
	/**
	 * THis fields will be used to generate the equals and hashcode methods
	 */
	private List<FieldDescriptor> idFields;

	/**
	 * @param parent
	 */
	public ClassDescriptor(IClassContainer parent) {
		super();
		this.parent = parent;
		this.parent.addChildClass(this);
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
	 * @param extendsName
	 *            the extendsName to set
	 */
	public void setExtendsName(String extendsName) {
		this.extendsName = extendsName;
	}

	/**
	 * @return the extendsName
	 */
	public String getExtendsName() {
		return extendsName;
	}

	/**
	 * @return the implementsList
	 */
	public List<String> getImplementsList() {
		if (implementsList == null)
			return Collections.emptyList();
		return implementsList;
	}

	public void addImplementInterface(String qName) {
		if (implementsList == null)
			implementsList = new ArrayList<String>();
		implementsList.add(qName);
	}

	public void removeImplementInterface(String qName) {
		if (implementsList != null)
			implementsList.add(qName);
	}

	/**
	 * @return the innerClasses
	 */
	public List<ClassDescriptor> getChildClasses() {
		if (innerClasses == null)
			return Collections.emptyList();
		return innerClasses;
	}

	/**
	 * @param annotation
	 *            the annotation to add
	 */
	public void addChildClass(ClassDescriptor cd) {
		if (this.innerClasses == null)
			this.innerClasses = new ArrayList<ClassDescriptor>();
		this.innerClasses.add(cd);
	}

	/**
	 * @param annotation
	 *            the annotation to remove
	 */
	public void removeChildClass(ClassDescriptor cd) {
		if (this.innerClasses != null)
			this.innerClasses.remove(cd);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<AnnotationDescriptor> getAnnotations() {
		if (annotations == null)
			return Collections.emptyList();
		return annotations;
	}

	/**
	 * @return the methods
	 */
	public List<MethodDescriptor> getMethods() {
		if (methods == null)
			return Collections.emptyList();
		return methods;
	}

	public void addMethod(MethodDescriptor md) {
		if (methods == null)
			methods = new ArrayList<MethodDescriptor>();
		methods.add(md);
	}

	public void removeMethod(MethodDescriptor md) {
		if (methods != null)
			methods.remove(md);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addAnnotations(AnnotationDescriptor annotation) {
		if (this.annotations == null)
			this.annotations = new ArrayList<AnnotationDescriptor>();
		this.annotations.add(annotation);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeAnnotations(AnnotationDescriptor annotation) {
		if (this.annotations != null)
			this.annotations.remove(annotation);
	}

	/**
	 * @return the fields
	 */
	public List<FieldDescriptor> getFields() {
		if (fields == null)
			return Collections.emptyList();
		return fields;
	}

	/**
	 * @param fields
	 *            the fields to add
	 */
	public void addField(FieldDescriptor field) {
		if (this.fields == null)
			this.fields = new ArrayList<FieldDescriptor>();
		this.fields.add(field);
	}

	/**
	 * @param fields
	 *            the fields to remove
	 */
	public void removeFields(FieldDescriptor field) {
		if (this.fields == null)
			this.fields = new ArrayList<FieldDescriptor>();
		this.fields.remove(field);
	}
	
	/**
     * @return the idFields
     */
    public List<FieldDescriptor> getIdFields() {
    	if (idFields==null)
    		return Collections.emptyList();
    	return idFields;
    }
    
    public void addIdField(FieldDescriptor fd) {
    	if (idFields==null)
    		idFields = new ArrayList<FieldDescriptor>();
    	idFields.add(fd);    	
    }
    
    public void removeIdField(FieldDescriptor fd) {
    	if (idFields!=null)
    		idFields.remove(fd);    	
    }
	
	public String getQualifiedName() {
		return parent.getName()+"."+getName();
	}

	/**
     * @return the parent
     */
    protected IClassContainer getParent() {
	    return parent;
    }
	
	/**
	 * @param pack
	 * @throws JClassAlreadyExistsException
	 * @throws CodeGeneratorException
	 */
	public void generateClass(JClassContainer pack, JCodeModel cm) throws Exception {
		int mods = getMods();

		JDefinedClass clazz = cm._getClass(parent.getName() + "." + getName());
		if (clazz == null)
			clazz = pack._class(mods, getName());
		
		for (String qName : getImplementsList()) {
			clazz._implements(cm.ref(qName));
		}
		
		if (getExtendsName()!=null) {
			clazz._extends(cm.ref(getExtendsName()));
		}

		for (AnnotationDescriptor ad : getAnnotations()) {
			ad.generateAnnotation(clazz, cm);
		}

		for (FieldDescriptor fd : getFields()) {
			fd.generateField(clazz, cm);
		}

		for (MethodDescriptor md : getMethods()) {
			md.generateMethod(clazz, cm);
		}

		for (ClassDescriptor cd : getChildClasses()) {
			cd.generateClass(clazz, cm);
		}
		
		
		if (getIdFields().isEmpty())
			return;
		
		generateEqualsMethod(clazz, cm);
		generateHashCodeMethod(clazz, cm);
	}

	/**
     * @param clazz
     * @param cm
     */
    private void generateHashCodeMethod(JDefinedClass clazz, JCodeModel cm) {
    	JMethod method = clazz.method(JMod.PUBLIC, cm.BOOLEAN, "equals");
    	JVar p = method.param(Object.class, "obj");
    	
    	// if it is the same object return true
    	method.body()._if(JExpr._this().eq(p))._then()._return(JExpr.TRUE);
    	
    	// if obj (p) is null return false
    	method.body()._if(p.eq(JExpr._null()))._then()._return(JExpr.FALSE);
    	
    	// if (getClass() != obj.getClass())
    	method.body()._if(JExpr._this().invoke("getClass").ne(p.invoke("getClass")))._then()._return(JExpr.FALSE);
    	
    	// create temp var
    	JVar castedObj = method.body().decl(clazz, "other", JExpr.cast(clazz, p));
		
		for (FieldDescriptor fd : getIdFields()) {
			JBlock block = method.body().block();
			JFieldRef ref1 = JExpr._this().ref(fd.getRealName());
			JFieldRef ref2 = castedObj.ref(fd.getRealName());
			
			JConditional mainIf = block._if(ref1.eq(JExpr._null()));
			mainIf._then()._if(ref2.ne(JExpr._null()))._then()._return(JExpr.FALSE);
			mainIf._elseif(ref1.invoke("equals").arg(ref2).not())._then()._return(JExpr.FALSE);
		}
		
		method.body()._return(JExpr.TRUE);
    }
	
   
	/**
     * @param clazz
     * @param cm
     */
    private void generateEqualsMethod(JDefinedClass clazz, JCodeModel cm) {
    	final String line = "result = prime * result + ((this.{0} == null) ? 0 : this.{0}.hashCode());";
    	
    	JMethod method = clazz.method(JMod.PUBLIC, cm.INT, "hashCode");
    	method.body().decl(JMod.FINAL, cm.INT, "prime", JExpr.lit(31));
    	JVar res = method.body().decl(cm.INT, "result", JExpr.lit(1));
    	for (FieldDescriptor fd : getIdFields()) {
    		method.body().directStatement(MessageFormat.format(line, fd.getRealName()));
    	}
    	method.body()._return(res);
    }

	/**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
	    StringBuilder builder = new StringBuilder();
	    builder.append("ClassDescriptor [name=");
	    builder.append(name);
	    builder.append(", extendsName=");
	    builder.append(extendsName);
	    builder.append("]");
	    return builder.toString();
    }
}
