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

package de.topicmapslab.codegenerator.descriptors.genny;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassContainer;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

import de.topicmapslab.codegenerator.descriptors.ClassDescriptor;
import de.topicmapslab.codegenerator.descriptors.FieldDescriptor;
import de.topicmapslab.codegenerator.descriptors.PackageDescriptor;

/**
 * The {@link ModelContentProviderDescriptor} is used to describe a ModelContentProvider.
 * This provider has a ModelContainer and returns all instances of a type for a field.
 * 
 * @author Hannes Niederhausen
 * 
 */
public class ModelContentProviderDescriptor extends ClassDescriptor {

	private List<FieldDescriptor> supportedFieldList;

	/**
	 * @param parent
	 */
	public ModelContentProviderDescriptor(PackageDescriptor parent) {
		super(parent);
		setName("ModelContentProvider");
		new FieldDescriptor(this, false, "modelContainer",
		        "de.topicmapslab.genericeditor.application.model.IModelContainer", false);
		addImplementInterface("de.topicmapslab.kuria.swtgenerator.edit.IContentProvider");
	}

	/**
	 * @return the supportedFieldList
	 */
	public List<FieldDescriptor> getSupportedFieldList() {
		if (supportedFieldList == null)
			return Collections.emptyList();
		return supportedFieldList;
	}

	/**
	 * 
	 * Adds a field descriptor which needs a content proposal.
	 * 
	 * @param fd the fielddescriptor
	 */
	public void addSupportedField(FieldDescriptor fd) {
		if (supportedFieldList == null)
			supportedFieldList = new ArrayList<FieldDescriptor>();
		supportedFieldList.add(fd);
	}

	/**
	 * 
	 * Removes a field descriptor which needs a content proposal.
	 * 
	 * @param fd the fielddescriptor
	 */
	public void removeSupportedField(FieldDescriptor fd) {
		if (supportedFieldList != null)
			supportedFieldList.remove(fd);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void generateClass(JClassContainer pack, JCodeModel cm) throws Exception {
		super.generateClass(pack, cm);

		JDefinedClass c = cm._getClass(getQualifiedName());
		JFieldVar mcField = c.fields().get("modelContainer");

		JFieldRef mcRef = JExpr._this().ref(mcField);

		// generate constructor
		JMethod constructor = c.constructor(JMod.PUBLIC);
		constructor.param(mcField.type(), "modelContainer");
		constructor.body().assign(mcRef, mcField);

		/*
		 * @Override public boolean hasContent(String fieldName, Object model) {
		 * return true; }
		 */
		JMethod m = c.method(JMod.PUBLIC, cm.BOOLEAN, "hasContent");
		JVar p1 = m.param(cm.ref(String.class), "fieldName");
		JVar p2 = m.param(cm.ref(Object.class), "model");
		
		for (FieldDescriptor fd : getSupportedFieldList()) {
			JBlock thenBlock = generateFieldAndModelCondition(cm, m, p1, p2, fd);
			thenBlock._return(JExpr.TRUE);
		}
		
		m.body()._return(JExpr.FALSE);

		m = c.method(JMod.PUBLIC, cm.ref(Object.class).array(), "getElements");
		p1 = m.param(cm.ref(String.class), "fieldName");
		p2 = m.param(cm.ref(Object.class), "model");
		for (FieldDescriptor fd : getSupportedFieldList()) {
			JBlock thenBlock = generateFieldAndModelCondition(cm, m, p1, p2, fd);
			JClass fieldType = cm.ref(fd.getType());
			JInvocation methodInvoc = mcRef.invoke("get").arg(fieldType.dotclass());
			thenBlock._return(methodInvoc.invoke("toArray"));
		}
		m.body()._return(JExpr._new(cm.ref(Object.class).array()));
		
		
	}
	
	/**
	 * Creates the checks for the hasContent/getElements methods
	 * 
     * @param cm the codemodel instance
     * @param m the method which gets the ckeck
     * @param p1 the fieldname variable
     * @param p2 the model variable
     * @param fd the fielddescriptor to use
     * @return the then JBlock if the created condition 
     */
    private JBlock generateFieldAndModelCondition(JCodeModel cm, JMethod m, JVar p1, JVar p2, FieldDescriptor fd) {
	    // check if fieldname equals p1
	    String name = fd.getName();
	    if (fd.isMany())
	    	name+="Set";
	    
		JExpression fieldCheck = JExpr.lit(name).invoke("equals").arg(p1);
	    // get the type of the owner of the field
	    JClass fieldOwner = cm.ref(fd.getParent().getQualifiedName());
	    // check if the model is instanceof the owner type
	    JExpression fieldOwnerCheck = p2._instanceof(fieldOwner);
	    // put it together
	    JBlock thenBlock = m.body()._if(fieldCheck.cand(fieldOwnerCheck))._then();
	    return thenBlock;
    }

}
